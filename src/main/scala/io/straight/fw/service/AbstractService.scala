package io.straight.fw.service

import io.straight.fw.model.DomainType

/**
 * @author rbuckland
 */
trait AbstractService[T <: DomainType[I], I <: Any] {
  def repository: Repository[I,T]
  def get(id: I): Option[T] = repository.getByKey(id)
  def getMap = repository.getMap
  def getAll: Iterable[T] = repository.getValues
  def exists(id: I) = repository.getByKey(id).isDefined
}
