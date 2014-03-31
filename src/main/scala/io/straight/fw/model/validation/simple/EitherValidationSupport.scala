package io.straight.fw.model.validation.simple

/**
 * Some people don't like or want to use Scalaz validation.
 * I like it.
 *
 * Some things I really like about Scalaz. But for new people learning, it is just a barrier without
 * some good documentation. Simple. (time to learn = money required = someone has to pay it)
 *
 * Scalaz _can_ mean less time with my children who will not care what ScalaZ is :-o (wow!)
 *
 * @author Rob Dickens
 * @note Source : http://robsscala.blogspot.co.uk/2012/04/validation-without-scalaz.html
 */
trait EitherValidationSupport[T] {
  def succeed[L]: Right[L, T]
  def fail[R]: Left[T, R]
  def check[L](checks: ((T) => Either[L, T])*): Either[List[L], T]
  def checkAndMap[L, R](checks: ((T) => Either[L, T])*)(f: (T) => R): Either[List[L], R]
}

object EitherValidationSupport {
  implicit def any2EitherExtras[T](any: T): EitherValidationSupport[T] = new EitherValidationSupport[T] {
    def succeed[L] = Right[L, T](any)
    def fail[R] = Left[T, R](any)
    def check[L](checks: ((T) => Either[L, T])*): Either[List[L], T] =
      checkAndMap[L, T](checks: _*) { t => t }
    def checkAndMap[L, R](checks: ((T) => Either[L, T])*)(f: (T) => R): Either[List[L], R] = {
      val msgs = for {
        check <- checks.toList // from WrappedArray
        msg <- check(any).left.toSeq
      } yield msg
      if (msgs.isEmpty) Right(f(any)) else Left(msgs)
    }
  }
}