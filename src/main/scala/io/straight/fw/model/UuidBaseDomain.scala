package io.straight.fw.model

import io.straight.fw.jackson.JacksonBindingSupport._
import com.fasterxml.jackson.annotation.JsonGetter

/**
 * @author rbuckland
 */
abstract class UuidBaseDomain(  // for some reason, when these are addedd to the abstract class and overidden in subclasses
                                // we need to explicitly tell Jackson to Marshal them
                                @jsonProperty override val id: Uuid,
                                @jsonProperty override val version: Long
                               ) extends BaseDomain[Uuid](id,version) {

  @JsonGetter
  def shortId = id.shortId  // a pretty looking iD (just the id and the groupId)

}
