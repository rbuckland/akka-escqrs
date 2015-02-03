package io.straight.escqrs.spray.marshalling

import _root_.io.straight.escqrs.model.validation.sz.SZDomainValidation
import org.slf4j.LoggerFactory
import spray.httpx.marshalling.Marshaller
import io.straight.escqrs.model.validation.ValidationException
import scalaz._
import Scalaz._

/**
 * @author rbuckland
 */

/**
 * Marshal a Scalaz Domain Validation Object
 */
object SZDomainValidationMarshaller {
  val logger = LoggerFactory.getLogger(this.getClass)
  import scalaz._
  import io.straight.escqrs.model.validation.sz.SZDomainValidation
  implicit def domainValidationMarshaller[T](implicit m: Marshaller[T]) =
    Marshaller[SZDomainValidation[T]] { (value,ctx) =>
      value match {
        case Success(result) => {
          logger.info("going to marshall " + result.getClass)
          m.apply(result, ctx)
        }
        case Failure(errors) => throw ValidationException(errors)
      }
    }
}
