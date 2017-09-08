package controllers

import actors.{SectionActor, TrainActor}
import akka.actor.ActorSystem
import model.{TrainSection => MTrainSection}

/**
  * Created by pontus on 2017-09-08.
  */
object Simulator {

  def createTrains(system:ActorSystem) = {

    val sectionMap = Sections.sections map(s => s.id -> system.actorOf(SectionActor.props(s))) toMap

    Trains.trains map(t => {

      val sections = t.sections map(tc => {
        val actorRef = sectionMap.get(tc.sectionId).get
        MTrainSection(tc.time, tc.sectionId, actorRef)
      })


      system.actorOf(TrainActor.props(t.id, t.start, sections))
    })
  }
}
