import actors.{SectionActor, Time}
import akka.actor.ActorRef

package object model {

  type SectionId = Long
  type TrainId = Long

  case class TrainSection(time: Time, sectionActor: ActorRef)

  case class Section(id:SectionId, capacity:Int)
}
