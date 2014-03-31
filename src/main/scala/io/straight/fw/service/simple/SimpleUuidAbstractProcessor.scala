package io.straight.fw.service.simple

import io.straight.fw.messages.{EventType, CommandType}
import io.straight.fw.model._
import io.straight.fw.service.{AbstractService, UuidRepository}

/**
 * This is so thin we probably don't need it.
 * The abstract type needs top change fropm Abstract to either Either or Scalaz Validation.
 *
 * @author rbuckland
 */
trait SimpleUuidAbstractProcessor[D <: DomainType[Uuid], E <: EventType, C <: CommandType] extends EitherValidationProccessor[D,E,C,Uuid] {
  val repository: UuidRepository[D]
  val idGenerator: UuidGenerator[D]
}

trait SimpleUuidAbstractService[D <: DomainType[Uuid]] extends AbstractService[D,Uuid]{
  override def repository: UuidRepository[D]
}

