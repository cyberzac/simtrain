package actors

import actors.SectionActor.{EnterSection, ExitSection, SectionBlocked, SectionEntered}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import model.{TrainId, TrainSection}

object TrainActor {

  def props(id: TrainId, start: Time, sections: List[TrainSection]) = Props(new TrainActor(id, start, sections))

}

class TrainActor(id: TrainId, start: Time, sections: List[TrainSection]) extends Actor with ActorLogging {

  var totalBlocked = 0

  override def receive = {
    case GetStatus ⇒ sender() ! NotStarted

    case Tick(time) ⇒
      // log.info(s"train:$id: time is $time, start:$start")
      if (time == start) {
        val section :: tail = sections
        section.sectionActor ! EnterSection(id)
        context.become(waitForEntry(sender(), time, None, section, tail))
      }
      sender() ! Ticked(time, id)

    case x ⇒ log.error(s"Unexpected initial: $x")
  }


  def onSection(left: Time, section: TrainSection, sections: List[TrainSection]): Receive = {

    case GetStatus ⇒ sender() ! OnSection(section, left)

    case Tick(time) if left > 0 ⇒
      log.debug(s"$time train:$id: ${section.sectionId} left $left")
      context.become(onSection(left - 1, section, sections))
      sender() ! Ticked(time, id)

    case Tick(time) if sections.isEmpty ⇒
      log.info(s"$time train:$id reached final destination ${section.sectionId} totalBlocked $totalBlocked")
      context.become(finalDestination(section))
      sender() ! Ticked(time, id)

    case Tick(time) ⇒
      val next :: tail = sections
      log.debug(s"$time train:$id: ask ${section.sectionId} -> ${next.sectionId}")
      next.sectionActor ! EnterSection(id)
      context.become(waitForEntry(sender(), time, Some(section), next, tail))

    case x ⇒ log.error(s"Unexpected onSection: $x")
  }

  def waitForEntry(ticker: ActorRef, time: Time, current: Option[TrainSection], next: TrainSection, tail: List[TrainSection]): Receive = {
    case GetStatus => sender() ! WaitingForEntry(current, next, totalBlocked)

    case SectionEntered(_) =>
      current match {
        case Some(section) ⇒
          log.info(s"$time train:$id: go ${section.sectionId} -> ${next.sectionId}")
          section.sectionActor ! ExitSection(id)
        case None ⇒
          log.info(s"$time train:$id: departure from ${next.sectionId}")

      }
      context.become(onSection(next.time, next, tail))
      ticker ! Ticked(time, id)

    case SectionBlocked(section, trains) ⇒
      totalBlocked += 1
      log.warning(s"$time train:$id: blocked ${toSectionId(current)} -> ${next.sectionId}, trains:${trains.mkString(",")}")
      ticker ! Ticked(time, id)

    case Tick(newTime) =>
      totalBlocked += 1
      log.warning(s"$newTime train:$id: waiting ${toSectionId(current)} -> ${next.sectionId}")
      context.become(waitForEntry(sender(), newTime, current, next, tail))
      sender() ! Ticked(time, id)

    case x ⇒ log.error(s"Unexpected waitForentry: $x")
  }

  private def toSectionId(current: Option[TrainSection]) = {
    (current map (_.sectionId)).getOrElse("")
  }

  def finalDestination(section: TrainSection): Receive = {
    case GetStatus ⇒
      sender() ! FinalDestination(section, totalBlocked)
    case Tick(time) =>
      sender() ! Ticked(time, id)
  }

}
