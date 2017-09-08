package actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import model.TrainSection
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.Logger

class TrainActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
  with BeforeAndAfter
  with Matchers {

  def this() = this(ActorSystem())

  val log: Logger = Logger(this.getClass)
  val startTime = 2
  private val section1 = TrainSection(4)
  private val section2 = TrainSection(3)
  val sections = List(section1, section2)
  val trainId = 4711

  var dut: ActorRef = _

  before {
    dut = system.actorOf(TrainActor.props(trainId, startTime, sections))
  }

  "A TrainActor" when {

    "not started" should {

      "reply with NotStarted at time 0" in {
        dut ! GetStatus
        expectMsg(NotStarted)
      }
      "reply with NotStarted at time 1" in {
        dut ! Tick(0)
        dut ! GetStatus
        expectMsg(NotStarted)
      }


    }

    "on section" should {
      "reply with OnSection" in {
        sendTicks(0, 2)
        dut ! GetStatus
        expectMsg(OnSection(section1, 4))
      }

    }
  }


  def sendTicks(from: Time, to: Time): Unit = {
    for (time ← from until to +1) dut ! Tick(time)
  }
}
