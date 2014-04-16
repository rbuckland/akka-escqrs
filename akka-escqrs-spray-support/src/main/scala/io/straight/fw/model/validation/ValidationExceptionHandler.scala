package io.straight.fw.model.validation

import spray.routing.ExceptionHandler
import spray.util.LoggingContext
import io.straight.fw.spray.marshalling.JacksonMapper

/**
 * This is the ValidationExceptionHandler for use with the ValidationException
 * @author rbuckland
 *
 *         TODO sort out the Validation components that spray uses now (v1.3.1)
 *         https://groups.google.com/forum/#!msg/spray-usersecurity/D0d5ZJcvkoo/fxHzwH7YYq0J
 */

trait ValidationExceptionHandler {

  object AJacksonMapper extends JacksonMapper

  implicit def myExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler({
      case e: ValidationException => ctx =>
        log.warning("Validation errors occured with {}", ctx.request)
        ctx.complete(spray.http.StatusCodes.BadRequest, AJacksonMapper.serializeJson(e.errors))
    })
}