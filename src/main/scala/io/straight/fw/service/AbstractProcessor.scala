package io.straight.fw.service

import akka.event.Logging
import io.straight.fw.model._
import scalaz._
import Scalaz._
import akka.persistence.EventsourcedProcessor
import scala.reflect.runtime.universe._
import io.straight.fw.messages.{BaseEvent, BaseCommand}
import scala.reflect.ClassTag
import scalaz.Success
import scalaz.Failure
import scala.Some

/**
 * @author rbuckland
 */
trait AbstractProcessor[T <: BaseDomain[I], E <: BaseEvent, C <: BaseCommand, I <: Any] extends EventsourcedProcessor {

  self ! "ResetPrimaryKeyId"

  /**
   * Our repository (implement with val constructor)
   *
   * I is the ID (or key) for the repository
   * T is the Domain Object (extends from BaseDomain)
   *
   */
  protected def repository: Repository[I,T]

  /**
   * Each time we create a new Domain Object, we need a new ID.
   * We will delegate that responsibility to this fellow.
   *
   * Also, when we recover, we will tell it (after we have finished reocovering)
   * what the current (starting) id now is
   *
   * In theory this idGenerator could be shared amongst (*huh*) multiple Aggregate Actors.
   * ooh test that !  cos I have not got time
   *
   * @return
   */
  protected def idGenerator: IdGenerator[I,T]

  /**
   * Aggregate *ahh fancy*. This is the Base Domain Object classname
   *
   * @param t
   * @return
   */
  def aggregateClassName()(implicit t: ClassTag[T]) = t.runtimeClass.getCanonicalName

  override def receive = initializing.orElse(active)

  def initializing: Receive = {
    case "ResetPrimaryKeyId" =>

      // recovery has finished .. so set the ID on the repo
      idGenerator.setStartingId(repository.maxId)

      unstashAll()
      context.become(active)
    case other if recoveryFinished =>
      stash()
  }

  def active: Receive = super.receive

  protected val log = Logging(context.system, this)

  /**
   * A call to the repository
   */
  private def updateRepository(domainObject: T) = repository.updateMap(domainObject)


  /**
   * Upon recovery we will do some magic and return a domain object
   *
   * @return
   */
  val receiveRecover: Receive = {
    case evt: BaseEvent => {
      updateRepository(eventToDomainObject(evt.asInstanceOf[E]))
    }
    // TODO case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  /**
   * The implementors handler of their event -> that creates a domain object
   * e.g. case e: NewFileAdded => StoredFile(e)
   * or   case e: NameChanged => repository.get(e.uuid).copy(name = e.newName)
   *
   * We will call updateRepository on your behalf
   * (see process())
   * @param event The event that makes a change
   * @return T doaminObject of type T
   */
  def eventToDomainObject(event: E): T

  /**
   * Our magical Process method.
   * @param fvalidate Takes a Command and produces an Event or Validation Error
   * @return nothing (we send out to our actor the new object or the error)
   */

  // TODO implement Command Logging (not sourcing) .. so use this later
  // def process(cmd: C)(fvalidate: => DomainValidation[E]): Unit = {
  def process(fvalidate: => DomainValidation[E]): Unit = {
    // i am sure that there is a better way to do this
    fvalidate match {
      case f @ Failure(error) => sender ! f
      case s @ Success(event) => persist(event) { e =>
         val obj = eventToDomainObject(e)
         updateRepository(obj);
         context.system.eventStream.publish(e) // publish to our subscribers the event we just created
         sender ! obj.success // return the object as a domain validation success back to the sender
      }
    }
  }

}

trait AbstractService[T <: BaseDomain[I], I <: Any] {
  protected def repository: Repository[I,T]
  def get(id: I): Option[T] = repository.getByKey(id)
  def getMap = repository.getMap
  def getAll: Iterable[T] = repository.getValues
}
