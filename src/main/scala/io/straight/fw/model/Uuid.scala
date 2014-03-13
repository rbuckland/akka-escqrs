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

import java.security.SecureRandom

case class Uuid(uuid: String) {
  require(uuidRegex.unapplySeq(uuid).isDefined, "The UUID String (" + uuid + ") supplied is invalid")
  private def uuidRegex = """[\da-fA-F]{8}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{12}""".r

  val shortId:String = {
    val ShortIdReg = """[0\-]*([\da-fA-F\-]+-[\da-fA-F]{4})-[\da-fA-F]{4}-[\da-fA-F]{12}""".r
    val ShortIdReg(shortId) = uuid
    shortId
  }
}

// http://www.coderanch.com/t/493983/java/java/UUID-Long-Integer
object Uuid {

  def secureRandom = new SecureRandom();

  /*

A UUID is comprised of two 64bit longs.
[       lower    ] [    upper      ]
00000000-0000-6000-a000-000000000000
                    -----------------
                    60 bits Random bits (time or other)
                   -
                   x == a, b, 8 or 9 - we will use 'a'
               ---
               Group ID - of 12 bits value is (0 to 4095)
              - 
              4 == always for (Version 4, Random UUID)
-------- ----
Primary key ID of 48 bits  (max 281,474,976,710,656)

Essentially, the ID and the Group ID of the object
will be found in the lower 64 bits, separated in the UUID by the '4' (the version).
The other upper 64 bits will be random, excepting the IETF marker.

   */
  val UUID_VERSION = 6L
  val UUID_VARIANT = 10L

  val ID_OFFSET = 16
  val UUID_VERSION_OFFSET = 12
  val UUID_VARIANT_OFFSET = 60

  def fromJavaUuid(javaUuid: java.util.UUID) = Uuid(javaUuid.toString())
  def empty() = new Uuid("ffffffff-eeee-dddd-cccc-bbbbbbbbbbbb")

  /**
   * Create a new UUID given a Class Name and an already sequenced ID
   */
  def createUuid(groupName: String, id: Long): Uuid = {
    val randomBytes = new Array[Byte](8)
    secureRandom.nextBytes(randomBytes)
    val randomLong = java.nio.ByteBuffer.wrap(randomBytes).getLong()
    return createUuid(groupId(groupName), id, randomLong)
  }


  /**
   * Create a Long, specifying the class for a group ID, the ID and the end Long
   * The end Long is typically a timestamp
   *
   * @param groupName typically the class name
   * @param id
   * @return
   */
  def createUuid(groupName: String, id: Long, upperLong: Long): Uuid = createUuid(groupId(groupName), id, upperLong)

  /**
   * Create a new UUID given some ID as the groupID and an already sequenced ID
   */
  def createUuid(groupId: Int, id: Long, upperLong: Long): Uuid = {
    // groupID has to be 12bits as the 4L is going in over the top.
    return Uuid(new java.util.UUID(groupId | (UUID_VERSION << UUID_VERSION_OFFSET) | (id << ID_OFFSET), (UUID_VARIANT << UUID_VARIANT_OFFSET) | (upperLong >>> 4)).toString())
  }

  /**
   * Create a partial Uuid .. the last part
   * ::
   * :: The beginning is always 0
   */
  def createPartialUuidString(groupId: Int, id: Long): String = new java.util.UUID(groupId | (UUID_VERSION << UUID_VERSION_OFFSET) | (id << ID_OFFSET), 0L).toString().substring(0, 18)

  /**
   * A Class name version
   */
  def createPartialUuidString(groupName: String, id: Long): String = createPartialUuidString(groupId(groupName), id)

  /**
   * Determine a Group Id for a Class
   */
  def groupId(groupName : String) = hash(groupName)

  def hash(toHash: String) = crc12(toHash)
  
  def crc12(toHash: String) = {

    /**
     * ************************************************************************
     *  Using direct calculation
     * ************************************************************************
     */

    var crc:Int = 0xFFF; // initial contents of LFBSR
    var poly: Int = 0xF01; // reverse polynomial
    var bytes = toHash.getBytes()

    for (b: Byte <- bytes) {
      var temp = (crc ^ b) & 0xff;

      // read 8 bits one at a time
      for (i <- 0 to 7) {
        if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
        else temp = (temp >>> 1);
      }
      crc = (crc >>> 8) ^ temp;
    }

    // flip bits
    crc = crc ^ 0xfff;

    crc;

  }

}

