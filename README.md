io-straight-fw
==============

straight.io - akka-persistence + spray.io
=======

straight.io Framework

akka-persistence and DDD
This support library is a buch of support classes that will help you build an awesome 

CQRS + Event Sourced + DDD base application using akka-persistence event sourcing.
There are support modules for spray.io and (un)Marshalling so youi don't have to think as well. (currently these are embedded in).

Supported Versions are 

spray.io : 1.3.1
akka-persistence : 2.3.0
akka : 2.3.0
Scala : 2.10.3

And a bunch of other dependencies to boot!

# Overview

This is a simple set of helper classes for building web apps with
- akka-persistence
- spray.io
  - jackson marshalling and unmarshalling
- a neat UUID repository implementation

# TODO

- (See the Issues list for more)
- Create a whole sample that is using the Domain architecture

# Some Samples

## Using a repository (your memory Image)
```scala
/**
 * The Service in injected with effectively Read-Only access to the memory Image
 *
 * If you have special "ways" to interrogate your Service, then put them here
 *
 * @author rbuckland
 */
class ConnectGroupService()
                         (implicit val bindingModule: BindingModule)
  extends UuidAbstractService[ConnectGroup]
  with AutoInjectable {

  val repository: UuidRepository[ConnectGroup] = inject[UuidRepository[ConnectGroup]]

}
```

## The Actor akka-persistence Processor
```scala
package com.soqqo.luap.service.connectgroup

import com.soqqo.luap.model.connectgroup._
import com.soqqo.luap.model.person._
import akka.actor.{Props, actorRef2Scala}
import scalaz._
import Scalaz._
import com.soqqo.luap.service.person.PersonService

import com.soqqo.luap.messages._
import com.soqqo.luap.messages.CreateConnectGroup
import com.escalatesoft.subcut.inject.{AutoInjectable, BindingModule}
import io.straight.fw.service.{UuidAbstractService, UuidRepository}
import io.straight.fw.model.{Uuid, DomainError, UuidGenerator}
import io.straight.fw.akka.ActorSupport
import io.straight.fw.service.sz.SZValidationUuidAbstractProcessor
import io.straight.fw.model.validation.sz.SZDomainValidation


/**
 * ConnectGroupProcessor
 */
class ConnectGroupProcessor()
                           (implicit val bindingModule: BindingModule)
  extends SZValidationUuidAbstractProcessor[ConnectGroup, ConnectGroupEvent, ConnectGroupCommand]
  with AutoInjectable {

  val repository = inject[UuidRepository[ConnectGroup]]
  val personService = inject[PersonService]
  val idGenerator = inject[UuidGenerator[ConnectGroup]]

  /**
   * This magic method will create a ConnectGroup given an Event.
   * The AbstractProcessor calls this method on our behalf inside the persist(event) {  domainObjectFromEvent(event) }
   *
   * remember, although the event is "past tense" (eg ConnectGroupCreated) the order is
   *
   * cmd --> event --> objectCreated  .. and not
   * cmd --> objectCreated --> event
   *
   * If you look at the "ConnectGroupProcessor" as a Black Box.. a Command went in and an Event Came out
   * So .. the past Tense is for the external "looker" of this Processor, not so much us.
   *
   * We just need to know how to create a ConnectGroup, given the event (because we are event sourced)
   *
   * @param event
   * @return
   */
  override def domainObjectFromEvent(event: ConnectGroupEvent): ConnectGroup = {
    case evt: ConnectGroupCreated => ConnectGroup(evt)
    case evt: ConnectGroupNameChanged => repository.getByKey(evt.id).get.changeName(evt.name)
  }
  /**
   *
   * @return list of people
   */
  def getLeaders(leaders: List[Uuid]): SZDomainValidation[List[Person]] =
    (for (
      uuid <- leaders;
      person <- personService.get(uuid)
    ) yield person).toList.success


  // command to event
  val processCommand: Receive = {
    case cmd@CreateConnectGroup(timestamp, leaders, connectGroupName) => {
      for {
        leaders <- getLeaders(leaders)
      } yield ConnectGroup.canCreate(idGenerator.newId, cmd, leaders)
    }

    case cmd@ChangeConnectGroupName(timestamp, connectGroupId, expectedVersion, newName) => {
      for {
        obj <- ensureVersion(connectGroupId, cmd.expectedVersion)
        event <- ConnectGroup.canChangeName(obj, cmd)
      } yield event
    }

  }

}
```
