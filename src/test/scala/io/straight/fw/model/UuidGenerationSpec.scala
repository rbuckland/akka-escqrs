package io.straight.fw.model

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.security.SecureRandom

class UuidGenerationSpec extends FlatSpec with ShouldMatchers {

  "UUID Gen" should "generate a UUID where the higher bits matches our supplied details" in {

    val groupId = Uuid.groupId(classOf[java.lang.Long].getCanonicalName)
    val uniqueId = 179

    println("uniqId = " + uniqueId.toHexString + " groupId = " + groupId.toHexString)

    def expecteduuidRegex = "0{8}-" +
      uniqueId.toHexString.reverse.padTo(4,'0').reverse +
      "-" + Uuid.UUID_VERSION.toHexString +
      groupId.toHexString.reverse.padTo(3, '0').reverse +
      "-" + Uuid.UUID_VARIANT.toHexString

    val uuid = Uuid(uniqueId,groupId,0).uuid.toString()
    println("[" + uuid + "] regexp expected is : " + expecteduuidRegex);
    uuid should startWith regex (expecteduuidRegex.r)
  }

  "UUID Gen" should "generate a good Partial UUID" in {

    val groupId = Uuid.groupId(classOf[java.lang.Integer].getCanonicalName)
    val uniqueId = 1789
    
    val partialUuid = Uuid.createPartialUuidString(uniqueId,groupId)
    val uuid = Uuid(uniqueId,groupId,0).uuid.toString()
    println("[" + uuid + "] --> partial test : " + partialUuid);
    uuid should startWith regex (partialUuid)
  }

  "UUID Gen" should "create a nice shortid" in {

    val groupId = Uuid.groupId(classOf[java.lang.Math].getCanonicalName)
    val uniqueId = 2562

    def expecteduuidRegex = uniqueId.toHexString +"-" + Uuid.UUID_VERSION.toHexString + groupId.toHexString

    val uuid = Uuid(uniqueId,groupId,0)
    println("[" + uuid + "] --> short id : " + uuid.shortId );
    uuid.toString should include regex (expecteduuidRegex)
  }

  "UUID Gen" should "create short hex strings (12 bits) for use as a Group ID" in {
    val groupId = Uuid.groupId(classOf[java.lang.String].getCanonicalName)
    println("groupId = " + groupId)
    groupId >= 0 &&  groupId < 4096
  }
    
  "generation of a groupId" should "always be less than 4096" in {

    val sr = new SecureRandom()
    var allOk = true
    for (i <- 1 to 20000) {
      val groupId = Uuid.groupId(sr.nextInt().toHexString)
      if (groupId < 0 || groupId > 4096) {
        allOk = false
      }
    }
    allOk should be (true)
  }
  
}
