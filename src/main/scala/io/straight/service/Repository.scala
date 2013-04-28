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

import scala.concurrent.stm.Ref

/**
 * A Wrapper around an STM Ref of a Map.
 * To be used by the "Service" class and the "Processor" classes only
 */
class Repository[K,A](keyGetter: (A) => K) {

  private val internalMap :Ref[Map[K,A]] = Ref(Map.empty[K, A])
  
  def getMap = internalMap.single.get
  def getByKey(key: K): Option[A] = getMap.get(key)
  def getValues: Iterable[A] = getMap.values
  def getKeys: Iterable[K] = getMap.keys
  
  // TODO work out how to restrict the updateMap to just 
  // subclasses of AbstractProcessor.
  // at the moment, the AbstractProcessor must be in the io.straight.service package
  // CHANGE this
  protected[service] def updateMap(value: A) =
    internalMap.single.transform(map => map + (keyGetter(value) -> value))
   
}
