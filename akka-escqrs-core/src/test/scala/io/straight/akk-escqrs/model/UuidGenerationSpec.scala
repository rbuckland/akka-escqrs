package io.straight.escqrs.model

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.security.SecureRandom
import java.util.Date

class UuidGenerationSpec extends FlatSpec with ShouldMatchers {

  "UUID Gen" should "generate a UUID where the higher bits matches our supplied details" in {

    val groupId = Uuid.groupId(classOf[java.lang.Long].getCanonicalName)
    val uniqueId = 179

    println("uniqId = " + uniqueId.toHexString + " groupId = " + groupId.toHexString)

    def expecteduuidRegex = "0{8}-0{4}-" +
      uniqueId.toHexString.reverse.padTo(4,'0').reverse +
      "-" +
      groupId.toHexString.reverse.padTo(3, '0').reverse

    val uuid = Uuid(uniqueId,groupId,0).toString
    println("[" + uuid + "] regexp expected is : " + expecteduuidRegex);
    uuid should startWith regex (expecteduuidRegex.r)
  }

//  "UUID Gen" should "generate a good Partial UUID" in {
//
//    val groupId = Uuid.groupId(classOf[java.lang.Integer].getCanonicalName)
//    val uniqueId = 1789
//
//    val partialUuid = Uuid.createPartialUuidString(uniqueId,groupId)
//    val uuid = Uuid(uniqueId,groupId,0).uuid.toString()
//    println("[" + uuid + "] --> partial test : " + partialUuid);
//    uuid should startWith regex (partialUuid)
//  }

  "UUID Gen" should "create a nice shortid" in {

    val groupId = Uuid.groupId(classOf[java.lang.Math].getCanonicalName)
    val uniqueId = 2562

    def expecteduuidRegex = uniqueId.toHexString +"-" + groupId.toHexString

    val uuid = Uuid(uniqueId,groupId,0)
    println("[" + uuid + "] --> short id : " + uuid.shortId  + " // we expect it to match " + expecteduuidRegex);
    uuid.shortId should include regex (expecteduuidRegex)
  }

  "UUID Gen" should "create short hex strings (12 bits) for use as a Group ID" in {
    val groupId = Uuid.groupId(classOf[java.lang.String].getCanonicalName)
    println("groupId = " + groupId)
    groupId >= 0 &&  groupId < 4096
  }
    
  "generation of a groupId" should "always be less than 4096" in {

    val sr = new SecureRandom()
    var allOk = true
    for (i <- 1 to 70000) {
      val groupId = Uuid.groupId(sr.nextInt().toHexString)
      if (groupId < 0 || groupId > 65536) {
        allOk = false
      }
    }
    allOk should be (true)
  }

  "The Uuuid.fromString(String)" should "create the correct Uuuid" in {

    val millis = new Date().getTime
    println("millis = " + millis)
    println("millisHex = " + millis.toHexString)
    val uuid = Uuid(523,20,millis)
    println("uuid = " + uuid)
    val uuidStr = uuid.toString
    val fromStringUuid = Uuid.fromString(uuidStr)
    uuid should equal (fromStringUuid)

  }

  "A large randomId to string" should "(through the crc48) give back the same Uuid" in {

      val bigRand = new Date().getTime * 801279
      println("bigRand = " + bigRand)
      println("bigRandHex = " + bigRand.toHexString)
      println("bigRand as crc48(hex) = " + Uuid.crc48(bigRand).toHexString)
      val uuid = Uuid(523,20,Uuid.crc48(bigRand))
      println("uuid = " + uuid)
      val uuidStr = uuid.toString
      val fromStringUuid = Uuid.fromString(uuidStr)
      uuid should equal (fromStringUuid)
  }
}
