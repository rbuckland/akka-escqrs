package io.straight.escqrs

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
 * abstract class PersonNewCommand(val timestamp: Long)
 * abstract class PersonChangeCommand(val id: Uuid, val expectedVersion: Long, val timestamp: Long)
 * abstract class PersonNewEvent(val id: Uuid, val timestamp: Long)
 * abstract class PersonChangeEvent(val id: Uuid, expectedVersion: Long, timestamp: Long)
 *
 * Because the EventType and CommandType are type classes expecting something will implement
 * timestamp, we don't need out base command and events to extend anything from io.straight.escqrs
 *
 * @author rbuckland
 */
package object messages {

   type MessageType = AnyRef{def timestamp: Long}  // seconds since 1 Jan 1970 GMT etc
   type EventType = MessageType
   type CommandType = MessageType

}

