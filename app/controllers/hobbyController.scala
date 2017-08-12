package controllers

import javax.inject.Inject

import models.HobbyServices
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global

class hobbyController @Inject()(val messagesApi: MessagesApi,hobbyServices: HobbyServices)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def allData = Action.async { implicit request =>
  hobbyServices.returnAll().map { e =>
    Ok(views.html.allpage(e))
    }
  }

}
