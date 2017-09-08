package actors

import actors.SectionActor.{EnterSection, SectionBlocked, SectionEntered}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import model.{TrainId, TrainSection}

object TrainActor {

  def props(id: TrainId, start: Time, sections: List[TrainSection]) = Props(new TrainActor(id, start, sections))

}

class TrainActor(id: TrainId, start: Time, sections: List[TrainSection]) extends Actor with ActorLogging {
  override def receive = {
    case GetStatus ⇒ sender() ! NotStarted
    case Tick(time) ⇒
      // log.info(s"train:$id: time is $time, start:$start")
      if (time == start) {
        val section :: tail = sections
        log.info(s"$time train:$id: departure from ${section.sectionId}")
        context.become(onSection(section.time, section, tail))
      }
      sender() ! Ticked
  }


  def onSection(left: Time, section: TrainSection, sections: List[TrainSection]): Receive = {

    case GetStatus ⇒ sender() ! OnSection(section, left)

    case Tick(time) if left > 0 ⇒
      log.info(s"$time train:$id: ${section.sectionId} left $left")
      context.become(onSection(left - 1, section, sections))
      sender() ! Ticked

    case Tick(time) if sections.isEmpty ⇒
      log.info(s"$time train:$id reached final destination ${section.sectionId}")
      context.become(finalDestination(section))
      sender() ! Ticked

    case Tick(time) ⇒
      val next :: tail = sections
      log.info(s"$time train:$id: ask ${section.sectionId} -> ${next.sectionId}")
      next.sectionActor ! EnterSection(id)
      context.become(waitForEntry(sender(), time, section, next, tail))
  }

  def waitForEntry(ticker:ActorRef, time:Time, current: TrainSection, next: TrainSection, tail: List[TrainSection]): Receive = {
    case GetStatus => sender() ! WaitingForEntry(current, next)

    case SectionEntered(_) =>
      log.info(s"$time train:$id: go ${current.sectionId} -> ${next.sectionId}")
      context.become(onSection(next.time, next, tail))
      ticker! Ticked

    case SectionBlocked(_) ⇒
      log.info(s"$time train:$id: blocked ${current.sectionId} -> ${next.sectionId}")
      ticker ! Ticked

    case Tick(newTime) =>
      log.info(s"$newTime train:$id: waiting for ${current.sectionId} -> ${next.sectionId}")
      context.become(waitForEntry(sender(), newTime, current, next, tail))
      sender() ! Ticked
  }

  def finalDestination(section: TrainSection): Receive = {
    case GetStatus ⇒ sender() ! FinalDestination(section)
    case Tick(_) => sender() ! Ticked
  }

}
