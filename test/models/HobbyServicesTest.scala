package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class HobbyServicesTest extends PlaySpec with MockitoSugar {

  val hobbyServices = new ModelsTest[HobbyServices]

  val hobby1 = Hobby(1,"Playing Cricket")
  val hobby2 = Hobby(2,"Playing Football")

  "Testing Hobby Database" should {

    "Testing return all - Negative" in {

      val result = hobbyServices.result(hobbyServices.repository.returnAll())
      result mustEqual Nil
    }

    "Testing storing Database - Positive" in {

      val result = hobbyServices.result(hobbyServices.repository.store(hobby1))
      result mustEqual true
    }

    "Testing return all - Positive" in {

      hobbyServices.result(hobbyServices.repository.store(hobby2))

      val hobby3: Hobby = Hobby(3,"Playing Badminton")

      hobbyServices.result(hobbyServices.repository.store(hobby3))

      val result = hobbyServices.result(hobbyServices.repository.returnAll())

      result mustEqual List(hobby1,hobby2,hobby3)
    }

  }

}