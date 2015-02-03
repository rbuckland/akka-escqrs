package io.straight.escqrs.model.validation

import io.straight.escqrs.model.DomainError

/**
 * @author rbuckland
 */
package object simple {

  type EitherDomainValidation[+T] = Either[DomainError,T]

}
