package utils.response

import play.api.mvc.Result

package object ResponseHelper {
  implicit class ResponseTypeHelper(r: Result) {
    def resultAsJson = r.as("application/json; charset=utf-8")
  }
}
