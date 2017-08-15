
package controllers

import akka.stream.Materializer
import models.{HobbyServices, UserData, UserDataServices, UsertoHobbyServices}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.test.WithApplication
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val hobbyServices: HobbyServices = mock[HobbyServices]
  val formEg: FormEg = mock[FormEg]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val userDataServices: UserDataServices = mock[UserDataServices]
  val usertoHobbyServices : UsertoHobbyServices = mock[UsertoHobbyServices]
  val authentication: Authentication = new Authentication(messagesApi, formEg, userDataServices,usertoHobbyServices,hobbyServices)

  val user: User = User("Aakash", None, "Jain", "Aakash06", "Aakash06", "Aakash06", 8447018441L,  "male", 21, List(1,2))
  val loginvalues = LoginUser("Aakash06","Aakash06")
  val userForm: Form[User] = new FormEg().userConstraints.fill(user)
  val loginForm:Form[LoginUser]=new FormEg().loginConstraints.fill(loginvalues)
  implicit lazy val materializer: Materializer = app.materializer

  "Authentication Testing - Signup" should {

    "Account should be created" in {
      when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(None))
      when(formEg.userConstraints).thenReturn(userForm)
      when(userDataServices.store(ArgumentMatchers.any(classOf[UserData])))
        .thenReturn(Future(true))
      when(userDataServices.findByUsernameGetId("Aakash06")).thenReturn(Future(Some(1)))
      when(usertoHobbyServices.store(1, List(1, 2))).thenReturn(Future(true))

      val result = call(authentication.createUser(), FakeRequest(POST, "/signUp").withFormUrlEncodedBody(
        "firstName" -> "Aakash", "middleName" -> "", "lastName" -> "Jain", "username" -> "Aakash06", "password" -> "Aakash06",
        "confirmPassword" -> "Aakash06", "phoneNumber" -> "8447018441", "gender" -> "male", "age" -> "21", "hobbyID[0]" -> "1", "hobbyID[1]" -> "2"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/admin")

    }

    "Error while storing hobbies in Database" in {
      when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(None))
      when(formEg.userConstraints).thenReturn(userForm)
      when(userDataServices.store(ArgumentMatchers.any(classOf[UserData])))
        .thenReturn(Future(true))
      when(userDataServices.findByUsernameGetId("Aakash06")).thenReturn(Future(Some(1)))
      when(usertoHobbyServices.store(1, List(1, 2))).thenReturn(Future(false))

      val result = call(authentication.createUser(), FakeRequest(POST, "/signUp").withFormUrlEncodedBody(
        "firstName" -> "Aakash", "middleName" -> "", "lastName" -> "Jain", "username" -> "Aakash06", "password" -> "Aakash06",
        "confirmPassword" -> "Aakash06", "phoneNumber" -> "8447018441", "gender" -> "male", "age" -> "21", "hobbyID[0]" -> "1", "hobbyID[1]" -> "2"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/signUp")

    }

    "Error while id is not found in database" in {
      when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(None))
      when(formEg.userConstraints).thenReturn(userForm)
      when(userDataServices.store(ArgumentMatchers.any(classOf[UserData])))
        .thenReturn(Future(true))
      when(userDataServices.findByUsernameGetId("Aakash06")).thenReturn(Future(None))

      val result = call(authentication.createUser(), FakeRequest(POST, "/signUp").withFormUrlEncodedBody(
        "firstName" -> "Aakash", "middleName" -> "", "lastName" -> "Jain", "username" -> "Aakash06", "password" -> "Aakash06",
        "confirmPassword" -> "Aakash06", "phoneNumber" -> "8447018441", "gender" -> "male", "age" -> "21", "hobbyID[0]" -> "1", "hobbyID[1]" -> "2"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/signUp")

    }


    "Error while storing in database" in {
      when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(None))
      when(formEg.userConstraints).thenReturn(userForm)
      when(userDataServices.store(ArgumentMatchers.any(classOf[UserData])))
        .thenReturn(Future(false))
      val result = call(authentication.createUser(), FakeRequest(POST, "/signUp").withFormUrlEncodedBody(
        "firstName" -> "Aakash", "middleName" -> "", "lastName" -> "Jain", "username" -> "Aakash06", "password" -> "Aakash06",
        "confirmPassword" -> "Aakash06", "phoneNumber" -> "8447018441", "gender" -> "male", "age" -> "21", "hobbyID[0]" -> "1", "hobbyID[1]" -> "2"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/signUp")

    }

    "Username already Exists in database" in {
      when(formEg.userConstraints).thenReturn(userForm)
      when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(Some("Aakash06")))

      val result = call(authentication.createUser(), FakeRequest(POST, "/signUp").withFormUrlEncodedBody(
        "firstName" -> "Aakash", "middleName" -> "", "lastName" -> "Jain", "username" -> "Aakash06", "password" -> "Aakash06",
        "confirmPassword" -> "Aakash06", "phoneNumber" -> "8447018441", "gender" -> "male", "age" -> "21", "hobbyID[0]" -> "1", "hobbyID[1]" -> "2"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/signUp")

    }

    "Form With errors" in {
      when(formEg.userConstraints).thenReturn(userForm)
      when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(Some("Aakash06")))

      val result = call(authentication.createUser(), FakeRequest(POST, "/signUp").withFormUrlEncodedBody(
        "firstName" -> "Aakash", "middleName" -> "", "lastName" -> "Jain", "username" -> "Aakash06", "password" -> "Aakash06",
        "confirmPassword" -> "Aakash06", "phoneNumber" -> "84470184", "gender" -> "male", "age" -> "95", "hobbyID[0]" -> "1", "hobbyID[1]" -> "2"))

      status(result) mustBe 303
      //redirectLocation(result) mustBe Some("/signUp")

    }
  }

    "Authentication Testing - Login" should {

      "Login as user" in {

        when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(Some("Aakash06")))
        when(formEg.loginConstraints).thenReturn(loginForm)
        when(userDataServices.checkloginValue("Aakash06", "Aakash06")).thenReturn(Future(true))
        when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
        when(userDataServices.checkEnable("Aakash06")).thenReturn(Future(true))

        val result = call(authentication.loginCheck(), FakeRequest(POST, "/login").withFormUrlEncodedBody(
          "username" -> "Aakash06", "password" -> "Aakash06"))
        status(result) mustBe 303
        redirectLocation(result) mustBe Some("/user")

      }

      "User is disable" in {

        when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(Some("Aakash06")))
        when(formEg.loginConstraints).thenReturn(loginForm)
        when(userDataServices.checkloginValue("Aakash06", "Aakash06")).thenReturn(Future(true))
        when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
        when(userDataServices.checkEnable("Aakash06")).thenReturn(Future(false))

        val result = call(authentication.loginCheck(), FakeRequest(POST, "/login").withFormUrlEncodedBody(
          "username" -> "Aakash06", "password" -> "Aakash06"))
        status(result) mustBe 303
        redirectLocation(result) mustBe Some("/login")

      }

      "Login as admin" in {

        when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(Some("Aakash06")))
        when(formEg.loginConstraints).thenReturn(loginForm)
        when(userDataServices.checkloginValue("Aakash06", "Aakash06")).thenReturn(Future(true))
        when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))

        val result = call(authentication.loginCheck(), FakeRequest(POST, "/login").withFormUrlEncodedBody(
          "username" -> "Aakash06", "password" -> "Aakash06"))
        status(result) mustBe 303
        redirectLocation(result) mustBe Some("/admin")

      }

      "Username and password doesn't match" in {

        when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(Some("Aakash06")))
        when(formEg.loginConstraints).thenReturn(loginForm)
        when(userDataServices.checkloginValue("Aakash06", "Aakash06")).thenReturn(Future(false))

        val result = call(authentication.loginCheck(), FakeRequest(POST, "/login").withFormUrlEncodedBody(
          "username" -> "Aakash06", "password" -> "Aakash06"))
        status(result) mustBe 303
        redirectLocation(result) mustBe Some("/login")

      }

      "Username not found" in {

        when(userDataServices.findByUsername("Aakash06")).thenReturn(Future(None))
        when(formEg.loginConstraints).thenReturn(loginForm)

        val result = call(authentication.loginCheck(), FakeRequest(POST, "/login").withFormUrlEncodedBody(
          "username" -> "Aakash06", "password" -> "Aakash06"))
        status(result) mustBe 303
        redirectLocation(result) mustBe Some("/login")

      }

      "Login form with error" in {

        when(formEg.loginConstraints).thenReturn(loginForm)

        val result = call(authentication.loginCheck(), FakeRequest(POST, "/login").withFormUrlEncodedBody(
          "username" -> "Aakash06", "password" -> "kash"))
        status(result) mustBe 303
        redirectLocation(result) mustBe Some("/login")
      }

    }

}
