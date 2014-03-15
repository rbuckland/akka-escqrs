package io.straight.fw.model

import scala.reflect.ClassTag

/**
 * @author rbuckland
 */
trait IdGenerator[I <: Any,T <: BaseDomain[I]] {

  /**
   * Method to create a new Id
   * @return
   */
  def newId():I


  /**
   * Upon actor replay, this method will be called
   *
   * Class tag because you may want to know what the type of T is in order to generate
   * the correct ID
   *
   * @param id
   */
  def setStartingId(id: I): Unit

  /**
   * Return the potential Next Id - of course there is no guarantees that ca call to
   * newId will give ytou this .. it is just helpful for debugging purposes. (logging etc)
   * @return
   */
  def potentialNextId: I

}
