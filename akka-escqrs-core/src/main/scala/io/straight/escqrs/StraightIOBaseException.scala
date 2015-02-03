package io.straight.escqrs

@SerialVersionUID(1L)
abstract class StraightIOBaseException(val message: String, val cause: Throwable) extends RuntimeException(message, cause) with Serializable
