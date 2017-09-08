package actors

import actors.SectionActor.{EnterSection, ExitSection, SectionEntered}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import model.{Section, TrainId}

import scala.collection.immutable.Queue

object SectionActor {
  def props(section: Section) = Props(new SectionActor(section))

  case class EnterSection(trainId: TrainId)

  case class ExitSection(trainId: TrainId)

  case class SectionEntered(section: Section)
}

class SectionActor(section: Section) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    context.become(hasCapacity(section.capacity))
  }

  def hasCapacity(free: Int): Receive = {
    case GetStatus ⇒ sender() ! SectionFreeStatus(section, free)

    case EnterSection(trainId) if free > 0 ⇒
      val nextFree = free - 1
      log.info(s"train:$trainId entered section:${section.id} ($nextFree/${section.capacity})")
      sender() ! SectionEntered(section)
      if (nextFree == 0)
        context.become(blocked(Queue.empty))
      else
        context.become(hasCapacity(nextFree))

    case ExitSection(trainId) ⇒
      val nextFree = free + 1
      log.info(s"train:$trainId exited section:${section.id} ($nextFree/${section.capacity})")
      context.become(hasCapacity(nextFree))

    case x ⇒ log.error(s"Unexpected $x")
  }

  def blocked(queue: Queue[QueuedTrain]): Receive = {

    case GetStatus ⇒ sender() ! SectionBlockedStatus(section, queue.size)

    case EnterSection(trainId) ⇒
      log.info(s"section:${section.id} queuing train:$trainId")
      context.become(blocked(queue enqueue QueuedTrain(trainId, sender())))

    case ExitSection(trainId) if queue.isEmpty ⇒
      val nextFree = 1
      log.info(s"train:$trainId exited section:${section.id} ($nextFree/${section.capacity})")
      context.become(hasCapacity(nextFree))

    case ExitSection(trainId) ⇒
      val (qt, newQueue) = queue.dequeue
      qt.train ! SectionEntered(section)
      log.info(s"train:${qt.trainId} exited section:${section.id} (-${queue.size}/${section.capacity})")
      context.become(blocked(newQueue))

    case x ⇒ log.error(s"Unexpected $x")
  }

  override def receive = {
    case x ⇒ log.error(s"We should never be here $x")
  }

  case class QueuedTrain(trainId: TrainId, train: ActorRef)

}
