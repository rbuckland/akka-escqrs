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

package io.straight.escqrs.spray.marshalling

import org.slf4j.LoggerFactory
import io.straight.escqrs.model._
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import spray.http.HttpEntity.Empty
import spray.http.{MediaType, HttpEntity, ContentTypeRange}
import io.straight.escqrs.model.validation.ValidationException

/**
 * Marshal an Either Domain Validation Object
 */
object EitherDomainValidationMarshaller {
  import io.straight.escqrs.model.validation.simple.EitherDomainValidation
  val logger = LoggerFactory.getLogger(this.getClass)
  implicit def domainValidationMarshaller[T](implicit m: Marshaller[T]) =
    Marshaller[EitherDomainValidation[T]] { (value,ctx) =>
      value match {
        case Right(result) => {
          logger.info("going to marshall " + result.getClass)
          m.apply(result, ctx)
        }
        case Left(errors) => throw ValidationException(errors)
      }
    }
}


object JacksonUnmarshaller extends JacksonMapper {
  def apply[T: Manifest]: Unmarshaller[T] =
    new SimpleUnmarshaller[T] {
      val canUnmarshalFrom = ContentTypeRange(`application/json`) :: ContentTypeRange(`text/xml`) :: ContentTypeRange(`application/xml`) :: Nil
      def unmarshal(entity: HttpEntity) = {
        entity match {
          case Empty => Left(ContentExpected)
          case HttpEntity.NonEmpty(contentType, data) => protect(
              contentType.mediaType match {
                // call Jackson XMLMapper
                case `text/xml` | `application/xml` => deserializeXml[T](data.asString)
                // call Jackon JSONMapper
                case `application/json` => deserializeJson[T](data.asString)
                case _ => throw new Exception("Unsupported mediaType: " + contentType.mediaType)
              }
            )
        }
      }
    }

}


trait JacksonMarshaller extends JacksonMapper {

  
  val logger = LoggerFactory.getLogger(this.getClass)

  def marshal(mediaType: MediaType, value: Any): String = {
    mediaType match {
      case `text/xml` | `application/xml` => serializeXml(value)
      case `application/json` => serializeJson(value)
      case _ => "Error - the marshaller doesn't support " + mediaType.toString
    }
  }

  implicit def basicUuidBaseDomainMarshaller[A <: DomainType[Uuid]]:spray.httpx.marshalling.Marshaller[A] = {
    Marshaller.of[A](`application/json`, `text/xml`, `application/xml`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, marshal(contentType.mediaType, value).getBytes))
    }
  }

  // this one doesn't help. because I is not resolved for it :-( -- make ones like the above.
  // otherwise your error message is ..
  // could not find implicit value for parameter marshaller: spray.httpx.marshalling.ToResponseMarshaller[scala.concurrent.Future

  implicit def basicBaseDomainMarshaller[A <: DomainType[I], I <: Any]:spray.httpx.marshalling.Marshaller[A] = {
    Marshaller.of[A](`application/json`, `text/xml`, `application/xml`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, marshal(contentType.mediaType, value).getBytes))
    }
  }


  /**
   * This is an example of how you can us this ..
   */
  /*
  implicit def basicContainerMarshaller[A <: ResponseMessage]:spray.httpx.marshalling.Marshaller[A] =
    Marshaller.of[A](`application/json`, `text/xml`, `application/xml`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, marshal(contentType.mediaType, value).getBytes))
    }
  */


}
