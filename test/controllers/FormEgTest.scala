package controllers

import org.scalatest.FunSuite

class FormEgTest extends FunSuite {

  val userData = UserProfileData.apply(List(("Aakash",None,"Jain",8447018441L,22,"male",List(1,2)),("Kapil",None,"Sharma",8447015841L,18,"male",List(1,2))))

  test("it should displayed first list from userDatas"){
    assert(userData===UserProfileData("Aakash",None,"Jain",8447018441L,22,"male",List(1,2)))
  }
}
