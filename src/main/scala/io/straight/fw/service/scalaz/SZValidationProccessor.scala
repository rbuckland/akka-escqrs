package io.straight.fw.service.scalaz

import io.straight.fw.service.AbstractProcessor
import io.straight.fw.messages.{CommandType, EventType}
import io.straight.fw.model.{DomainError, DomainType}
import scalaz._
import Scalaz._
import io.straight.fw.model.scalaz.DomainValidation
import scala.reflect.ClassTag

/**
 * An AbstractProcessor that uses Scalaz Validation
 *
 * @author rbuckland
 */
trait SZValidationProccessor[D <: DomainType[I], E <: EventType, C <: CommandType, I <: Any] extends AbstractProcessor[D, DomainValidation[D], E, C, I] {
  /**
   * return a success obj from the DomainObject that the event created
   * @param domainObject
   * @return
   */
  override def toSuccess(domainObject: D): DomainValidation[D] = domainObject.success

  private val invalidVersionMessage = "%s(%s): expected version %s doesn't match current version %s"

  def invalidVersion[T:ClassTag](obj:D,expected: Long) =
    DomainError(invalidVersionMessage format(scala.reflect.classTag[T].getClass.getCanonicalName, obj.id, expected, obj.version))

  def versionCheck(obj:D, expectedVersion: Option[Long]): DomainValidation[D] = {
    expectedVersion match {
      case Some(expected) if obj.version.toLong != expected.toLong => invalidVersion(obj,expected).fail
      case Some(expected) if obj.version.toLong == expected.toLong => obj.success
      case None => obj.success
    }
  }
}
