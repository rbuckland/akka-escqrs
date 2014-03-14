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

package io.straight.fw.validation

import spray.util.LoggingContext
import spray.routing.ExceptionHandler
import io.straight.fw.marshalling.JacksonMapper
import io.straight.fw.StraightIOBaseException

/**
 * This exception is used to throw up through the spray hierachy.
 * https://groups.google.com/forum/#!msg/spray-usersecurity/D0d5ZJcvkoo/fxHzwH7YYq0J
 */
case class ValidationException(errors: List[String]) extends StraightIOBaseException(errors.toString,null)

// This is the ValidationExceptionHandler for use with the ValidationException
trait ValidationExceptionHandler { 
  
  object AJacksonMapper extends JacksonMapper
  
  implicit def myExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler( {
      case e:ValidationException => ctx =>
        log.warning("Validation errors occured with {}", ctx.request)
        ctx.complete(spray.http.StatusCodes.BadRequest, AJacksonMapper.serializeJson(e.errors))
    })
}