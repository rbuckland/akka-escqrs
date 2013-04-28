/*
 * Copyright (C) 2013 soqqo ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.straight.service

import akka.actor.{ Actor, ActorRef, ActorSystem }
import akka.pattern.ask
import akka.event.Logging
import io.straight.model._
import scalaz._
import scalaz.Scalaz._

trait AbstractProcessor[T] extends Actor {

  val log = Logging(context.system, this)
  
  def klass: Class[_]
  
  def className = klass.getCanonicalName()

  def repository: Repository[Uuid, T]

  /**
   * calls the function f: (on the domain object, some method) that returns a DomainValidation
   * If the object (keyed by uuid) was not there, then you get an error
   */
  def updatWithValidationCheck[B <: T](uuid: Uuid, expectedVersion: Option[Long])(f: T => DomainValidation[B], versionCheck: (T,Option[Long]) => DomainValidation[T]): DomainValidation[B] =
    repository.getByKey(uuid) match {
      case None => DomainError(className + "(%s): does not exist" format uuid).fail
      case Some(domainObject) => for {
        current <- versionCheck(domainObject, expectedVersion)
        updated <- f(domainObject)
      } yield updated
    }

  /**
   * 
   */
  def process(validation: DomainValidation[T])(onSuccess: T => Unit) = {
    validation.foreach { domainObject =>
      updateRepository(domainObject)
      onSuccess(domainObject)
    }
    sender ! validation
  }

  /**
   * A call to the repository
   */
  private def updateRepository(domainObject: T) = repository.updateMap(domainObject)

}