
package controllers

import akka.stream.Materializer
import models._
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

class AssignmentControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val formEg: FormEg = mock[FormEg]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val userDataServices: UserDataServices = mock[UserDataServices]
  val assignmentServices = mock[AssignmentServices]

  val assignmentController: AssignmentController = new AssignmentController(messagesApi, formEg,assignmentServices, userDataServices)

  val assignment1 = AssignmentForm("Play","Play Assignment")
  val assignmentForm: Form[AssignmentForm] = new FormEg().AssignmentConstraints.fill(assignment1)

  implicit lazy val materializer: Materializer = app.materializer


  "Assignment Testing - Add Assignment" should {

    "be added" in {
      when(formEg.AssignmentConstraints).thenReturn(assignmentForm)
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(assignmentServices.store(ArgumentMatchers.any(classOf[Assignment]))).thenReturn(Future(true))

      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "Play", "description" -> "Play Assignment").withSession("user"->"Aakash06"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showAddAssignment")
    }

    "not added" in {
      when(formEg.AssignmentConstraints).thenReturn(assignmentForm)
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(assignmentServices.store(ArgumentMatchers.any(classOf[Assignment]))).thenReturn(Future(false))

      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "Play", "description" -> "Play Assignment").withSession("user"->"Aakash06"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showAddAssignment")
    }

    "not admin" in {
      when(formEg.AssignmentConstraints).thenReturn(assignmentForm)
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))

      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "Play", "description" -> "Play Assignment").withSession("user"->"Aakash06"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "form with errors" in {
      when(formEg.AssignmentConstraints).thenReturn(assignmentForm)
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "", "description" -> "").withSession("user"->"Aakash06"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showAddAssignment")
    }

    "No session" in {
      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "", "description" -> "Play Assignment").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }
  }



  "Assignment Testing - View Assignment User" should {

    "display list of Assignments" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
      when(assignmentServices.returnAll()).thenReturn(Future(List(Assignment(1,"Play","PlayAssignment"))))

      val result = call(assignmentController.viewAssignmentUser, FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 200
    }

    "No assignments for User" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))
      when(assignmentServices.returnAll()).thenReturn(Future(Nil))

      val result = call(assignmentController.viewAssignmentUser, FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 200
    }

    "if User is admin" in {

      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))

      val result = call(assignmentController.viewAssignmentUser, FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }


    "No session" in {
      val result = call(assignmentController.viewAssignmentUser, FakeRequest(GET, "/").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }
  }



  "Assignment Testing - View Assignment Admin" should {

    "display list of Assignments" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(assignmentServices.returnAll()).thenReturn(Future(List(Assignment(1,"Play","PlayAssignment"))))

      val result = call(assignmentController.viewAssignmentAdmin, FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 200
    }

    "No assignments by Admin" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(assignmentServices.returnAll()).thenReturn(Future(Nil))

      val result = call(assignmentController.viewAssignmentAdmin, FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 200
    }

    "if he/she is not admin" in {

      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))

      val result = call(assignmentController.viewAssignmentAdmin, FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }


    "No session" in {
      val result = call(assignmentController.viewAssignmentAdmin, FakeRequest(GET, "/").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }
  }

  "Assignment Testing - Delete Assignment Admin" should {

    "Successfully Assignment is deleted" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(assignmentServices.delete(1)).thenReturn(Future(true))

      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 303
    }

    "not be able to delete" in {
      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(true))
      when(assignmentServices.delete(1)).thenReturn(Future(false))

      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 303
    }

    "if he/she is not admin" in {

      when(userDataServices.checkAdmin("Aakash06")).thenReturn(Future(false))

      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/").withSession("user"->"Aakash06"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }


    "No session" in {
      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/").withSession())
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }
  }
}
