package io.straight.fw.service

import io.straight.fw.model._

import akka.persistence.EventsourcedProcessor
import io.straight.fw.messages.{CommandType, EventType}
import scala.reflect.ClassTag
import akka.actor.ActorLogging
import scala.language.reflectiveCalls

/**
 * D is your Domain Class (e.g. Person, Robot, Widget, ShopBooking, CarSale, TreeLoppingAppointment]
 *   Implement a class that looks like DomainType
 * VE is the validation class for Events
 * VD is the validation class for Domain Object
 * E is your EventType - we use a type class here - your messae must implement a { def timestamp: Long }
 * C is your CommandType - we use a type class here - your messae must implement a { def timestamp: Long }
 * I is the ID of your domain class (a Long, or a String, or a Uuid .. we have a sample custom Uuid in this project)
 *
 * @author rbuckland
 */
trait AbstractProcessor[D <: DomainType[I], VD <: AnyRef, VE <: AnyRef, E <: EventType, C <: CommandType, I <: Any] extends EventsourcedProcessor with ActorLogging {

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
   * This is where the commands will come in
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
  def process(fvalidate: => VE): Unit = {
    val validation = fvalidate
    if (isFailure(validation)) {
      sender ! toDomainValidationFailure(validation)
    } else {
      val event = toEvent(validation)
      persist(event) { e =>
        val obj = domainObjectFromEvent(e)
        updateRepository(obj)
        context.system.eventStream.publish(e) // publish to our subscribers the event we just created
        sender ! toDomainValidationSuccess(obj) // return the object as a domain validation success back to the sender
      }
    }
  }

  val invalidVersionMessage = "%s(%s): expected version %s doesn't match current version %s"

  def invalidVersion(obj:D,expected: Long)(implicit t: ClassTag[D]) =
    DomainError(invalidVersionMessage format(t.getClass.getCanonicalName, obj.id, expected, obj.version))

  def versionCheck(obj:D, expectedVersion: Option[Long])(implicit t: ClassTag[D]): VD

  /**
   * return a success of domain object creation
   * scalaz.Validation or EitherValidation knows how to create one of these
   *
   * @param validation for type Event
   * @return validation for type DomainObject
   */
  def toDomainValidationFailure(validation: VE): VD


  /**
   * return a failure Validation for the domainObject
   * scalaz.Validation or EitherValidation knows how to create one of these
   *
   * @param domainObject
   * @return
   */
  def toDomainValidationSuccess(domainObject: D): VD

  /**
   * We will call this to make sure that the command call can create an event
   * (your implementation) either created an Event from the Command or there was
   * an error with the command. We will take this VE and swap it to a VD via
   * the toDomainValidationFailure(validation) method
   *
   * @param validation
   * @return
   */
  def isFailure(validation: VE): Boolean

  /**
   * Given a validation that ha succeeded, return the success (the event)
   * @param validation
   * @return
   */
  def toEvent(validation: VE): E

}
