package controllers

import javax.inject.Inject
import models.{HobbyServices, UserDataServices, UsertoHobbyServices}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

class AdminController @Inject()(val messagesApi: MessagesApi, formEg: FormEg, userDataServices: UserDataServices,
  usertoHobbyServices : UsertoHobbyServices,hobbyServices: HobbyServices)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def userEnableUpdate = Action.async{
    userDataServices.retrieveAll().map{
      userlist=> Ok(views.html.UserList(userlist))
      }
    }
  }
