package controllers

import javax.inject._

import actors.{Tick, Ticked}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(system: ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {

  val log: Logger = Logger(this.getClass)
  implicit val timeout = Timeout(10 seconds)

  val trains = Simulator.createTrains(system)
  for (time ← 0 to 400) {
    val tick = Tick(time)
//    log.info(s"Doing a tick: $tick")

    val futures =  trains map (t ⇒ (t  ? tick).mapTo[Ticked])
    //    val futures: Future[List[Any]] = Future.sequence(Simulator.createTrains(system) map(_ ? tick))
    //
    futures foreach (f ⇒ {
        f onComplete {
          case Success(_) => //log.info(s"$list")
          case Failure(t) => log.error(s"t", t)
        }
      Await.result(f, 10 second)
//      log.info(s"waited $r")
    })
//    waitAll(futures).map( r ⇒ log.info(s"$r"))
  }

  // Await.ready(futures, 10 seconds)

  import scala.util.{Failure, Success}

  private def lift[T](futures: Seq[Future[T]]) =
    futures.map(_.map {
      Success(_)
    }.recover { case t => Failure(t) })

  def waitAll[T](futures: Seq[Future[T]]) =
    Future.sequence(lift(futures)) // having neutralized exception completions through the lifting, .sequence can now be used

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
