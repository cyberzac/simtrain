import model.TrainSection

package object actors {
  type Time = Long

  case class Tick(time: Time)
  case object GetStatus

  sealed trait Status
  case object NotStarted extends Status
  case class OnSection(section:TrainSection, left:Time) extends Status

}
