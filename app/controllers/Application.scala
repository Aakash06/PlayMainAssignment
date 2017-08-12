package controllers

import com.google.inject.Inject
import models.UserDataServices
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}

class Application @Inject()(implicit val messagesApi: MessagesApi, userDataServices: UserDataServices ) extends Controller with I18nSupport {

  val userForm: FormEg = new FormEg

  def index1 = Action { implicit request =>
    Ok(views.html.index())
  }
  def index: Action[AnyContent] = Action {
          Ok(views.html.home())
  }
  def signUp: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.formEg(userForm.userConstraints))
  }
  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.Login(userForm.loginConstraints))
  }

}
