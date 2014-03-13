package io.straight.fw.service

import io.straight.fw.model.Uuid
import io.straight.fw.model.BaseDomain

class UuidWithIdRepository[A <: BaseDomain] extends Repository[Uuid,A]( {(x: A) => x.uuid } ) {

  /**
   * Iterable list objects by a partial match on the UUID
   * @param partialUuid
   * @return
   */
  def filterByPartial(partialUuid: String): Iterable[A] = {
    getValues.filter( x => x.uuid.uuid.contains(partialUuid))
  }

  /**
   * Find the FIRST matched object by partial UUID
   * @param partialUuid
   * @return
   */
  def findByPartial(partialUuid: String): Option[A] = {
    getValues.find( x => x.uuid.uuid.contains(partialUuid))
  }

  implicit object ordering extends Ordering[Uuid] {
    def compare(a: Uuid, b: Uuid): Int = {
      if (a.uuid.equals(b.uuid)) {
        return 0;
      } else if (a.uuid > b.uuid) {
        return 1;
      } else {
        return -1;
      }
    }

  }
}