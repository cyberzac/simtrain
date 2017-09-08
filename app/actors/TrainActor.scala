package actors

import akka.actor.{Actor, ActorLogging, Props}

object TrainActor {

  def props(start: Time, sections: List[Section]) = Props(new TrainActor(start: Time, sections: List[Section]))

}

case class Section(time: Time)

class TrainActor(start: Time, sections: List[Section]) extends Actor with ActorLogging {
  override def receive = {
    case GetStatus ⇒ sender() ! NotStarted
    case Tick(time) ⇒
      log.info(s" time is $time, start:$start")
      if (time == start) {
        val section :: tail = sections
        log.info(s"next section $section")
        context.become(onSection(section.time, section, tail))
      }
  }


  def onSection(left: Time, section: Section, sections: List[Section]): Receive = {

    case GetStatus ⇒ sender() ! OnSection(section, left)
    case Tick(time) if left > 0 ⇒
      log.info(s"Time left $left")
      context.become(onSection(left - 1, section, sections))
    case Tick(time) ⇒
      log.info(s"Time to change section: $sections")
  }
}
