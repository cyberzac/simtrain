package actors

import actors.SectionActor.EnterSection
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
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
  val sectionActor = TestProbe()
  private val section1 = TrainSection(4, sectionActor.ref)
  private val section2 = TrainSection(3, sectionActor.ref)
  val sections = List(section1, section2)

  var globalClock = 0

  "A TrainActor" when {

    "not started" should {
      val trainId = 1
      val dut = system.actorOf(TrainActor.props(trainId, startTime, sections))
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
      val trainId = 2
      val dut = system.actorOf(TrainActor.props(trainId, startTime, sections))
      sendTicks(4, dut)

      "reply with OnSection" in {
        dut ! GetStatus
        expectMsg(OnSection(section1, 2))
      }

      "send enter section" in {
        sendTicks(7,dut)
        sectionActor.expectMsg(EnterSection(trainId))
      }
    }

    "waiting for section" should {
      val trainId = 3
      val dut = system.actorOf(TrainActor.props(trainId, startTime, sections))
      "return status" in {
        sendTicks(8, dut)
        dut ! GetStatus
        expectMsg(WaitingForEntry(section1, section2))
      }
    }
  }


  def sendTicks(to: Time, dut:ActorRef): Unit = {
    while(globalClock < to ){
      globalClock += 1
      dut ! Tick(globalClock)
    }
  }
}
