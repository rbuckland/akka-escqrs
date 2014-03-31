package io.straight.fw.service

import scala.reflect.ClassTag
import io.straight.fw.messages.{EventType, CommandType}
import io.straight.fw.model._
import scalaz._
import Scalaz._
import scala.Some

/**
 * @author rbuckland
 */
trait UuidAbstractProcessor[T <: DomainType[Uuid], E <: EventType, C <: CommandType] extends AbstractProcessor[T,E,C,Uuid] {


  val repository: UuidRepository[T]

  val idGenerator: UuidGenerator[T]


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

trait UuidAbstractService[T <: UuidBaseDomain] extends AbstractService[T,Uuid]{
  override def repository: UuidRepository[T]
}

