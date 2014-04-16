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

package io.straight.fw.model

/**
 * This trait is used when we want a class to be marshallable going "out" from spray.
 * 
 * I use these on "Container" classes such as: 
 * 
 * case class Persons(persons: List[Person]) extends BasicMarshallable
 * 
 * Where Person is a real domain case class.
 */
trait BasicMarshallable
