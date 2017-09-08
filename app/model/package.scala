import actors.Time

package object model {

  type SectionId = Long
  type TrainId = Long

  case class TrainSection(time: Time)

  case class Section(id:SectionId, capacity:Int)
}
