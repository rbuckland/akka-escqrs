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

package io.straight.model


import scalaz._
import scalaz.Scalaz._

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
 */
abstract class BaseDomain {
    def uuid: Uuid
    def version: Long
    def versionOption = if (version == -1L) None else Some(version)
}

trait BaseDomainSupport {

  val invalidVersionMessage = "%s(%s): expected version %s doesn't match current version %s"

  def invalidVersion[T](base: T, uuid: Uuid, expected: Long, current: Long) =
    DomainError(invalidVersionMessage format (base.getClass.getCanonicalName, uuid, expected, current))

  def requireVersion[T <: BaseDomain](base: T, expectedVersion: Option[Long]): DomainValidation[T] = {
    val uuid = base.uuid
    val version = base.version

    expectedVersion match {
      case Some(expected) if (version.toLong != expected.toLong) => invalidVersion(base, uuid, expected, version).fail
      case Some(expected) if (version.toLong == expected.toLong) => base.success
      case None =>                                    base.success
    }
  }

}
