package io.straight.escqrs.model

/**
 * @author rbuckland
 */
trait IdGenerator[I <: Any,T <: DomainType[I]] {

  /**
   * Method to create a new Id

   * @return
   */
  def newId():I


  /**
   * Upon actor replay, this method will be called
   *
   * @param id
   */
  def setStartingId(id: I): Unit

  /**
   * Return the potential Next Id - of course there is no guarantees that a call to
   * newId will give you this .. it is just helpful for debugging purposes. (logging etc)
   * @return
   */
  def potentialNextId: I

}
