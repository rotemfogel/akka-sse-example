package me.rotemfo.sse

import org.json4s.{Extraction, Formats, NoTypeHints, StringInput}
import org.json4s.native.Serialization
import org.json4s.native.JsonMethods._

case class User(id: Int, name: String) {

  override def toString: String = {
    import User._
    compact(render(Extraction.decompose(this)))
  }
}

object User {
  implicit val formats: Formats = Serialization.formats(NoTypeHints).skippingEmptyValues

  def apply(s: String): User = {
    parse(StringInput(s)).extract[User]
  }
}