package controllers

import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.matching.Regex

case class User(firstName: String, middleName: Option[String], lastName: String, username: String, password: String,
                confirmPassword: String, phoneNumber: Long , gender: String, age: Int,hobbyID: List[Int])

case class LoginUser(username: String, password: String)

case class UserProfileData(firstName: String, middleName: Option[String], lastName: String, phoneNumber: Long , age: Int, gender: String,hobbies: List[Int])

case class UpdatePassword(userName : String,password : String, confirmPassword : String)


object UserProfileData{
  def apply(list: List[(String, Option[String], String, Long, Int , String,List[Int])]) = {
    val firstName = list.head._1
    val middleName = list.head._2
    val lastName = list.head._3
    val phoneNumber = list.head._4
    val age = list.head._5
    val gender = list.head._6
    val hobbies = list.head._7

    new UserProfileData(firstName, middleName, lastName,phoneNumber, age, gender,hobbies)
  }
}

class FormEg {

  val userConstraints: Form[User] = Form(mapping(
    "firstName" -> nonEmptyText.verifying(checkName),
    "middleName" -> optional(text).verifying(checkMiddleName),
    "lastName" -> nonEmptyText.verifying(checkName),
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(validPassword),
    "confirmPassword" -> nonEmptyText,
    "phoneNumber" -> longNumber.verifying(" Must be 10", ph => ph.toString.length == 10),
    "gender" -> text,
    "age" -> number.verifying(min(18), max(75)),
    "hobbyID"->list(number)
  )(User.apply)(User.unapply)
    verifying("Failed form constraints!", field => field.password.equals(field.confirmPassword)))


  val loginConstraints: Form[LoginUser] = Form(mapping(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(validPassword)
  )(LoginUser.apply)(LoginUser.unapply))


  val userProfileDataForm = Form(
    mapping(
      "firstName" -> text.verifying("Please enter first name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> text.verifying("Please enter last name", lastName => !lastName.isEmpty),
      "phoneNumber" -> longNumber.verifying(" Must be 10", ph => ph.toString.length == 10),
      "age" -> number(min = 18, max = 75),
      "gender" -> nonEmptyText,
      "hobbies"->list(number)
  )(UserProfileData.apply)(UserProfileData.unapply))


  val updatePasswordConstraints: Form[UpdatePassword] = Form(mapping(
    "userName" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(validPassword),
    "confirmPassword" -> nonEmptyText.verifying(validPassword)
  )(UpdatePassword.apply)(UpdatePassword.unapply)
    verifying("Failed form constraints!", field => field.password.equals(field.confirmPassword)))

  def validPassword: Constraint[String] = {
    Constraint(
      {
            case validPasswordRegex() => Valid
            case _ => Invalid(ValidationError("Password must contain at least 1 number  and 1 capital case letter"))
      }
    )
  }

  def checkName: Constraint[String] = {
    Constraint("checkName.constraint")(
        {
          case allLetters() => Valid
          case _ => Invalid(ValidationError("Name should only contain letters"))
        }
    )
  }

  def checkMiddleName: Constraint[Option[String]] = {
    Constraint("checkMiddleName.constraint")(
      middleName =>  middleName.fold("Empty")(identity) match
      {
        case "Empty" => Valid
        case allLetters() => Valid
        case _ => Invalid(ValidationError("Name should only contain letters"))
      }
    )
  }

  val allLetters: Regex = """[A-Za-z]*""".r
  val validPasswordRegex: Regex = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$""".r
}
