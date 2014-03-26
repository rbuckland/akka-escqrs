package io.straight.fw

import org.joda.time.DateTime

/**
 * Our processors expect to see a command that looks like an EventType and a CommandType,
 * Really the only thing we want on either of them is the timestamp.. to help you (the implementor)
 * as the message will have the timestamp it was created, when serialised.
 *
 * Implementation of these command and event messages is per an aggregate root (DDD.. )
 * per a "Domain Type (eg Person) Processor"
 *
 * For example:
 *
 * abstract class PersonNewCommand(val timestamp: DateTime) extends CommandType
 * abstract class PersonChangeCommand(val id: Uuid, val expectedVersion: Long, val timestamp: DateTime) extends CommandType
 * abstract class PersonNewEvent(val id: Uuid, val timestamp: DateTime) extends EventType
 * abstract class PersonChangeEvent(val id: Uuid, expectedVersion: Long, timestamp: DateTime) extends EventType
 *
 * @author rbuckland
 */
package object messages {

   type MessageType = AnyRef{def timestamp: DateTime}
   type EventType = MessageType
   type CommandType = MessageType

}

