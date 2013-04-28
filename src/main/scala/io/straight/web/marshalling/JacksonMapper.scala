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

import java.lang.reflect.{ Type, ParameterizedType }
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
import java.util.Date
import com.fasterxml.jackson.databind.{ Module, ObjectMapper }
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import java.io.StringWriter
import com.fasterxml.jackson.datatype.joda.JodaModule

/**
 * FasterXML Jackson Library
 * - https://github.com/FasterXML/jackson-module-jaxb-annotations (see the README.md)
 *
 */
trait JacksonMapper {

  /**
   * Serializer for JSON
   */
  def serializeJson(value: Any) = jsonSerializer.writerWithDefaultPrettyPrinter().writeValueAsString(value)
  def serializeJsonNoPretty(value: Any) = jsonSerializer.writeValueAsString(value)


  /**
   * Serializer for XML
   */
  def serializeXml(value: Any) = xmlSerializer.writeValueAsString(value)

  /**
   * deserializer for JSON 
   */
  def deserializeJson[T: Manifest](value: String): T = jsonSerializer.readValue(value, typeReference[T])

    /**
   * deserializer for XML 
   */
  def deserializeXml[T: Manifest](value: String): T = xmlSerializer.readValue(value, typeReference[T])

  
  private[this] def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private[this] def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.runtimeClass }
    else new ParameterizedType {
      def getRawType = m.runtimeClass

      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

      def getOwnerType = null
    }
  }

  def jsonSerializer = {
    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
    m.registerModule(new JodaModule())
    m.registerModule(UuidModule.create)
    // these are the settings for JAXB annotations .. but don't mix with the DefaultScalaModule well
   // m.setAnnotationIntrospector(new JaxbAnnotationIntrospector())
    //m.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    //m.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
    m
  }

  def xmlSerializer = {
    val m = new XmlMapper()
    m.registerModule(DefaultScalaModule)
    m.registerModule(new JodaModule())
    m.registerModule(UuidModule.create)
    // these are the settings for JAXB annotations .. but don't mix with the DefaultScalaModule well
   // m.setAnnotationIntrospector(new JaxbAnnotationIntrospector())
    //m.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    //m.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
    m

  }

}


