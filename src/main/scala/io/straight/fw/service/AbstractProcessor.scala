package io.straight.fw.service

import io.straight.fw.model._

import akka.persistence.EventsourcedProcessor
import io.straight.fw.messages.{CommandType, EventType}
import scala.reflect.ClassTag
import akka.actor.ActorLogging
import io.straight.fw.model.scalaz._

/**
 * D is your Domain Class (e.g. Person, Robot, Widget, ShopBooking, CarSale, TreeLoppingAppointment]
 *   Implement a class that looks like DomainType
 * V is your ValidationBase Object that wraps your D and E (Scalaz or Either)  (it must implement isSuccess and isFailure)
 * E is your EventType - we use a type class here - your messae must implement a { def timestamp: Long }
 * C is your CommandType - we use a type class here - your messae must implement a { def timestamp: Long }
 * I is the ID of your domain class (a Long, or a String, or a Uuid .. we have a sample custom Uuid in this project)
 *
 * @author rbuckland
 */
trait AbstractProcessor[D <: DomainType[I], V <: ValidationBase, E <: EventType, C <: CommandType, I <: Any] extends EventsourcedProcessor with ActorLogging {

  self ! "ResetPrimaryKeyId"

  /**
   * Our repository (implement with val constructor)
   *
   * I is the ID (or key) for the repository
   * D is the Domain Object (extends from BaseDomain)
   *
   */
  def repository: Repository[I,D]

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
  def idGenerator: IdGenerator[I,D]

  /**
   * Aggregate *ahh fancy*. This is the Base Domain Object classname
   *
   * @param t
   * @return
   */
  def aggregateClassName()(implicit t: ClassTag[D]) = t.runtimeClass.getCanonicalName

  /**
   * This is where the commands wilkl come in
   * @return
   */
  def processCommand: Receive


  /**
   * We need to hijack the receiveCommand so we can capture a recovery state
   * @return
   */
  override def receiveCommand = initializing.orElse(active)

  /**
   * If we are initializing, (and now receiving our messages)
   * @return
   */
  def initializing: Receive = {

    case "ResetPrimaryKeyId" =>

      log.debug("Recovery is Complete - " + repository.getKeys.size + " objects loaded. Will now reset key ID")
      // recovery has finished .. so set the ID on the repo
      idGenerator.setStartingId(repository.maxId)
      log.debug("Repository Key Reset - Next ID is: " + idGenerator.potentialNextId)

      unstashAll()

      context.become(active)

    case other if recoveryFinished =>
      stash()

  }

  def active: Receive = processCommand

  /**
   * A call to the repository
   */
  private def updateRepository(domainObject: D) = repository.updateMap(domainObject)


  /**
   * Upon recovery we will do some magic and return a domain object
   *
   * @return
   */
  val receiveRecover: Receive = {
    case evt: EventType => {
      updateRepository(domainObjectFromEvent(evt.asInstanceOf[E]))
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
   * @return D doaminObject of type D
   */
  def domainObjectFromEvent(event: E): D

  /**
   * Our magical Process method.
   * @param fvalidate Takes a Command and produces an Event or Validation Error
   * @return nothing (we send out to our actor the new object or the error)
   */

  // TODO implement Command Logging (not sourcing) .. so use this later
  def process(fvalidate: => V[E]): Unit = {
    // i am sure that there is a better way to do this
    fvalidate match {
      case error @ _ if isFailure(error) => sender ! error
      case event @ _ if isSuccess(event) => persist(event) { e =>
         val obj = domainObjectFromEvent(e)
         updateRepository(obj)
         context.system.eventStream.publish(e) // publish to our subscribers the event we just created
         sender ! toSuccess(obj) // return the object as a domain validation success back to the sender
      }
    }
  }

  val invalidVersionMessage = "%s(%s): expected version %s doesn't match current version %s"

  def invalidVersion(obj:D,expected: Long)(implicit t: ClassTag[D]) =
    DomainError(invalidVersionMessage format(t.getClass.getCanonicalName, obj.id, expected, obj.version))

  def versionCheck(obj:D, expectedVersion: Option[Long])(implicit t: ClassTag[D]): V

  /**
   * Check if the Validation failed (validation that cmd generates an event)
   * @param eventValidation
   * @return
   */
  def isFailure(eventValidation: V): Boolean = eventValidation.isFailure

  /**
   * check if the validation succeeded (validation that cmd generates an event)
   * @param eventValidation
   * @return
   */
  def isSuccess(eventValidation: V): Boolean  = eventValidation.isFailure

  /**
   * return a success of
   * @param domainObject
   * @return
   */
  def toSuccess(domainObject: D): V[D]

}


