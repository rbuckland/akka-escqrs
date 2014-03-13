package io.straight.fw.messages

import io.straight.fw.StraightIOBaseException

case class InvalidMessageException(override val message: String, override val cause: Throwable = null) extends StraightIOBaseException(message, cause)