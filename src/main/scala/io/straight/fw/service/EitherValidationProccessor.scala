package io.straight.fw.service

import io.straight.fw.messages.{CommandType, EventType}
import io.straight.fw.model.{DomainType, EitherValidation}
import io.straight.fw.validation.EitherValidationSupport._

/**
 * An AbstractProcessor that uses Scalaz Validation
 *
 * @author rbuckland
 */
trait EitherValidationProccessor[T <: DomainType[I], E <: EventType, C <: CommandType, I <: Any] extends AbstractProcessor[T, EitherValidation[T], E, C, I] {
  /**
   * return a success obj from the DomainObject that the event created
   * @param domainObject
   * @return
   */
  override def toSuccess(domainObject: T): EitherValidation[T] = domainObject.succeed

  /**
   * check if the validation suceeded
   * @param domainValidation
   * @return
   */
  override def isSuccess(domainValidation: EitherValidation[T]): Boolean = domainValidation.isRight

  /**
   * Check if the Validation errored
   * @param eventValidation
   * @return
   */
  override def isFailure(eventValidation: EitherValidation[E]): Boolean = eventValidation.isLeft
}
