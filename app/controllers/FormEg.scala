package controllers

import com.google.inject.Inject
import models.{UserData, UserDataServices}
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}


case class User(firstName: String, middleName: Option[String], lastName: String, username: String, password: String,
                confirmPassword: String, phoneNumber: Long , gender: String, age: Int)

case class LoginUser(username: String, password: String)

class FormEg {


  val userConstraints: Form[User] = Form(mapping(
    "firstName" -> nonEmptyText,
    "middleName" -> optional(text),
    "lastName" -> nonEmptyText,
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(validPassword),
    "confirmPassword" -> nonEmptyText,
    "phoneNumber" -> longNumber.verifying(" Must be 10", ph => ph.toString.length == 10),
    "gender" -> text,
    "age" -> number.verifying(min(18), max(75))
  )(User.apply)(User.unapply)
    verifying("Failed form constraints!", field => field.password.equals(field.confirmPassword)))

  val loginConstraints: Form[LoginUser] = Form(mapping(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(validPassword)
  )(LoginUser.apply)(LoginUser.unapply))

  def validPassword: Constraint[String] = {
    val validPassword = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$""".r
    Constraint({
      password =>
        val errors =
          password match {
            case validPassword() => Nil
            case _ => Seq(ValidationError("Password must contain at least 1 number  and 1 capital case letter"))
          }
        if (errors.isEmpty) Valid else Invalid(errors)
    })
  }

}
