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


package io.straight.messages


import org.joda.time.DateTime
import io.straight.model.{Uuid,DomainId}

/**
 * Eventsourced will journal these messages.
 * 
 * Incoming, CommandMessage
 * Outgoing, EventMessage
 * 
 * Of note: The spray Json marshalling will unmarshall the BaseCommand.
 * 
 */
abstract class BaseMessage { 
    def messageDate:DateTime
}

abstract class BaseCommand
abstract class BaseEvent

case class CommandMessage(
    messageDate: DateTime = DateTime.now(),
    id: DomainId = -1L,
    uuid: Uuid = null,
    cmd: BaseCommand) extends BaseMessage
    
case class EventMessage(
    messageDate: DateTime = DateTime.now(),
    id: DomainId,
    uuid: Uuid,
    event: BaseEvent) extends BaseMessage



