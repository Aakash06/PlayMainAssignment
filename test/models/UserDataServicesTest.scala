package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Application

class UserDataServicesTest extends PlaySpec with MockitoSugar {

  val userDataServices = new ModelsTest[UserDataServices]

  val user = UserData(0,"Aakash",None,"Jain","Aakash06","Aakash06",8447018441L,"male",22,false,true)
  "Testing User database" should {

  "Storing Database" in {

   val result = userDataServices.result(userDataServices.repository.store(user))
    result mustEqual true
  }}
}