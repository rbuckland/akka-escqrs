package io.straight.fw.service.view

import akka.persistence.View
import akka.actor.ActorLogging
import akka.persistence.Persistent
import akka.persistence.SnapshotOffer
import io.straight.fw.messages.{CommandType, EventType}
import io.straight.fw.model._
import scala.collection.immutable.TreeMap
import scala.concurrent.stm.Ref
import io.straight.fw.service.Repository

trait AbstractView[D <: DomainType[I], E <: EventType, I <: Any]
  extends View with ActorLogging {
  
  /**
   * Our state (implement with val constructor)
   *
   * I is the ID (or key) for the view
   * D is the Domain Object (extends from BaseDomain)
   *
   */
  //def state: State[I,D]
  def repository: Repository[I,D]
  
  // what do we default these to
  def processorId: String
  def viewId:String 

  val SnapShot = 'Snapshot

  /**
   * A call to the repository
   */
  private def updateRepository(domainObject: D) = repository.updateMap(domainObject)
 // private def updateState(domainObject: D) = state.updateMap(domainObject)
  
  /**
   * The implementors handler of their event -> that creates a domain object
   * e.g. case e: NewFileAdded => StoredFile(e)
   * or   case e: NameChanged => repository.get(e.uuid).copy(name = e.newName)
   *
   * We will call updateState on your behalf
   * (see process())
   * @param event The event that makes a change
   * @return D doaminObject of type D
   */
  def domainObjectFromEvent(event: E): D

  /**
   * 
   * The processorId identifies the processor from which the view receives journaled messages.
   * Views read messages from a processor's journal directly. When a processor is started later 
   * and begins to write new messages, the corresponding view is updated automatically, 
   * by default.
   * 
   * A processor must have an identifier that doesn't change across different actor incarnations. 
   * It defaults to the String representation of processor's path without the address part and 
   * can be obtained via the processorId method.
   * 
   * Overriding processorId is the recommended way to generate stable identifiers.
   * 
   * Provide a unique Processorid to link with the processorid of the Processor.
   * 
   */
  
  def receive = {
    case Persistent(payload, sequenceNr) => { 
      log.debug(s"VIEW:Persistent, payload : $payload, sequencNr: $sequenceNr")
      val obj = domainObjectFromEvent(payload.asInstanceOf[E])
      updateRepository(obj)
      log.debug(s"processorId: $processorId, viewId: $viewId, object = $obj")
    }
    case SnapShot => log.debug("Snapshot received, save snap shot now")
 //     saveSnapshot(state)
    case SnapshotOffer(_, snapshot: TreeMap[I,D]) => log.debug("SnapshotOffer received, snapshot : $snapshot")
  //    state = snapshot
  }

  override def preStart(): Unit = {
    log.info("AbstractView with processorId : $processorId, viewId : $viewId, starting")
    super.preStart()
  }

  override def postStop(): Unit = {
    log.info("AbstractView for processorId: $processorId, viewId : $viewId stopped")
    super.postStop()
  }
    
}