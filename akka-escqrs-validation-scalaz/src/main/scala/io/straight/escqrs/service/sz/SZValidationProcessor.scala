package io.straight.escqrs.service.sz

import io.straight.escqrs.service.AbstractProcessor
import io.straight.escqrs.messages.{CommandType, EventType}
import io.straight.escqrs.model.{DomainError, DomainType}
import scalaz._
import Scalaz._
import io.straight.escqrs.model.validation.sz.SZDomainValidation
import scala.reflect.ClassTag
import scala.language.reflectiveCalls

/**
 * An AbstractProcessor that uses Scalaz Validation
 *
 * @author rbuckland
 */
trait SZValidationProcessor[D <: DomainType[I], E <: EventType, C <: CommandType, I <: Any]
  extends AbstractProcessor[D, SZDomainValidation[D], SZDomainValidation[E], E, C, I] {

  /**
   * Utility method we will use to ensure that the version being modified is the one you expect :-)
   */
  override def ensureVersion(id: I, expectedVersion: Option[Long])(implicit t: ClassTag[D]): SZDomainValidation[D] = {
    repository.getByKey(id) match {
      case None => DomainError(aggregateClassName + "(%s): does not exist" format id).failure
      case Some(domainObject) => versionCheck(domainObject,expectedVersion)
    }
  }

  override def versionCheck(obj:D, expectedVersion: Option[Long])(implicit t: ClassTag[D]): SZDomainValidation[D] = {
    expectedVersion match {
      case Some(expected) if obj.version.toLong != expected.toLong => invalidVersion(obj,expected).failure
      case Some(expected) if obj.version.toLong == expected.toLong => obj.success
      case None => obj.success
    }
  }

  /**
   * return a failure Validation for the domainObject
   * scalaz.Validation or EitherValidation knows how to create one of these
   *
   * @param validation
   * @return
   */
  override def toDomainValidationFailure(validation: SZDomainValidation[E]): SZDomainValidation[D] =
    validation.toEither.left.get.failure

  /**
   * return a success of domain object creation
   *
   * @param domainObject
   * @return
   */
  override def toDomainValidationSuccess(domainObject: D): SZDomainValidation[D] = domainObject.success

  override def isFailure(validation: SZDomainValidation[E]): Boolean = validation.isFailure

  override def toEvent(validation: SZDomainValidation[E]): E = validation.toEither.right.get
}
