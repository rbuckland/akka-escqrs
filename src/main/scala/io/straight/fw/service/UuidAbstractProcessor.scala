package io.straight.fw.service

import scala.reflect.ClassTag
import io.straight.fw.messages.{BaseEvent, BaseCommand}
import io.straight.fw.model.{UuidBaseDomain, DomainError, DomainValidation, BaseDomain, Uuid}
import scalaz._
import Scalaz._

/**
 * @author rbuckland
 */
trait UuidAbstractProcessor[T <: UuidBaseDomain, E <: BaseEvent, C <: BaseCommand] extends AbstractProcessor[T,E,C,Uuid] {


  val repository: UuidRepository[T]

  /**
   * Utility method we will use to ensure that the version being modified is the one you expect :-)
   *
   * @param uuid
   * @param expectedVersion
   * @return
   */
  def ensureVersion(uuid: Uuid, expectedVersion: Option[Long])(implicit t: ClassTag[T]): DomainValidation[T] = {
    repository.getByKey(uuid) match {
      case None => DomainError(aggregateClassName + "(%s): does not exist" format uuid).fail
      case Some(domainObject) => domainObject.versionCheck(expectedVersion)
    }
  }

}