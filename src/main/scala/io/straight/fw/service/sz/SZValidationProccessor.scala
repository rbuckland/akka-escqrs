package io.straight.fw.service.sz

import io.straight.fw.service.AbstractProcessor
import io.straight.fw.messages.{CommandType, EventType}
import io.straight.fw.model.{DomainError, DomainType}
import scalaz._
import Scalaz._
import io.straight.fw.model.validation.sz.SZDomainValidation
import scala.reflect.ClassTag
import scala.language.reflectiveCalls
import scala.Some

/**
 * An AbstractProcessor that uses Scalaz Validation
 *
 * @author rbuckland
 */
trait SZValidationProccessor[D <: DomainType[I], E <: EventType, C <: CommandType, I <: Any]
  extends AbstractProcessor[D, SZDomainValidation[D], SZDomainValidation[E], E, C, I] {

  override def versionCheck(obj:D, expectedVersion: Option[Long])(implicit t: ClassTag[D]): SZDomainValidation[D] = {
    expectedVersion match {
      case Some(expected) if obj.version.toLong != expected.toLong => invalidVersion(obj,expected).fail
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
    validation.toEither.left.get.fail

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
