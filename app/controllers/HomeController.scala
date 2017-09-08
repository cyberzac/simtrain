package controllers

import javax.inject._

import actors.{SectionActor, Tick, TrainActor}
import akka.actor.{ActorRef, ActorSystem}
import model.{Section, TrainSection}
import play.api._
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(system: ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {

  val log: Logger = Logger(this.getClass)
  val sectionOne = system.actorOf(SectionActor.props(Section(1, 2)))
  val sectionTwo = system.actorOf(SectionActor.props(Section(2, 2)))

  private val train: ActorRef = system.actorOf(TrainActor.props(4711, 3, List(TrainSection(2, sectionOne), TrainSection(1, sectionTwo))))

  for (time ← 0 to 10) {
    val tick = Tick(time)
    log.info(s"Sending $tick")
    train ! tick
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
