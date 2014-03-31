package io.straight.fw.model

import scalaz._
import scalaz.Scalaz._

/**
 * @author rbuckland
 */
package object scalaz {


  // What the? Read on - http://stackoverflow.com/questions/8736164/what-are-type-lambdas-in-scala-and-what-are-their-benefits
  type SZDomainValidation[+T] = ({type L[T]=Validation[DomainError, T]})#L[T]

}
