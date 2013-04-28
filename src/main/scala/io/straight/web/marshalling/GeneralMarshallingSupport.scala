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

package io.straight.web.marshalling

import org.slf4j.LoggerFactory
import io.straight.model._
import io.straight.validation._
import spray.http._
import spray.http.HttpBody
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import scalaz.Failure
import spray.routing.Rejection
import spray.routing.RequestContext


object ResponseMarshaller {
  
  import scalaz._
  import Scalaz._
 
  implicit def domainValidationMarshaller[T](implicit m: Marshaller[T]) = 
    Marshaller[DomainValidation[T]] { (value,ctx) =>
      value match { 
        case Success(result) => m.apply(result, ctx)
        case Failure(errors) => ctx.handleError(ValidationException(errors))
      }
  }
}

object JacksonUnmarshaller extends JacksonMapper {
  def apply[T: Manifest]: Unmarshaller[T] =
    new SimpleUnmarshaller[T] {
      val canUnmarshalFrom = ContentTypeRange(`application/json`) :: ContentTypeRange(`text/xml`) :: ContentTypeRange(`application/xml`) :: Nil
      def unmarshal(entity: HttpEntity) = {
        entity match {
          // no content
          case EmptyEntity => Left(ContentExpected)
          // content (cause an "Either Left(error) or Right(Object)" to occur through the protect()
          case b @ HttpBody(contentType, buffer) => protect(
              
              contentType.mediaType match {
                
                // call Jackson XMLMapper
                case `text/xml` | `application/xml` => deserializeXml[T](b.asString)
                
                // call Jackon JSONMapper 
                case `application/json` => deserializeJson[T](b.asString)
                
                case _ => throw new Exception("Unsupported mediaType: " + contentType.mediaType)
              }
              
            )
        }
      }
    }

}


object JacksonMarshaller extends JacksonMapper{

  
  val logger = LoggerFactory.getLogger(this.getClass())

  private def marshal(mediaType: MediaType, value: Any) = {
    mediaType match {
      case `text/xml` | `application/xml` => serializeXml(value)
      case `application/json` => serializeJson(value)
      case _ => "Error - the marshaller doesn't support " + mediaType.toString
    }
  }

  implicit def basicBaseDomainMarshaller[A <: BaseDomain] =
    Marshaller.of[A](`application/json`, `text/xml`, `application/xml`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpBody(contentType, marshal(contentType.mediaType, value)))
    }

  implicit def basicContainerMarshaller[A <: BasicMarshallable] =
    Marshaller.of[A](`application/json`, `text/xml`, `application/xml`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpBody(contentType, marshal(contentType.mediaType, value)))
    }

}
