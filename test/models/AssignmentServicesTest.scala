package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class AssignmentServicesTest extends PlaySpec with MockitoSugar {

  val assignmentServices: ModelsTest[AssignmentServices] = new ModelsTest[AssignmentServices]

  val assignment1: Assignment = Assignment(1,"Play","Play Assignment")
  val assignment2: Assignment =Assignment(2,"Akka","Akka Testing")


  "Testing Assignment Model" should {

    "Testing returnAll Function - Negative" in {
      val result = assignmentServices.result(assignmentServices.repository.returnAll())

      result mustEqual Nil
    }

    "Testing Storing" in {
      val result = assignmentServices.result(assignmentServices.repository.store(assignment1))

      assignmentServices.result(assignmentServices.repository.store(assignment2))

      result mustEqual true
    }

    "Testing returnAll Function - Positive" in {
      val result = assignmentServices.result(assignmentServices.repository.returnAll())

      result mustEqual List(assignment1,assignment2)
    }

    "Testing delete Function - Positive" in {
      val result = assignmentServices.result(assignmentServices.repository.delete(1))

      result mustEqual true
    }

    "Testing delete Function - Negative" in {
      val result = assignmentServices.result(assignmentServices.repository.delete(1))

      result mustEqual false
    }

  }

}
