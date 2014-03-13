package io.straight.fw.service

import akka.event.Logging
import io.straight.fw.model._
import scalaz._
import Scalaz._
import akka.persistence.EventsourcedProcessor
import scala.reflect.runtime.universe._
import io.straight.fw.messages.{BaseEvent, BaseCommand}
import scala.reflect.ClassTag

/**
 * @author rbuckland
 */
trait AbstractProcessor[T <: BaseDomain, E <: BaseEvent, C <: BaseCommand] extends EventsourcedProcessor with UuidGenerator[T] {

  protected val log = Logging(context.system, this)

  /**
   * Our repository (implement with val constructor)
   */
  protected def repository: UuidWithIdRepository[T]

  /**
   * A call to the repository
   */
  private def updateRepository(domainObject: T) = repository.updateMap(domainObject)

  /**
   * Utility method we will use to ensure that the version being modified is the one you expect :-)
   *
   * @param uuid
   * @param expectedVersion
   * @return
   */
  def ensureVersion(uuid: Uuid, expectedVersion: Option[Long])(implicit t: ClassTag[T]): DomainValidation[T] = {
    repository.getByKey(uuid) match {
      case None => DomainError(className + "(%s): does not exist" format uuid).fail
      case Some(domainObject) => domainObject.versionCheck(expectedVersion)
    }
  }

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

trait AbstractService[T <: BaseDomain] {
  protected def repository: UuidWithIdRepository[T]
  def get(uuid: Uuid): Option[T] = repository.getByKey(uuid)
  def getMap = repository.getMap
  def getAll: Iterable[T] = repository.getValues
}
