package controllers.network

import javax.inject._

import play.api.mvc._
import utils.response.ResponseHelper._
import utils.StringHelper._
import services._

import scala.concurrent.Future

class KarateExampleController @Inject()(cc: ControllerComponents
                                 ) extends AbstractController(cc) {
  val networkService = new NetworkService

  def getInstance = Action.async { implicit request: Request[AnyContent] =>
    val network = networkService.getKarateNetwork()
    Future.successful {
      Ok {
        network.toJson
      }.resultAsJson
    }
  }
}