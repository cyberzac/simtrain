package controllers

import javax.inject._

import actors.{SectionActor, Tick, TrainActor}
import akka.actor.{ActorRef, ActorSystem}
import model.{Section, TrainSection}
import play.api._
import play.api.mvc._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(system: ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {

  val log: Logger = Logger(this.getClass)
  implicit val timeout = Timeout(10 seconds)

  for (time ← 0 to 10) {
    val tick = Tick(time)
    log.info(s"Doing a tick: $tick")

    val futures: Future[List[Any]] = Future.sequence(Simulator.createTrains(system) map(_ ? tick))

    futures onComplete {
      case Success(list) => {
        log.info(s"$list")
      }
      case Failure(t) => log.error(s"t", t)
    }

   // Await.ready(futures, 10 seconds)
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
