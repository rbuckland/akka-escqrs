package io.straight.fw

@SerialVersionUID(1L)
abstract class StraightIOBaseException(val message: String, val cause: Throwable) extends RuntimeException(message, cause) with Serializable
