package io.straight.fw.model

import _root_.scalaz.Validation

/**
 * @author rbuckland
 */
package object scalaz {

  // What the? Read on - http://stackoverflow.com/questions/8736164/what-are-type-lambdas-in-scala-and-what-are-their-benefits
  // The λ[α] are just type Paramaters (like T or A)
  type DomainValidation[+α] = ({type λ[α]=Validation[DomainError, α]})#λ[α]

}
