import model.{Section, TrainSection}

package object actors {
  type Time = Long

  case class Tick(time: Time)
  case object GetStatus
  case object Ticked

  sealed trait Status
  case object NotStarted extends Status
  case class OnSection(section:TrainSection, left:Time) extends Status
  case class FinalDestination(section:TrainSection) extends Status

  sealed trait SectionStatus
  case class SectionFreeStatus(section:Section, free:Int) extends SectionStatus
  case class SectionBlockedStatus(section:Section, queue:Int) extends SectionStatus

  case class WaitingForEntry(current: TrainSection, next: TrainSection) extends Status
}
