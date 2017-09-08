import actors.{SectionActor, Time}
import akka.actor.ActorRef

package object model {

  type SectionId = String
  type TrainId = Long

  case class TrainSection(time: Time, sectionId:SectionId, sectionActor: ActorRef)

  case class Section(id:SectionId, capacity:Int)
}
