package controllers

import com.google.inject.Inject
import models.{HobbyServices, UserDataServices}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Application @Inject()(implicit val messagesApi: MessagesApi,
      hobbyServices: HobbyServices,formEg:FormEg,userDataServices:UserDataServices ) extends Controller with I18nSupport {


  def index1: Action[AnyContent] = Action.async { implicit request =>
    val username = request.session.get("user")
    username match {
      case Some(user) =>
        userDataServices.checkAdmin(user).map {
          case true => Ok(views.html.index())
          case false => Redirect(routes.Application.login()).withNewSession
        }
      case None => Future.successful(Redirect(routes.Application.login()).withNewSession)
    }
  }
  def loginUser: Action[AnyContent] =Action.async{
    implicit request =>
      val username = request.session.get("user")
      username match {
        case Some(user) =>
          userDataServices.checkAdmin(user).map {
            case true => Redirect(routes.Application.login()).withNewSession
            case false =>Ok(views.html.indexLogin())
          }
        case None => Future.successful(Redirect(routes.Application.login()).withNewSession)
      }
  }

  def index: Action[AnyContent] = Action {
    Ok(views.html.home())
  }

  def signUp: Action[AnyContent] = Action.async { implicit request =>
    hobbyServices.returnAll().map { e =>
      Ok(views.html.formEg(formEg.userConstraints, e)).withNewSession
    }
  }

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.Login(formEg.loginConstraints)).withNewSession
  }

  def updatePass: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.passwordUpdate(formEg.updatePasswordConstraints)).withNewSession
  }

  def showAddAssignment: Action[AnyContent] = Action.async { implicit request =>
    val username = request.session.get("user")
    username match {
      case Some(user) =>
        userDataServices.checkAdmin(user).map {
          case true => Ok(views.html.AddAssignment(formEg.AssignmentConstraints))
          case false => Redirect(routes.Application.login()).withNewSession
        }
      case None => Future.successful(Redirect(routes.Application.login()).withNewSession)
    }
  }
}
