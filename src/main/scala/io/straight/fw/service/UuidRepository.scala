package io.straight.fw.service

import io.straight.fw.model.{UuidBaseDomain, Uuid, BaseDomain}

class UuidRepository[A <: UuidBaseDomain] extends Repository[Uuid,A] {

  /**
   * Iterable list objects by a partial match on the UUID
   * @param partialUuid
   * @return
   */
  def filterByPartial(partialUuid: String): Iterable[A] = {
    getValues.filter( x => x.id.toString.contains(partialUuid))
  }

  /**
   * Find the FIRST matched object by partial UUID
   *
   * recall, our UUID's look like
   * iiiiiiii-iiii-6xxx-a000-0000
   * where the i's are the sequential ID
   *
   * So this will just find (in the repo) the objevct where the UUID id matches
   *
   * @return
   */
  def findBySimpleId(numericId: Long): Option[A] = ??? // getKeys.find( uuid => if (uuid.id.equals(numericId)) getByKey(uuid) )

  implicit object ordering extends Ordering[Uuid] {
    def compare(a: Uuid, b: Uuid): Int = {
      if (a.equals(b)) {
        return 0;
      } else if (a.id > b.id) {
        return 1;
      } else {
        return -1;
      }
    }

  }

  override def maxId: Uuid = getKeys.foldLeft(Uuid(-1,-1,-1)){ (a,b) => if (a.max(b)) a else b }
}