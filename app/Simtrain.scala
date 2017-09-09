import actors.{Tick, Ticked}
import akka.pattern.ask
import akka.actor.ActorSystem
import akka.util.Timeout
import controllers.Simulator
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.util.{Failure, Success}
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Simtrain extends App {

  private val system = ActorSystem.create("simtrain")
  implicit val timeout = Timeout(10 seconds)
  val log = LoggerFactory.getLogger("simtrain")


  val trains = Simulator.createTrains(system)
  for (time ← 0 to 60 * 24 * 2) {
    val tick = Tick(time)
    //    log.info(s"Doing a tick: $tick")
    val futures = trains map (t ⇒ (t ? tick).mapTo[Ticked])
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


}