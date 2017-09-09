package actors

import actors.SectionActor.{EnterSection, ExitSection, SectionBlocked, SectionEntered}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import model.Section
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.Logger

import scala.concurrent.duration._

class SectionActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
  with BeforeAndAfter
  with Matchers {

  def this() = this(ActorSystem())

  val log: Logger = Logger(this.getClass)
  val startTime = 2

  val train1 = 1
  val train2 = 2
  val train3 = 3
  val train4 = 4
  "A SectionActor" when {

    "has capacity" should {
      "reply with EnteredSection" in {
        val section = Section("Fln-Hno", 2)
        val dut = system.actorOf(SectionActor.props(section))
        dut ! EnterSection(train1)
        expectMsg(SectionEntered(section))
        dut ! EnterSection(train2)
        expectMsg(SectionEntered(section))
      }

    }

    "Keep track of capacity" in {
      val section = Section("Blg-Orn", 2)
      val dut = system.actorOf(SectionActor.props(section))

      dut ! EnterSection(train1)
      dut ! GetStatus
      fishForMessage(200 millis, "free 1") {
        case SectionFreeStatus(`section`, 1) ⇒ true
        case _ ⇒ false
      }

      dut ! EnterSection(train2)
      dut ! GetStatus
      fishForMessage(200 millis, "blocked 0") {
        case SectionBlockedStatus(`section`, 0) ⇒ true
        case _ ⇒ false
      }

      dut ! EnterSection(train3)
      dut ! GetStatus
      fishForMessage(200 millis, "blocked 1") {
        case SectionBlockedStatus(`section`, 1) ⇒ true
        case _ ⇒ false
      }

      dut ! ExitSection(train1)
      dut ! GetStatus
      fishForMessage(200 millis, "blocked 0") {
        case SectionBlockedStatus(`section`, 0) ⇒ true
        case _ ⇒ false
      }

      dut ! ExitSection(train2)
      dut ! GetStatus
      fishForMessage(200 millis, "free 1") {
        case SectionFreeStatus(`section`, 1) ⇒ true
        case _ ⇒ false
      }
    }

        "is blocked" should {
          val section = Section("Orn-Vhy", 1)
          val dut = system.actorOf(SectionActor.props(section))
          "queue EnterRequest until capacity is available " in {
            dut ! EnterSection(train1)
            expectMsg(SectionEntered(section))
            dut ! EnterSection(train2)
            expectMsg(SectionBlocked(section, Set(train1)))
            dut ! EnterSection(train3)
            expectMsg(SectionBlocked(section, Set(train1)))
            dut ! EnterSection(train4)
            expectMsg(SectionBlocked(section, Set(train1)))
            expectNoMsg(500 millis)
            dut ! ExitSection(train1)
            expectMsg(SectionEntered(section))
            dut ! ExitSection(train2)
            expectMsg(SectionEntered(section))
          }
        }

  }

}
