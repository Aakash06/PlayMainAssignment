package controllers

import javax.inject._

import org.mindrot.jbcrypt.BCrypt
import models.{HobbyServices, UserData, UserDataServices, UsertoHobbyServices}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Authentication @Inject()(val messagesApi: MessagesApi, formEg: FormEg, userDataServices: UserDataServices,
      usertoHobbyServices : UsertoHobbyServices,hobbyServices: HobbyServices)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def createUser: Action[AnyContent] = Action.async { implicit request =>
    formEg.userConstraints.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Error while creating an account :" + formWithErrors)
       Future.successful(Redirect(routes.Application.signUp()).flashing("Error" -> "Fill Form Correctly"))
       /*hobbyServices.returnAll().map{ e =>
        BadRequest(views.html.formEg(formWithErrors,e))}*/
      },
      userData => {
        userDataServices.findByUsername(userData.username).flatMap {

          case Some(userNamefromData) => Logger.error("Username already exist, try with new one" + userNamefromData)
            Future.successful(Redirect(routes.Application.signUp()).flashing("Error" -> "userName Already exists"))

          case None => Logger.info("UserInfo after creating an account : " + userData)
            val encryptPassword = BCrypt.hashpw(userData.password, BCrypt.gensalt)
            userDataServices.store(UserData(0, userData.firstName, userData.middleName, userData.lastName,
              userData.username, encryptPassword, userData.phoneNumber, userData.gender, userData.age, false, true))
              .flatMap { case true =>
                  Logger.info("Finding UsernameID")
                  userDataServices.findByUsernameGetId(userData.username).flatMap {
                    case Some(id) => usertoHobbyServices.store(id, userData.hobbyID).map {
                      case true =>
                        Logger.info("Redirecting to home page")
                        Redirect(routes.Application.index1())
                        .flashing("Success" -> "Thank You for registration").withSession("user" -> userData.username)
                      case false => Redirect(routes.Application.signUp()).flashing("Error" -> "Error while linking hobbies")
                    }
                    case None => Future.successful(Redirect(routes.Application.signUp()).flashing("Error" -> "Error while registration"))
                  }
                case false => Future.successful(Redirect(routes.Application.signUp()).flashing("Error" -> "Error while registration"))
              }
        }
      })
  }

  def loginCheck: Action[AnyContent] = Action.async { implicit request =>
    formEg.loginConstraints.bindFromRequest.fold(
        formWithErrors => {
          Logger.error("Error while login : " + formWithErrors)
          Future.successful(Redirect(routes.Application.login()).flashing("Error"->"Fill Form Correctly"))
        },
    userData => {
          userDataServices.findByUsername(userData.username).flatMap{

            case Some(usernamee)=>
              Logger.info("Checking In Database For login" + usernamee)
              userDataServices.checkloginValue(userData.username,userData.password).flatMap
                 {
                   case true=> userDataServices.checkAdmin(userData.username).flatMap{

                     case true =>Future.successful(Redirect(routes.Application.index1())
                       .flashing("Success" -> "Thank You for registration As admin").withSession("user"->userData.username))

                     case false=> userDataServices.checkEnable(userData.username).map{

                      case true => Redirect(routes.Application.loginUser())
                        .flashing("Success" -> "Thank You for registration as user").withSession("user"->userData.username)

                      case false => Redirect(routes.Application.login())
                        .flashing("Error"->"Your are disable")
                      }

                    }
                     case false => Future.successful(Redirect(routes.Application.login()).flashing("Error"->"Wrong Username or Password"))
                  }
             case None => Future.successful(Redirect(routes.Application.login()).flashing("Error"-> "No user by this username"))
            }
        })
    }
}
