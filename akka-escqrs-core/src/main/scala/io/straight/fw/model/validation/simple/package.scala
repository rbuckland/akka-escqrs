package io.straight.fw.model.validation

import io.straight.fw.model.DomainError

/**
 * @author rbuckland
 */
package object simple {

  type EitherDomainValidation[+T] = Either[DomainError,T]

}
