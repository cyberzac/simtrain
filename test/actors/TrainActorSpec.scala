package actors

import actors.SectionActor.{EnterSection, SectionEntered}
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import model.{Section, TrainSection}
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
  val section1 = Section("Orn-Vhy",1)
  val section2 = Section("Blg-Orn",1)
  private val trainSection1 = TrainSection(4, section1.id, sectionActor.ref)
  private val trainSection2 = TrainSection(3, section2.id, sectionActor.ref)
  val trainSections = List(trainSection1, trainSection2)


  var globalClock = 0

  before {
    globalClock = 0
  }

  "A TrainActor" when {

    "not started" should {
      val trainId = 1
      val dut = system.actorOf(TrainActor.props(trainId, startTime, trainSections))
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
      val dut = system.actorOf(TrainActor.props(trainId, startTime, trainSections))
      advanceClockTo(4, dut)

      "reply with OnSection" in {
        dut ! GetStatus
        expectMsg(OnSection(trainSection1, 2))
      }

      "send enter section" in {
        advanceClockTo(7,dut)
        sectionActor.expectMsg(EnterSection(trainId))
      }
    }

    "waiting for section" should {
      val trainId = 3
      val dut = system.actorOf(TrainActor.props(trainId, startTime, trainSections))
      "return status" in {
        advanceClockTo(8, dut)
        dut ! GetStatus
        expectMsg(WaitingForEntry(trainSection1, trainSection2))
      }
    }

    "advance to next section" should {
      val trainId = 3
      val dut = system.actorOf(TrainActor.props(trainId, startTime, trainSections))
      "return status" in {
        advanceClockTo(9, dut)
        dut ! SectionEntered(section2)
        dut ! GetStatus
        expectMsg(OnSection(trainSection2, trainSection2.time))
      }
    }
  }


  def advanceClockTo(to: Time, dut:ActorRef): Unit = {
    while(globalClock < to ){
      globalClock += 1
      dut ! Tick(globalClock)
    }
  }
}
