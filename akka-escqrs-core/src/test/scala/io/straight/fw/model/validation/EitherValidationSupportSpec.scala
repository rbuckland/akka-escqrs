package io.straight.fw.model.validation

import io.straight.fw.support.BaseSpec
import io.straight.fw.model.validation.simple.EitherValidationSupport

case class Person(firstname: String, lastname: String, email: String, age: Int)

class EitherValidationSupportSpec extends BaseSpec {
  
  val evs: EitherValidationSupport[Person] = Person("Todd", "nist", "tgn@@yahoo.com", 200)
    
  def isAgeValid(s: String): Either[String, Int] =
    try {
      val n = s.toInt
      if (n < 0)
        Left("Age must be greater than 0")
      else if (n > 130)
        Left("Age must be less than 130")
      else
        Right(n)
    } catch {
      case e: Throwable => Left(e.toString)
    }

  def isFirstNameValid(s: String): Either[String, String] =
    if (s.headOption exists (_.isUpper))
      Right(s)
    else
      Left("Name must begin with a capital letter")

  def isLastNameValid(s: String): Either[String, String] =
    if (s.headOption exists (_.isUpper))
      Right(s)
    else
      Left("Name must begin with a capital letter")

  def isValidEmailAddress(email: String): Boolean =
    """^([0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\w]*[0-9a-zA-Z]\.)+[a-zA-Z]{2,9})$""".r.unapplySeq(email).isDefined

  def isEmailValid(s: String): Either[String, String] =
    if (isValidEmailAddress(s))
      Right(s)
    else
      Left("Email must be valid")
   
  /*
   * TODO clean up test, it will always fail right now
   */
  "A EitherValidationSupport" must "validate all fields in a command" ignore {
    evs.check({p => isFirstNameValid(p.firstname).right.map(_ => p)},
      {p => isLastNameValid(p.lastname).right.map(_ => p)},
      {p => isEmailValid(p.email).right.map(_ => p)},
      {p => isAgeValid(p.age.toString).right.map(_ => p)}
    ) match {
      case Right(p) => println("We have a person: " + p)
      case Left(e) => { 
        e foreach println
        fail(s"Errors Found : $e")
      }
    }
  }

}