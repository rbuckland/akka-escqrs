package io.straight.escqrs.jackson

/**
 * @author rbuckland
 */

import com.fasterxml.jackson.annotation.JsonProperty
import scala.annotation.meta.field
import com.fasterxml.jackson.annotation.JsonIgnore

object JacksonBindingSupport {

  type jsonProperty = JsonProperty @field
  type jsonIgnore = JsonIgnore @field
}
