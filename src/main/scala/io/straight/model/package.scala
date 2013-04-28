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

package io.straight

import scalaz._

package object model {
  
  type DomainValidation[+α] = ({type λ[α]=Validation[DomainError, α]})#λ[α]
  type DomainError          = List[String]

  object DomainError {
    def apply(msg: String): DomainError = List(msg)
  }

  /*
   * DomainId
   */
  type DomainId = Long
  
}
