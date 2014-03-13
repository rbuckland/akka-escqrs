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

import scala.collection.mutable.Map
import java.lang.reflect.{ Type, ParameterizedType }
import scala.reflect.ClassTag


/**
 * The IDGenerator Actor
 */
trait UuidGenerator[D] {
  
  private val ids = Map.empty[String, Long]

  def className()(implicit t: ClassTag[D]) = t.runtimeClass.getCanonicalName

  /**
   * return the next ID
   * @param upperLong typically a timestamp (externally sourced, so that replays will work)
   */
  def newUuid(upperLong: Long)(implicit t: ClassTag[D]): Uuid = {
    val idKey = className
    val currentId = ids.getOrElseUpdate(idKey, 0L)
    ids += (idKey -> (currentId + 1))
    return Uuid.createUuid(className, currentId + 1, upperLong)
  }
    
  /**
   * We need this guy be running as an Actor .. return the next ID. Processed inside a transaction
   */
  def setStartingId(startingId: Long)(implicit t: ClassTag[D]): Long = {

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
