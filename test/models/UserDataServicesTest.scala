package models

import controllers.UserProfileData
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserDataServicesTest extends PlaySpec with MockitoSugar {
  val userDataServices = new ModelsTest[UserDataServices]
  val user = UserData(1,"Aakash",None,"Jain","Aakash06","Aakash06",8447018441L,"male",22,false,true)
  val user2 = UserData(2,"Kapil",None,"Sharma","Kapil14","Aakash06",84470188741L,"male",22,true,true)
  val encryptPassword: String = BCrypt.hashpw("Divya25", BCrypt.gensalt)
  val user3 = UserData(3,"Divya",None,"Dua","Divya25",encryptPassword,84470188741L,"female",22,false,true)

val user1Update: UserProfileData =  UserProfileData("Aakash",None,"Jain",8447018441L,25,"female",List(2,3,4))

  "Testing User database" should {

  "Storing Database" in {

   val result = userDataServices.result(userDataServices.repository.store(user2))
    result mustEqual true
  }


    "Testing findUsername Method- Positive Test Case "  in
    {
    val result = userDataServices.result(userDataServices.repository.findByUsername("Aakash06"))

      val value = result.get

      value mustEqual "Aakash06"
    }

    "Testing findUsername Method- Negative Test Case "  in
      {
        val result = userDataServices.result(userDataServices.repository.findByUsername("Aakash"))


        result mustEqual None

      }

    "Testing findByUsernameGetId - Positive Test Case " in
    {
      val result = userDataServices.result(userDataServices.repository.findByUsernameGetId("Aakash06"))

      val value = result.get

      value mustEqual 1
    }

    "Testing findByUsernameGetId - Negative Test Case " in
      {
        val result = userDataServices.result(userDataServices.repository.findByUsernameGetId("Aakash"))


        result mustEqual None
      }

    "Testing checkAdmin - Positive Test Case " in
      {
        val result = userDataServices.result(userDataServices.repository.checkAdmin("Kapil14"))

        result mustEqual true
      }

    "Testing checkAdmin - Negative Test Case " in
      {
        val result = userDataServices.result(userDataServices.repository.checkAdmin("Aakash06"))


        result mustEqual false
      }

    "Testing checkEnable" in
      {
        val result = userDataServices.result(userDataServices.repository.checkEnable("Aakash06"))


        result mustEqual true
      }

    "Testing Login Value - Positive" in
      {
        userDataServices.result(userDataServices.repository.store(user3))

        val result = userDataServices.result(userDataServices.repository.checkloginValue("Divya25","Divya25"))

        result mustEqual true
      }

    "Testing Login Value - Negative" in
      {
        val result = userDataServices.result(userDataServices.repository.checkloginValue("Divya25","Divya22"))


        result mustEqual false
      }

    "Testing Retrieve - Positive" in
      {
        val result = userDataServices.result(userDataServices.repository.retrieve("Divya25"))


        result mustEqual List(user3)
      }

    "Testing Retrieve - Negative" in
      {
        val result = userDataServices.result(userDataServices.repository.retrieve("Divya"))


        result mustEqual Nil
      }

    "Testing RetrieveAll - Positive" in
      {
        val result = userDataServices.result(userDataServices.repository.retrieveAll())


        result mustEqual List(user,user2,user3)
      }

    "Testing Update - Positive" in
      {
        val result = userDataServices.result(userDataServices.repository.updateInfo(user1Update,"Aakash06"))


        result mustEqual true
      }

    "Testing Update - Negative" in
      {
        val result = userDataServices.result(userDataServices.repository.updateInfo(user1Update,"Aakash"))


        result mustEqual false
      }

    "Testing Disable - Positive" in
      {
        val result = userDataServices.result(userDataServices.repository.makeDisable("Aakash06"))


        result mustEqual true
      }

    "Testing Disable - Negative" in
      {
        val result = userDataServices.result(userDataServices.repository.makeDisable("Aakash"))


        result mustEqual false
      }

    "Testing Enable - Positive" in
      {
        val result = userDataServices.result(userDataServices.repository.makeEnable("Aakash06"))


        result mustEqual true
      }

    "Testing Enable - Negative" in
      {
        val result = userDataServices.result(userDataServices.repository.makeEnable("Aakash"))


        result mustEqual false
      }

    "Testing UserPassword - Positive" in
      {
        val result = userDataServices.result(userDataServices.repository.updateUserPassword(encryptPassword,"Aakash06"))


        result mustEqual true
      }

    "Testing UserPassword- Negative" in
      {
        val result = userDataServices.result(userDataServices.repository.updateUserPassword(encryptPassword,"Aakash"))


        result mustEqual false
      }

  }
}