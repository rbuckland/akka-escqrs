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

import java.lang.reflect.{ Type, ParameterizedType }
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.core.{Version, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule

/**
 * FasterXML Jackson Library
 * - https://github.com/FasterXML/jackson-module-jaxb-annotations (see the README.md)
 *
 */
trait JacksonMapper {


  def jsonSerializer = internalJsonSerializer
  def xmlSerializer = internalXmlSerializer
  /**
   * Serializer for JSON
   */
  def serializeJson(value: Any) = jsonSerializer.writerWithDefaultPrettyPrinter().asInstanceOf[ObjectWriter].writeValueAsString(value)

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

  private def applyCommonConfig(m: ObjectMapper) {
    m.registerModule(DefaultScalaModule)
    m.registerModule(new JodaModule())
    m.registerModule(UuidModule.create)
  }

  def internalJsonSerializer = {
    val m = new ObjectMapper()
    applyCommonConfig(m)
    // these are the settings for JAXB annotations .. but don't mix with the DefaultScalaModule well
   // m.setAnnotationIntrospector(new JaxbAnnotationIntrospector())
    //m.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    //m.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  }

  def internalXmlSerializer = {
    val m = new XmlMapper()
    applyCommonConfig(m)
    // these are the settings for JAXB annotations .. but don't mix with the DefaultScalaModule well
   // m.setAnnotationIntrospector(new JaxbAnnotationIntrospector())
    //m.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    //m.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
    m
  }

  /**
   * We use this Serializer for loading configuration files to Case Classes
   * It means we can comment the config file to our hearts content!
   *
   * but we DON'T use this one for our APIs
   * Use it like:
   *
   *   val jackson = new JacksonMapper {
   *     override def jsonSerializer = jsonSerializerRelaxed
   *   }
   *
   * @return
   */
  def jsonSerializerRelaxed = {
    val m = internalJsonSerializer
    m.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    m.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    m.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    m
  }

  def jsonSerialiserRelaxedWithEmptyDefaults = {
    val m = internalJsonSerializer
    m.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    m.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    m.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    m.registerModule(NullToEmptyStringDeserialiser.create)
    m
  }

  def jsonSerializerAllAnnonIgnore = {
    val m = internalJsonSerializer
    m.configure(MapperFeature.USE_ANNOTATIONS, false);
    m
  }

  def xmlSerializerAllAnnonIgnore = {
    val m = internalXmlSerializer
    m.configure(MapperFeature.USE_ANNOTATIONS, false);
    m
  }

}

class NullToEmptyStringDeserialiser extends JsonDeserializer[String] {
  def deserialize(jp: JsonParser, ctxt: DeserializationContext) = jp.getText
  override def getNullValue  =  "";
}
object NullToEmptyStringDeserialiser {
  def create() = {
    new SimpleModule("NullToEmptyStringDeserialiserModule",new Version(1,0,0,null,"io.straight","NullToEmptyStringDeserialiserModule"))
      .addDeserializer(classOf[String], new NullToEmptyStringDeserialiser())
  }
}


