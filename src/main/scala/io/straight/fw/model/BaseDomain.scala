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

package io.straight.fw.model

import scalaz._
import Scalaz._
import com.fasterxml.jackson.annotation.JsonGetter
import io.straight.fw.jackson.JacksonBindingSupport._

/**
 * BaseDomain where we find the uuid.
 *
 * This class has no "ID" as it is not intended to be externally remembered.
 * All Domain objects will extend from this, however - therefore all will have a UUID for storage etc.
 *
 * Versioning is tied on the UUID, and not the ID. 
 *
 * uuid will be globally unique
 * the id will be Class Domain unique (a class primary key)
 *
 * TODO remove the reliance on UUID here (for people that want to use ID as the key)
 */
abstract class BaseDomain[I <: Any](
                           // for some reason, when these are addedd to the abstract class and overidden in subclasses
                           // we need to explicitly tell Jackson to Marshal them
                           @jsonProperty val id: I,
                           @jsonProperty val version: Long
                           ) {

  def versionOption = if (version == -1L) None else Some(version)

  private val invalidVersionMessage = "%s(%s): expected version %s doesn't match current version %s"

  def invalidVersion(expected: Long) = DomainError(invalidVersionMessage format(this.getClass.getCanonicalName, id, expected, version))

  def versionCheck[T <: BaseDomain[I]](expectedVersion: Option[Long]): DomainValidation[T] = {
    expectedVersion match {
      case Some(expected) if version.toLong != expected.toLong => invalidVersion(expected).fail
      case Some(expected) if version.toLong == expected.toLong => this.asInstanceOf[T].success
      case None => this.asInstanceOf[T].success
    }
  }

}
