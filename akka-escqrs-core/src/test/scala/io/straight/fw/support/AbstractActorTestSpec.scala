package io.straight.fw.support

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.{ WordSpec, FlatSpecLike, BeforeAndAfterAll }
import org.scalatest.mock.MockitoSugar
import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.ShouldMatchers

abstract class AbstractActorTestSpec(name: String = "unit-test")
    extends TestKit(ActorSystem(name, ConfigFactory.parseString("""
        akka.event-handlers = ["akka.testkit.TestEventListener"]
        |akka.persistence.journal.plugin = "in-memory-journal"
        """.stripMargin)))
    with BaseSpec
    with ImplicitSender {

  implicit val as = this.system

  override def afterAll() {
    system.shutdown()
  }
}
