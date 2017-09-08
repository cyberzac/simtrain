package controllers

import javax.inject._

import actors.{SectionActor, Tick, TrainActor}
import akka.actor.{ActorRef, ActorSystem}
import model.{Section, TrainSection}
import play.api._
import play.api.mvc._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import model.{TrainSection ⇒ MTrainSection}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(system: ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {

  val log: Logger = Logger(this.getClass)
  private val section1 = Section("Blg-Orn", 2)
  val sectionOneActor = system.actorOf(SectionActor.props(section1))
  private val section2 = Section("Orn-Vhy", 2)
  val sectionTwoActor = system.actorOf(SectionActor.props(section2))

  private val train: ActorRef = system.actorOf(TrainActor.props(4711, 3,
    List(MTrainSection(2, section1.id, sectionOneActor),
      M TrainSection(3, section2.id, sectionTwoActor))))

  implicit val timeout = Timeout(10 seconds)

  for (time ← 0 to 10) {
    val tick = Tick(time)
    val fTicked = train ? tick
    Await.ready(fTicked, 10 seconds)
  }

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
