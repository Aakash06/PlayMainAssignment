package controllers

import javax.inject.Inject

import models.{UserData, UserDataServices}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdminController @Inject()(val messagesApi: MessagesApi, userDataServices: UserDataServices)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def userEnableUpdate: Action[AnyContent] = Action.async{ implicit request =>
    val username = request.session.get("user")
    username match {
      case Some(user)=>
        userDataServices.checkAdmin(user).flatMap{
          case true => userDataServices.retrieveAll().map {
            userlist =>
              val onlyUsers: List[UserData] = userlist.filter(!_.isAdmin)
              Logger.info("vjjvvjhv")
              Ok(views.html.UserList(onlyUsers))
          }
          case false =>   Future.successful(Redirect(routes.Application.login()).flashing("Error"->"No Session").withNewSession)
        }

      case None => Future.successful(Redirect(routes.Application.login()).flashing("Error"->"No Session").withNewSession)
      }
    }



  def disableUser(userName:String): Action[AnyContent] = Action.async { implicit request=>
    val adminName = request.session.get("user")
    adminName match {
      case Some(user)=>
        userDataServices.checkAdmin(user).flatMap{
          case true => userDataServices.makeDisable(userName).map{
            case true => Redirect(routes.AdminController.userEnableUpdate()).flashing("Success"->"Successfully Enabled")
            case false =>  Redirect(routes.AdminController.userEnableUpdate()).flashing("Error"-> s"Error while Enabling $userName")
          }

          case false =>   Future.successful(Redirect(routes.Application.login()).flashing("Error"->"No Session").withNewSession)
        }

      case None => Future.successful(Redirect(routes.Application.login()).flashing("Error"->"No Session").withNewSession)
    }

  }

  def enableUser(userName:String): Action[AnyContent] = Action.async { implicit request=>
    val adminName = request.session.get("user")
    adminName match {
      case Some(user)=>
        userDataServices.checkAdmin(user).flatMap{
          case true => userDataServices.makeEnable(userName).map{
            case true => Redirect(routes.AdminController.userEnableUpdate()).flashing("Success"->s"Successfully Enabled $userName")
            case false =>  Redirect(routes.AdminController.userEnableUpdate()).flashing("Error"-> s"Error while Enabling $userName")
          }

          case false =>   Future.successful(Redirect(routes.Application.login()).flashing("Error"->"No Session").withNewSession)
        }

      case None => Future.successful(Redirect(routes.Application.login()).flashing("Error"->"No Session").withNewSession)
    }

  }
}
