package controllers

import javax.inject._
import models.{UserData, UserDataServices}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Authentication @Inject()(val messagesApi: MessagesApi, formEg: FormEg, userDataServices: UserDataServices)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def createUser: Action[AnyContent] = Action.async { implicit request =>
    formEg.userConstraints.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Error while creating an account :" + formWithErrors)
        Future.successful(Redirect(routes.Application.signUp()).flashing("Error"-> "Fill Form Correctly"))
      },
      userData => {
        userDataServices.findByUsername(userData.username).flatMap{

          case Some(userNamefromData) => Logger.error("Username already exist, try with new one" + userNamefromData)
            Future.successful(Redirect(routes.Application.signUp()).flashing("Error"->"userName Already exists"))

          case None => Logger.info("UserInfo after creating an account : " + userData)
            userDataServices.store(UserData(0,userData.firstName,userData.middleName,userData.lastName,
              userData.username,userData.password,userData.phoneNumber,userData.gender,userData.age))
              .map {
                case true =>
                  Redirect(routes.Application.index1()).flashing("Success" -> "Thank You for registration").withSession("user"->userData.username)
                case false =>   Redirect(routes.Application.index1()).flashing("Error" -> "Error while registration")
        }
        }
      })
  }

  def loginCheck: Action[AnyContent] = Action.async { implicit request =>
    formEg.loginConstraints.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Error while login : " + formWithErrors)
        Future.successful(Redirect(routes.Application.login()))
      }, userData => {
        Logger.info("After Login : " + userData)
        Future.successful(Redirect(routes.Application.index1()))
      })
  }



}
