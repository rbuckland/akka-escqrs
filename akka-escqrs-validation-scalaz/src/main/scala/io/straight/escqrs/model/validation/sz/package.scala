package io.straight.escqrs.model.validation

import scalaz._
import io.straight.escqrs.model.DomainError

/**
 * @author rbuckland
 */
package object sz {


  // What the? Read on - http://stackoverflow.com/questions/8736164/what-are-type-lambdas-in-scala-and-what-are-their-benefits
  type SZDomainValidation[+T] = ({type L[T]=Validation[DomainError, T]})#L[T]

}
