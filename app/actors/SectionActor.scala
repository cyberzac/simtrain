package actors

import actors.SectionActor.EnterSection
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import model.{Section, TrainId}

object SectionActor {
  def props(sectionId: Section) = Props(new SectionActor(sectionId))

  case class EnterSection(trainId: TrainId, train: ActorRef)

  case class ExitSection(trainId: TrainId, train: ActorRef)

}

class SectionActor(section: Section) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    context.become(free(section.capacity))
  }

  def free(free: Int): Receive = {
    case EnterSection(trainId, self) if free > 0 ⇒
      log.info(s"Enter section $trainId")
  }

  override def receive = {
    case GetStatus ⇒ log.error("We should never be here")
  }


}
