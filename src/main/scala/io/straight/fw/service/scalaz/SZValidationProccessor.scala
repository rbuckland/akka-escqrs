package io.straight.fw.service.scalaz

import io.straight.fw.service.AbstractProcessor
import io.straight.fw.messages.{CommandType, EventType}
import io.straight.fw.model.{ValidationBase, DomainError, DomainType}
import scalaz._
import Scalaz._
import io.straight.fw.model.validation.scalaz.DomainValidation
import scala.reflect.ClassTag

/**
 * An AbstractProcessor that uses Scalaz Validation
 *
 * @author rbuckland
 */
trait SZValidationProccessor[D <: DomainType[I], E <: EventType, C <: CommandType, I <: Any] extends AbstractProcessor[D, E, C, I] {

  private val invalidVersionMessage = "%s(%s): expected version %s doesn't match current version %s"

  def invalidVersion[T:ClassTag](obj:D,expected: Long) =
    DomainError(invalidVersionMessage format(scala.reflect.classTag[T].getClass.getCanonicalName, obj.id, expected, obj.version))

  override def versionCheck(obj:D, expectedVersion: Option[Long])(implicit t: ClassTag[D]): DomainValidation[D] = {
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
  override def toDomainValidationFailure(validation: ValidationBase[E]): DomainValidation[D] = {
    case Success(event) => // we cannot map from event to DomainValidation[domainObject]
    case Failure(error) => error.fail
  }

  /**
   * return a success of domain object creation
   *
   * @param domainObject
   * @return
   */
  override def toDomainValidationSuccess(domainObject: D): DomainValidation[D] = domainObject.success
}
