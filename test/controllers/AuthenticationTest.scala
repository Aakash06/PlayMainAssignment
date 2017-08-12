package controllers

import akka.stream.Materializer
import models.{UserData, UserDataServices}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.test.WithApplication

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val formEg: FormEg = mock[FormEg]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val userDataServices: UserDataServices = mock[UserDataServices]

  val authentication: Authentication = new Authentication(messagesApi, formEg, userDataServices)
  val user: User = User("Aakash", None, "Jain", "Aakash06", "Aakash06", "Aakash06",8447018441L,  "male", 21)
  val userForm: Form[User] = new FormEg().userConstraints.fill(user)
  implicit lazy val materializer: Materializer = app.materializer

  "Authentication Testing" should  {

    "Account should be created" in {

      when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(None))
      when(formEg.userConstraints).thenReturn(userForm)
      when(userDataServices.store(UserData(0, "Aakash", None, "Jain", "Aakash06", "Aakash06",8447018441L,"male",21)))
        .thenReturn(Future(true))

      val result = call(authentication.createUser(),FakeRequest(POST,"/signUp").withFormUrlEncodedBody(
        "firstName" -> "Aakash", "middleName" -> "","lastName" -> "Jain","username" -> "Aakash06", "password" -> "Aakash06",
        "confirmPassword" -> "Aakash06","phoneNumber"->"8447018441","gender" -> "male","age" -> "21"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/afterSuccess")

    }
  }

}
