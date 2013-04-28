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

import org.eligosource.eventsourced.core.Emitter
import org.slf4j.LoggerFactory
import scala.collection.mutable.Map
import java.util.UUID
import java.lang.reflect.{ Type, ParameterizedType }
import scalaz._
import scalaz.Scalaz._


/**
 * The IDGenerator Actor
 */
trait IdGenerator[D <: DomainId] {
  
  implicit def klass: Class[_]
  
  private val ids = Map.empty[String, Long]
  
  private[this]def className = klass.getClass().getCanonicalName()

  /**
   * return the next ID
   */
  def nextId:DomainId = {
    val idKey = className
    val currentId = ids.getOrElseUpdate(idKey, 0L)
    ids += (idKey -> (currentId + 1))
    return currentId + 1
  }
  
  /**
   * return the next ID. Processed inside a transaction
   */
  def setStartingId(startingId: Long): Long = {

      val idKey = className
      val currentId = ids.getOrElseUpdate(idKey, 0L)

      // not allowed to set the starting ID to be less that the current used up number
      if (currentId >= startingId) {
        throw new Exception("Starting Id too low (" + startingId + "). It cannot be less than (" + currentId + ")")
      } else {
        ids += (idKey -> startingId)
      }
      return startingId
  }

}
