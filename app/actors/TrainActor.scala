package actors

import actors.SectionActor.EnterSection
import akka.actor.{Actor, ActorLogging, Props}
import model.{TrainId, TrainSection}

object TrainActor {

  def props(id:TrainId, start: Time, sections: List[TrainSection]) = Props(new TrainActor(id, start, sections))

}

class TrainActor(id:TrainId, start: Time, sections: List[TrainSection]) extends Actor with ActorLogging {
  override def receive = {
    case GetStatus ⇒ sender() ! NotStarted
    case Tick(time) ⇒
      log.info(s"train:$id: time is $time, start:$start")
      if (time == start) {
        val section :: tail = sections
        log.info(s"train:$id: next section $section")
        context.become(onSection(section.time, section, tail))
      }
  }


  def onSection(left: Time, section: TrainSection, sections: List[TrainSection]): Receive = {

    case GetStatus ⇒ sender() ! OnSection(section, left)

    case Tick(time) if left > 0 ⇒
      log.info(s"train:$id: time left $left")
      context.become(onSection(left - 1, section, sections))
    case Tick(time) ⇒
      val next :: tail = sections
      next.sectionActor ! EnterSection(id)
      log.info(s"train:$id: time to change section $section -> $next")
      context.become(waitForEntry(section, next, tail))
  }

  def waitForEntry(section: TrainSection, next: TrainSection, sections: List[TrainSection]) : Receive = {
    case GetStatus => sender() ! WaitingForEntry(section, next)
  }

}
