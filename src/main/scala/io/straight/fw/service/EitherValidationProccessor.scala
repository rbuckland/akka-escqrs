package io.straight.fw.service

import _root_.scalaz.{Failure, Success}
import io.straight.fw.messages.{CommandType, EventType}
import io.straight.fw.model._
import io.straight.fw.model.validation.simple.EitherDomainValidation

/**
 * An AbstractProcessor that uses Scalaz Validation
 *
 * @author rbuckland
 */
trait EitherValidationProccessor[D <: DomainType[I], E <: EventType, C <: CommandType, I <: Any] extends AbstractProcessor[D, E, C, I] {


  import io.straight.fw.model.validation.simple.EitherValidationSupport._

  /**
   * return a failure Validation for the domainObject
   * scalaz.Validation or EitherValidation knows how to create one of these
   *
   * @param validation
   * @return
   */
  override def toDomainValidationFailure(validation: ValidationBase[E]): EitherDomainValidation[D] = {
    case Success(event) => // we cannot map from event to DomainValidation[domainObject]
    case Failure(error) => error.fail
  }

  /**
   * return a success of domain object creation
   *
   * @param domainObject
   * @return
   */
  override def toDomainValidationSuccess(domainObject: D): EitherDomainValidation[D] = domainObject.succeed
}
