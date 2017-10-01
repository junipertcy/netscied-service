package utils

import utils.json.DateTimeSerializer

import net.liftweb.json.{ Formats }

object StringHelper {
  lazy val jsonFormatsWithDateTime: Formats = net.liftweb.json.DefaultFormats + DateTimeSerializer()
  def toJson(a: Any, compact: Boolean = true) = {
    implicit val formats = jsonFormatsWithDateTime
    if (compact) net.liftweb.json.Printer.compact(net.liftweb.json.JsonAST.render(
      net.liftweb.json.Extraction.decompose(a)))
    else net.liftweb.json.Printer.pretty(net.liftweb.json.JsonAST.render(
      net.liftweb.json.Extraction.decompose(a)))
  }

  def fromJson(s: String) = {
    //implicit val formats = net.liftweb.json.DefaultFormats
    implicit val formats = jsonFormatsWithDateTime
    net.liftweb.json.JsonParser.parse(s).values
  }

  implicit class ToJson(a: Any) {
    def toJson: String = toJson()

    def toJson(compact: Boolean = true): String = {
      StringHelper.toJson(a, compact)
    }
  }

}
