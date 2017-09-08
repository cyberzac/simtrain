package actors

import actors.SectionActor.{EnterSection, ExitSection, SectionBlocked, SectionEntered}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import model.{Section, TrainId}

import scala.collection.immutable.Queue

object SectionActor {
  def props(section: Section) = Props(new SectionActor(section))

  case class EnterSection(trainId: TrainId)

  case class ExitSection(trainId: TrainId)

  case class SectionEntered(section: Section)

  case class SectionBlocked(section: Section, trains: Set[TrainId])

}

class SectionActor(section: Section) extends Actor with ActorLogging {

  var trains = Set.empty[TrainId]

  override def preStart(): Unit = {
    context.become(hasCapacity(section.capacity))
  }

  def hasCapacity(free: Int): Receive = {
    case GetStatus ⇒ sender() ! SectionFreeStatus(section, free)

    case EnterSection(trainId) if free > 0 ⇒
      val nextFree = free - 1
      log.debug(s"section:${section.id} train:$trainId enter  ($nextFree/${section.capacity})")
      trains = trains + trainId
      sender() ! SectionEntered(section)
      if (nextFree == 0)
        context.become(blocked(Queue.empty))
      else
        context.become(hasCapacity(nextFree))

    case ExitSection(trainId) ⇒
      val nextFree = free + 1
      log.debug(s"section:${section.id} train:$trainId exited  ($nextFree/${section.capacity})")
      trains = trains - trainId
      context.become(hasCapacity(nextFree))

    case x ⇒ log.error(s"Unexpected $x")
  }

  def blocked(queue: Queue[QueuedTrain]): Receive = {

    case GetStatus ⇒ sender() ! SectionBlockedStatus(section, queue.size)

    case EnterSection(trainId) ⇒
      val newQueue = queue enqueue QueuedTrain(trainId, sender())
      log.debug(s"section:${section.id} train:$trainId queuing (-${newQueue.size}/${section.capacity})")
      sender() ! SectionBlocked(section, trains)
      context.become(blocked(newQueue))

    case ExitSection(trainId) if queue.isEmpty ⇒
      val nextFree = 1
      log.debug(s"section:${section.id} train:$trainId exited ($nextFree/${section.capacity})")
      trains = trains - trainId
      context.become(hasCapacity(nextFree))

    case ExitSection(trainId) ⇒
      val (qt, newQueue) = queue.dequeue
      qt.train ! SectionEntered(section)
      log.debug(s"section:${section.id} train:${qt.trainId} enter  (-${newQueue.size}/${section.capacity})")
      log.debug(s"section:${section.id} train:$trainId exited (-${newQueue.size}/${section.capacity})")
      trains = trains - trainId
      trains = trains + qt.trainId
      context.become(blocked(newQueue))

    case x ⇒ log.error(s"Unexpected $x")
  }

  override def receive = {
    case x ⇒ log.error(s"We should never be here $x")
  }

  case class QueuedTrain(trainId: TrainId, train: ActorRef)

}
