package controllers

import com.google.inject.Inject
import models.{HobbyServices, UserDataServices}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.ExecutionContext.Implicits.global


class Application @Inject()(implicit val messagesApi: MessagesApi, userDataServices: UserDataServices,hobbyServices: HobbyServices ) extends Controller with I18nSupport {

  val userForm: FormEg = new FormEg

  def index1 = Action { implicit request =>
    Ok(views.html.index())
  }
  def index: Action[AnyContent] = Action {
          Ok(views.html.home())
  }
  def signUp: Action[AnyContent] = Action.async { implicit request =>
    hobbyServices.returnAll().map { e =>
      Ok(views.html.formEg(userForm.userConstraints,e)).withNewSession
    }
  }

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.Login(userForm.loginConstraints)).withNewSession
  }

  def updatePass: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.passwordUpdate(userForm.updatePasswordConstraints)).withNewSession
  }


}
