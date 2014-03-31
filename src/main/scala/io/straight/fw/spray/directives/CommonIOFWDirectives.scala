package io.straight.fw.spray.directives

import spray.routing._
import shapeless._
import io.straight.fw.model.Uuid
import shapeless.::
import scala.Some
import java.util.UUID

/**
 * @author rbuckland
 */
trait CommonIOFWDirectives extends HttpService {

  val longAnyMatcher = LongNumber.asInstanceOf[PathMatcher[Any :: HNil]]
  val uuidAnyMatcher = JavaUUID.asInstanceOf[PathMatcher[Any :: HNil]]
  val hexLongMatcher = HexIntNumber.asInstanceOf[PathMatcher[Any :: HNil]]

  /**
   * A PathMatcher that matches and extracts a java.util.UUID instance.
   */
  val IOFWUuid: PathMatcher1[Uuid] =
    PathMatcher("""[\da-fA-F]{8}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{12}""".r) flatMap { string ⇒
      try Some(Uuid.fromString(string))
      catch { case _: IllegalArgumentException ⇒ None }
    }


}
