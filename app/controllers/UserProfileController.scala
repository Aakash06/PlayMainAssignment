package controllers

import javax.inject._

import models.{HobbyServices, UserData, UserDataServices, UsertoHobbyServices}
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserProfileController @Inject()(val messagesApi: MessagesApi, formEg: FormEg, userDataServices: UserDataServices,
       usertoHobbyServices : UsertoHobbyServices,hobbyServices: HobbyServices)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def showProfile: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>

    val user = request.session.get("user")
    user match {
      case Some(username) => userDataServices.retrieve(username).flatMap {
        case Nil =>
          Logger.info("No user by this UserName")
          Future.successful(Redirect(routes.Application.index()))

        case userList: List[UserData] =>
          val user = userList.head
          usertoHobbyServices.getUserHobby(user.id).flatMap {
            case Nil =>
              Logger.info("Did not receive any hobbies for the user!")
              Future.successful(Redirect(routes.Application.index()))
            case hobbies: List[Int] =>
              val userProfile = UserProfileData(user.firstname, user.middlename, user.lastname, user.mobilenumber,
                user.age, user.gender, hobbies)
              hobbyServices.returnAll().flatMap{ hobby =>
              userDataServices.checkAdmin(username).map{
                case true =>Ok(views.html.updateForm(formEg.userProfileDataForm.fill(userProfile),hobby,true))
                case false =>Ok(views.html.updateForm(formEg.userProfileDataForm.fill(userProfile),hobby,false))
              }

              }
          }
      }

      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }


  def updateUserProfile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val username = request.session.get("user")
    username match {
      case Some(user) =>
        formEg.userProfileDataForm.bindFromRequest.fold(
          formWithErrors => {
            Logger.info("dvdvd" + formWithErrors)
            hobbyServices.returnAll().flatMap {
              hobbies =>
                userDataServices.checkAdmin(user).map {
                  case true => BadRequest(views.html.updateForm(formWithErrors, hobbies, true))
                  case false => BadRequest(views.html.updateForm(formWithErrors, hobbies, false))
                }
            }
          },
          userProfile => {
            Logger.info("Updating Your Data")
            userDataServices.updateInfo(userProfile, user).flatMap {
              case true => userDataServices.findByUsernameGetId(user).flatMap{
                case Some(id)=> usertoHobbyServices.updateUserHobby(id,userProfile.hobbies).map{
                  case true => Logger.info("Updated hobbies")
                    Redirect(routes.UserProfileController.showProfile()).flashing("Success"->"Your Profile is updated")
                  case false=>Redirect(routes.UserProfileController.showProfile()).flashing("Error"->"Error while Updating your hobbies")
                }

                case None => Future.successful(Redirect(routes.Application.index()).withNewSession)
              }
              case false => Future.successful(Redirect(routes.Application.index()).withNewSession)
            }
          })

      case None => Future.successful(Redirect(routes.Application.index1()).flashing("unauthorised" -> "You need to log in first!").withNewSession)
    }
  }

  def updatePassword(): Action[AnyContent] =Action.async{ implicit request =>
    formEg.updatePasswordConstraints.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Error while creating an account :" + formWithErrors)
        Future.successful(Redirect(routes.Application.updatePass()).flashing("Error" -> "Fill Form Correctly"))
      },
      userUpdatePassword => {
        userDataServices.findByUsername(userUpdatePassword.userName).flatMap {

          case Some(userNameFromData) => Logger.info("Username is found" + userNameFromData)

            val encryptPassword = BCrypt.hashpw(userUpdatePassword.password, BCrypt.gensalt)

             userDataServices.updateUserPassword(encryptPassword,userUpdatePassword.userName).map{

              case true => Logger.info("Password changed")
                Redirect(routes.Application.login()).withNewSession

              case false => Logger.error("Error while updating Password")
                Redirect(routes.Application.updatePass()).flashing("Error"->"Try Again")
            }
          case None => Logger.info("No User by this username")
            Future.successful(Redirect(routes.Application.updatePass()).flashing("Error" -> "No User by this username"))
        }

      })

  }

}
