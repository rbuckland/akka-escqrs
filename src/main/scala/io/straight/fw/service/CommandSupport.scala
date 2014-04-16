package io.straight.fw.service

import io.straight.fw.model._
import io.straight.fw.messages._

/**
 * Some stackable traits that help you with ES
 * @author rbuckland
 */

object isNotAkkaClass {
  def unapply(cmd: Any): Boolean = cmd.getClass.getCanonicalName.startsWith("akka")
}

/**
 * Log the command coming in, ignore all akka commands
 */
trait CommandLogging[D <: DomainType[I], VD <: AnyRef, VE <: AnyRef, E <: EventType, C <: CommandType, I <: Any]
  extends AbstractProcessor[D,VD,VE,E,C,I] {
   abstract override def commandHandler: Receive = {
     case cmd @ isNotAkkaClass() => log.debug(s"commandHandler(): $cmd"); super.commandHandler(cmd)
     case cmd:Any => super.commandHandler(cmd)
   }
}

/**
 * "Write and forget" command Journalling
 */
trait CommandJournaling { // extends AbstractProcessor {
  /* TODO implement a simple journaller for Commands. (not recovery purposes.. but logging for example)
  abstract override def commandHandler: Receive = {
    case cmd @ isNotAkkaClass() => journal(cmd); super.commandHandler(cmd)
    case cmd:Any => super.commandHandler(cmd)
  }
  */

}
