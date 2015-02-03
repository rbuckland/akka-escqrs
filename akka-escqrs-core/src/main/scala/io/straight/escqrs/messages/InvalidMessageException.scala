package io.straight.escqrs.messages

import io.straight.escqrs.StraightIOBaseException

case class InvalidMessageException(override val message: String, override val cause: Throwable = null) extends StraightIOBaseException(message, cause)
