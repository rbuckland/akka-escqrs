package io.straight.escqrs.service

import io.straight.escqrs.model.DomainType

/**
 * @author rbuckland
 */
trait AbstractService[D <: DomainType[I], I <: Any] {
  def repository: Repository[I,D]
  def get(id: I): Option[D] = repository.getByKey(id)
  def getMap = repository.getMap
  def getAll: Iterable[D] = repository.getValues
  def exists(id: I) = repository.getByKey(id).isDefined
  def fromIdList(ids: Iterable[I]):Iterable[D] = ids.map(id => get(id)).flatten
}
