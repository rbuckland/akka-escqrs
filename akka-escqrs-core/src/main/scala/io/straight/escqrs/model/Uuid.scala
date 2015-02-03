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

package io.straight.escqrs.model

import java.nio.ByteBuffer

/**
 *
 * A Uuid is comprised of two Longs. 128bits (64 bits x 2)
 *
 * We use one Long for the ID, 16bits for a groupd ID.. leaving
 * 48 bits for a random set
 *
 *
 * This Uuid Class internally stores the 3 values we care about
 *
 * Two others are fixed  (version and variant)
 *
 * {{{
 * A traditional Uuid is comprised of two 64bit longs.
 * [       lower    ] [    upper      ]
 * 00000000-0000-0000-0000-000000000000
 *
 * We will store the ID in the lower
 * and use the upper to hold a groupID of 16 bits and a timestamp of 48 bits
 *                    -----------------
 *                    60 bits Random bits (time or other)
 * -------- ----
 * iiiiiiii-iiii-iiii-gggg-tttttttttttt
 * example:
 * 00000000-0000-02ef-0988-ebb6f6fg1ac8
 * }}}
 *
 * We are not using any of the traditional semantics of Version and Variant.
 * This is just a simple way to encode an ID into a string.
 *
 */
case class Uuid(id: Long, groupId: Int, randId: Long) extends Serializable {

  assert(groupId < 65536 && groupId > -1,"Use Uuid.crc16 to guarantee to stay under 2^16")          // 2^16 // 00000000-0000-0000-abcd-000000000000
  assert(randId < 281474976710656L && randId > -1,"Use.crc48 to guarantee a value under 2^48") // 2^48 // 00000000-0000-0000-0000-abcdefghijkl

  // we blatantly ignore the groupId and the randId in a max check
  def max(other: Uuid) = this.id > other.id

  /**
   * Just the id and the groupId
   * eg. 12-66df, 2a-6afa
   * 6df is the groupId, afa is the groupId
   */
  private val idToHex = toHex(id >> 32, 8) + "-" + toHex(id >> 16, 4) + "-" + toHex(id, 4)

  val shortId = idToHex + "-" + toHex(groupId, 4)

  override val toString: String = {
    idToHex + "-" + toHex(groupId, 4) + "-" + toHex(randId, 12)
  }


  private def toHex(number: Long, size: Int): String = {
    val mask: Long = 1L << (size * 4)
    java.lang.Long.toHexString(mask | (number & (mask - 1))).substring(1)
  }

}

object Uuid {


  // convenience method .. groupName will be crc16'd and timestamp will be crc48'd
  def newUuid(id: Long, groupName: String, timestamp:Long) = Uuid(id,crc16(groupName),crc48(timestamp))

  def groupIdForClass(klass: Class[_]):Int = groupId(klass.getCanonicalName)

  /**
   * Create a Uuid object from the standard UUID String
   * @param uuid
   * @return
   */
  def fromString(uuid:String):Uuid = {

    // 00000000-0000-0000-abcd-000000000000
    // --- id ----------  grp  -- ranDid --
    val uuidRegex = """([0-9a-fA-F]{8})-([0-9a-fA-F]{4})-([0-9a-fA-F]{4})-([0-9a-fA-F]{4})-([0-9a-fA-F]{12})""".r

    val uuidRegex(id0,id1,id2,grp0,rand0) = uuid
    val id = java.lang.Long.parseLong(id0+id1+id2,16)
    val groupId = Integer.parseInt(grp0,16)
    val randId = java.lang.Long.parseLong(rand0,16)
    return Uuid(id,groupId,randId)
  }


  /**
   * Determine a Group Id for a Class
   */
  def groupId(groupName : String) = crc16(groupName)

  // we should use a table lookup variant (quicker)

  def crc16(toHash:String):Int = crc16(toHash.getBytes)

  /**
   * TODO Convert to an immutable recursive
   * http://introcs.cs.princeton.edu/java/51data/CRC16.java
   * @param bytes
   * @return
   */
  def crc16(bytes:Array[Byte]):Int = {
    var crc:Int = 0x0000
    for (b <- bytes) {
      crc = (crc >>> 8) ^ table((crc ^ b) & 0xff)
    }
    crc
  }

  def crc48(toHash:Long):Long = crc48(ByteBuffer.allocate(8).putLong(toHash).array())
  private val crc48poly = 0x0111552c4310cbL; // polynomial
  def crc48(bytes:Array[Byte]):Long = {
      var crc:Long = 0;
      for (b <- bytes) {
        for (i <- 0 to 8) {
          crc = crc << 1
          if ((crc >> 48) > 0) {
            crc = crc ^ crc48poly
          }
        }
        crc = crc ^ 0xff&b
      }
      crc
    }

  private def crc12(toHash: String) = {

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

  val table = Array[Int](
    0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
    0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
    0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
    0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
    0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
    0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
    0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
    0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
    0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
    0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
    0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
    0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
    0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
    0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
    0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
    0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
    0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
    0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
    0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
    0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
    0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
    0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
    0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
    0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
    0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
    0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
    0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
    0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
    0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
    0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
    0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
    0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
    )

}
