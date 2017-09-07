package actors

import actors.TrainActor.Tick
import akka.actor.{Actor, ActorLogging, Props}

object TrainActor {
  def props = Props[TrainActor]
  case class Tick(time:Long)
}

class TrainActor extends  Actor with ActorLogging{
  override def receive = {
    case Tick(time) ⇒
      log.info(s" time is $time")
  }
}
