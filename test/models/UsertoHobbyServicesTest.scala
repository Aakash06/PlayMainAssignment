package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UsertoHobbyServicesTest extends PlaySpec with MockitoSugar {

  val usertoHobbyServices = new ModelsTest[UsertoHobbyServices]
  val userDataServices = new ModelsTest[UserDataServices]
  val hobbyServices = new ModelsTest[HobbyServices]

  val user = UserData(1,"Aakash",None,"Jain","Aakash06","Aakash06",8447018441L,"male",22,false,true)
  userDataServices.result(userDataServices.repository.store(user))

  "Testing UserToHobby Database" should {

    "Testing Storing Database - Positive" in{

      val result = usertoHobbyServices.result(usertoHobbyServices.repository.store(1,List(1,2)))

      result mustEqual true
    }

    /*"Testing Storing Database - Negative " in{

      val result = usertoHobbyServices.result(usertoHobbyServices.repository.store(1,Nil))

      result mustEqual false
    }*/

    "Testing getUserHobby -  Positive" in {
      val result = usertoHobbyServices.result(usertoHobbyServices.repository.getUserHobby(1))

      result mustEqual List(1,2)
    }

    "Testing getUserHobby -  Negative" in {
      val result = usertoHobbyServices.result(usertoHobbyServices.repository.getUserHobby(2))

      result mustEqual Nil
    }

    "Testing updateUserHobby - Positive" in {
      val result = usertoHobbyServices.result(usertoHobbyServices.repository.updateUserHobby(1,List(1,2,3)))

      result mustEqual true
    }

  }

}
