package controllers

import scala.concurrent.Future
import javax.inject.Inject

import utils.response.ResponseHelper._
import utils.StringHelper.ToJson
import play.api._
import play.api.i18n.{Messages, MessagesApi}

import play.api.mvc.{Action, _}

/**
  * The basic application controller.
  *
  */
class ApplicationController @Inject()(
                                       cc: ControllerComponents) extends AbstractController(cc) {
  def index = Action.async { implicit request =>
    Future.successful {
      Ok {
        Map[String, Any]("status" -> true, "message" -> "Hello! We aim to provide API services for network science education.").toJson
      }.resultAsJson
    }
  }
}