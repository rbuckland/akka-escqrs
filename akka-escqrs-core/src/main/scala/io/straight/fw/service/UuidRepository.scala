package io.straight.fw.service

import io.straight.fw.model.{DomainType, Uuid}
import scala.language.reflectiveCalls

class UuidRepository[A <: DomainType[Uuid]] extends Repository[Uuid,A] with Serializable {

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
   * i == numericId
   * x = groupId
   *
   * @return
   */
  def findByIdAndGroup(numericId: Long, groupId: Int): Option[A] = {
    getKeys.find { case Uuid(`numericId`,`groupId`,_) => true } match {
      case Some(id) => getByKey(id)
      case None => None
    }
  }

  def findByOnlyId(numericId: Long): Option[A] = {
    getKeys.find { case Uuid(`numericId`,_,__) => true } match {
      case Some(id) => getByKey(id)
      case None => None
    }
  }

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

  override def maxId: Uuid = getKeys.foldLeft(Uuid(0,0,0)){ (a,b) => if (a.max(b)) a else b }
}