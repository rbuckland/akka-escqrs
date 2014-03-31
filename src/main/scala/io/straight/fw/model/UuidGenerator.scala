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

import scala.collection.mutable.{Map => MutableMap}
import scala.reflect.ClassTag
import java.util.Date
import io.straight.fw.StraightIOBaseException


case class StraightIOUuidException(error: String) extends StraightIOBaseException(error,null)

/**
 * The IDGenerator. This trait could live on a completely separate
 * actor to the Event sourced one, and all AbstractProcesssor (shared aggregates) would
 * communicate with it to get keys (completely do-able) and not tested.
 *
 * @author rbuckland_
 */
class UuidGenerator[T <: UuidDomainType](val klass: Class[T]) extends IdGenerator[Uuid,T] {
  
  private var ids = MutableMap.empty[String, Long]

  val klassName = klass.getCanonicalName

  /**
   * return the next ID
   * @param upperLong typically a timestamp (externally sourced, so that replays will work)
   */
  def newUuid(upperLong: Long): Uuid = {
    ids += (klassName -> (currentId + 1))
    Uuid(currentId(),Uuid.groupId(klassName),upperLong)
  }

  override def newId() :Uuid = newUuid(new Date().getTime)

  private def currentId() = ids.getOrElseUpdate(klassName, 0L)

  /**
   * A potential next Id  (you can't use it though as it won't really be the next ID (time based remember!!)
   * @return
   */
  def potentialNextId = Uuid(currentId + 1,Uuid.groupId(klassName),new Date().getTime)

  /**
   * We need this guy be running as an Actor .. return the next ID. Processed inside a transaction
   */
  override def setStartingId(startingUuid: Uuid): Unit = {

      val anewId = startingUuid.id

      // not allowed to set the starting ID to be less that the current used up number
      if (currentId > anewId) {
        throw new StraightIOUuidException("Starting Id too low (" + startingUuid + "). It cannot be less than (" + currentId + ")")
      } else {
        ids += (klassName -> anewId)
      }
  }
}


