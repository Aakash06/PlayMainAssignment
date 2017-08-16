
package controllers

import akka.stream.Materializer
import models.{UserData, UserDataServices}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdminControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val messagesApi: MessagesApi = mock[MessagesApi]
  val userDataServices: UserDataServices = mock[UserDataServices]

  val user = UserData(1,"Aakash",None,"Jain","Aakash06","Aakash06",8447018441L,"male",22,false,true)
  val adminController = new AdminController(messagesApi,userDataServices)

  implicit lazy val materializer: Materializer = app.materializer

  "AdminController Testing - UpdatenableDiable" should {

    "List Should be displayed" in {

      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(userDataServices.retrieveAll()).thenReturn(Future(List(user)))

      val result = call(adminController.userEnableUpdate, FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 200
     // redirectLocation(result) mustBe Some("/userList")
    }

    "When user is not admin-enableDisable" in {

      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))

      val result = call(adminController.userEnableUpdate, FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "When there is No session-enableDisable" in {

      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))

      val result = call(adminController.userEnableUpdate, FakeRequest(GET, "/").withSession())
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "be disabled" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(userDataServices.makeDisable("Aakash06")).thenReturn(Future(true))

      val result = call(adminController.disableUser("Aakash06"), FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 303

    }

    "not able to disabled" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(userDataServices.makeDisable("Aakash06")).thenReturn(Future(false))
      val result = call(adminController.disableUser("Aakash06"), FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 303
    }

    "When there is No session-disable" in {

      val result = call(adminController.disableUser("Aakash06"), FakeRequest(GET, "/").withSession())
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "not admin-disable" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
      val result = call(adminController.disableUser("Aakash06"), FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "be enabled" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(userDataServices.makeEnable("Aakash06")).thenReturn(Future(true))

      val result = call(adminController.enableUser("Aakash06"), FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 303

    }

    "not able to enabled" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(userDataServices.makeEnable("Aakash06")).thenReturn(Future(false))
      val result = call(adminController.enableUser("Aakash06"), FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 303
    }

    "When there is No session-enable" in {

      val result = call(adminController.enableUser("Aakash06"), FakeRequest(GET, "/").withSession())
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "not admin-enable" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
      val result = call(adminController.enableUser("Aakash06"), FakeRequest(GET, "/").withSession("user"->"Aakash06"))
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

}
