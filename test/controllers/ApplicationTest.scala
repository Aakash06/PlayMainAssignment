package controllers

import akka.stream.Materializer
import models._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.test.WithApplication
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.data.{Field, Form}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApplicationTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val formEg: FormEg = mock[FormEg]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val userDataServices: UserDataServices = mock[UserDataServices]
  val hobbyServices: HobbyServices = mock[HobbyServices]

  val user: User = User("Aakash", None, "Jain", "Aakash06", "Aakash06", "Aakash06", 8447018441L, "male", 21, List(1, 2))
  val userForm: Form[User] = new FormEg().userConstraints.fill(user)

  val loginvalues: LoginUser = LoginUser("Aakash06", "Aakash06")
  val loginForm: Form[LoginUser] = new FormEg().loginConstraints.fill(loginvalues)

  val updatePasswordvalues = UpdatePassword("Aakash", "Aakash06", "Aakash06")
  val passwordForm: Form[UpdatePassword] = new FormEg().updatePasswordConstraints.fill(updatePasswordvalues)

  val AssignmentValues= AssignmentForm("Play","Play Assignment")
  val assignmentform = new FormEg().AssignmentConstraints.fill(AssignmentValues)


  implicit lazy val materializer: Materializer = app.materializer

  val application: Application = new Application(messagesApi, hobbyServices, formEg, userDataServices)

  "Testing Application" should {

    "be able to render to Admin Profile page" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      val result = call(application.index1, FakeRequest(GET, "/").withSession("user" -> "Aakash06"))
      status(result) mustBe 200
    }

    "be able to login Page if not admin" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
      val result = call(application.index1, FakeRequest(GET, "/").withSession("user" -> "Aakash06"))
      status(result) mustBe 303
    }

    "No session in index" in {
      val result = call(application.index1, FakeRequest(GET, "/").withSession())
      status(result) mustBe 303
    }

    "be able to render to  User Profile page" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
      val result = call(application.loginUser, FakeRequest(GET, "/").withSession("user" -> "Aakash06"))
      status(result) mustBe 200
    }

    "be not able to user profile page if not admin" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      val result = call(application.loginUser, FakeRequest(GET, "/").withSession("user" -> "Aakash06"))
      status(result) mustBe 303
    }

    "No session in login user" in {
      val result = call(application.loginUser, FakeRequest(GET, "/").withSession())
      status(result) mustBe 303
    }

    "displayed home page" in {
      val result = call(application.index, FakeRequest(GET, "/").withSession())
      status(result) mustBe 200
    }

    "displayed sign up page" in {
      when(formEg.userConstraints).thenReturn(userForm)
      when(hobbyServices.returnAll()).thenReturn(Future(Nil))
      val result = call(application.signUp, FakeRequest(GET, "/").withSession())
      status(result) mustBe 200
    }

    "displayed login page" in {
      when(formEg.loginConstraints).thenReturn(loginForm)
      val result = call(application.login, FakeRequest(GET, "/").withSession())
      status(result) mustBe 200
    }

    "displayed update password page" in {
      when(formEg.updatePasswordConstraints).thenReturn(passwordForm)
      val result = call(application.updatePass, FakeRequest(GET, "/").withSession())
      status(result) mustBe 200
    }

    "be able to render to Assignment page" in {
      when(formEg.AssignmentConstraints).thenReturn(assignmentform)
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))

      val result = call(application.showAddAssignment, FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 200
    }

    "be not able to show page if not admin" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
      val result = call(application.showAddAssignment, FakeRequest(GET, "/").withSession("user" -> "Aakash06"))
      status(result) mustBe 303
    }

    "No session in showAddAssignment" in {
      val result = call(application.showAddAssignment, FakeRequest(GET, "/").withSession())
      status(result) mustBe 303
    }

  }
}
