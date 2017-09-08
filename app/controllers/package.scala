import actors.Time
import model.{SectionId, TrainId}

package object controllers {

  case class TrainSection(time: Time, sectionId:SectionId)

  case class Train(id: TrainId, start: Time, sections: List[TrainSection])
}
