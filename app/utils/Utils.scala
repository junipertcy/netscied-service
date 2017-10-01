package utils

import scala.util.control.Exception

object Utils extends Awakable {
  object SafeExec {
    def apply[R](body: => R): Option[R] = Exception.catching(classOf[Throwable]) opt body
  }
}
