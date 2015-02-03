package io.straight.escqrs.service.simple

import io.straight.escqrs.messages.{CommandType, EventType}
import io.straight.escqrs.model._
import io.straight.escqrs.model.validation.simple.EitherDomainValidation
import io.straight.escqrs.service.AbstractProcessor
import scala.reflect.ClassTag
import io.straight.escqrs.model.validation.simple.EitherValidationSupport._
import scala.language.reflectiveCalls

/**
 * An AbstractProcessor that uses Either (simple scala) Validation
 *
 * See http://robsscala.blogspot.co.uk/2012/04/validation-without-scalaz.html for explanation
 *
 * @author rbuckland
 */
trait EitherValidationProcessor[D <: DomainType[I], E <: EventType, C <: CommandType, I <: Any]
  extends AbstractProcessor[D, EitherDomainValidation[D], EitherDomainValidation[E], E, C, I] {

  /**
   * Utility method we will use to ensure that the version being modified is the one you expect :-)
   */
  override def ensureVersion(id: I, expectedVersion: Option[Long])(implicit t: ClassTag[D]): EitherDomainValidation[D] = {
    repository.getByKey(id) match {
      case None => DomainError(aggregateClassName + "(%s): does not exist" format id).fail
      case Some(domainObject) => versionCheck(domainObject,expectedVersion)
    }
  }

  override def versionCheck(obj:D, expectedVersion: Option[Long])(implicit t: ClassTag[D]): EitherDomainValidation[D] = {
    expectedVersion match {
      case Some(expected) if obj.version.toLong != expected.toLong => invalidVersion(obj,expected).fail
      case Some(expected) if obj.version.toLong == expected.toLong => obj.succeed
      case None => obj.succeed
    }
  }

  override def toDomainValidationFailure(validation: EitherDomainValidation[E]): EitherDomainValidation[D] = validation.left.get.fail

  override def toDomainValidationSuccess(domainObject: D): EitherDomainValidation[D] = domainObject.succeed

  override def isFailure(validation: EitherDomainValidation[E]): Boolean = validation.isLeft

  override def toEvent(validation: EitherDomainValidation[E]): E = validation.right.get
}
