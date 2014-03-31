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


package io.straight.fw.spray.marshalling

import com.fasterxml.jackson.databind.{ JsonDeserializer, JsonSerializer }
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import io.straight.fw.model.Uuid

/**
 * Deserializer for the Uuid class
 */
class UuidDeserializer extends JsonDeserializer[Uuid]
{
  override def deserialize(jp: JsonParser,ctxt: DeserializationContext) : Uuid = {
    Uuid(jp.getText)
  }
}

/**
 * Deserializer for the Uuid class
 */
class UuidSerializer extends JsonSerializer[Uuid]
{
  override def serialize(uuid: Uuid,jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) = {
    jsonGenerator.writeString(uuid.uuid)
  }
}


object UuidModule { 
  def create() = { 
    new SimpleModule("UuidModule",new Version(1,0,0,null,"io.straight.fw","UuidModule"))
                .addDeserializer(classOf[Uuid], new UuidDeserializer())
                .addSerializer(classOf[Uuid], new UuidSerializer())
  }
}
