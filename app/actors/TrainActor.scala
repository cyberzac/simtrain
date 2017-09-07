package actors

import actors.TrainActor.{Tick, Time}
import akka.actor.{Actor, ActorLogging, Props}

object TrainActor {
  type Time = Long

  def props(start: Time, sections: List[Section]) = Props(new TrainActor(start: Time, sections: List[Section]))

  case class Tick(time: Time)

}

case class Section(time: Time)

class TrainActor(start: Time, sections: List[Section]) extends Actor with ActorLogging {
  override def receive = {
    case Tick(time) ⇒
      log.info(s" time is $time, start:$start")
      if (time == start) {
        val section :: tail = sections
        log.info(s"next section $section")
        context.become(onSection(section.time, tail))
      }
  }


  def onSection(left: Time, sections: List[Section]): Receive = {
    case Tick(time) if left > 0 ⇒
      log.info(s"Time left $left")
      context.become(onSection(left - 1, sections))
    case Tick(time) ⇒
      log.info(s"Time to change section: $sections")
  }
}
