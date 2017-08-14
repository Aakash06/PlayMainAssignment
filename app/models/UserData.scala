package models

import javax.inject.Inject

import controllers.UserProfileData
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.i18n.MessagesApi
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UserData(id: Int, firstname: String, middlename: Option[String], lastname: String,
                    username: String, password: String, mobilenumber: Long, gender: String, age: Int,isAdmin : Boolean, isEnable : Boolean)

class UserDataServices @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,val messagesApi: MessagesApi)
  extends UserDataRepository {

  implicit val messages: MessagesApi = messagesApi

  import driver.api._

  def store(userData: UserData): Future[Boolean] = {
    db.run(userInfoQuery += userData).map(_ > 0)
  }

  def findByUsername(userName: String): Future[Option[String]] = {
    db.run(userInfoQuery.filter(_.username === userName).map(_.username).result.headOption)
  }

  def findByUsernameGetId(userName: String): Future[Option[Int]] = {
    db.run(userInfoQuery.filter(_.username === userName).map(_.id).result.headOption)
  }

  def checkAdmin(username:String): Future[Boolean] ={
    db.run(userInfoQuery.filter(_.username === username).map(_.isAdmin).result.head)
  }

  def checkEnable(username:String): Future[Boolean] = {
    db.run(userInfoQuery.filter(_.username === username).map(_.isEnable).result.head)
  }

  def checkloginValue(username:String, password : String): Future[Boolean] ={

    val users: Future[List[UserData]] = db.run(userInfoQuery.filter(_.username === username).to[List].result)
    users.map { user =>
      if (user.isEmpty) {
        false
      }
      else if (BCrypt.checkpw(password, user.head.password)) {
        true
      }
      else {
        false
      } }
  }

  def retrieve(username: String): Future[List[UserData]] = {
    db.run(userInfoQuery.filter(_.username === username).to[List].result)

  }

  def retrieveAll(): Future[List[UserData]] ={
    db.run(userInfoQuery.to[List].result)
  }

  def updateInfo(userProfileData: UserProfileData,username:String): Future[Boolean] ={
    db.run(userInfoQuery.filter(_.username===username).map(user=> (user.firstname,user.middlename,user.lastname,
      user.mobilenumber,user.age,user.gender)).update(userProfileData.firstName,userProfileData.middleName,
      userProfileData.lastName,userProfileData.phoneNumber,userProfileData.age,userProfileData.gender)).map(_ > 0)
  }

  def makeDisable(username:String): Future[Boolean] ={
    db.run(userInfoQuery.filter(_.username=== username).map(_.isEnable).update(false)).map(_ > 0)
  }

  def makeEnable(username:String): Future[Boolean] ={
    db.run(userInfoQuery.filter(_.username=== username).map(_.isEnable).update(true)).map(_ > 0)
  }

  def updateUserPassword(password: String, username: String): Future[Boolean] ={
    db.run(userInfoQuery.filter(_.username===username).map(_.password).update(password)).map(_ > 0)
  }
}

trait UserDataRepository extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userInfoQuery: TableQuery[UserDataTable] = TableQuery[UserDataTable]

  class UserDataTable(tag: Tag) extends Table[UserData](tag, "userdata") {

    def * : ProvenShape[UserData] = (id, firstname, middlename, lastname, username, password, mobilenumber, gender,
      age,isAdmin,isEnable) <> (UserData.tupled, UserData.unapply)

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstname: Rep[String] = column[String]("firstname")

    def middlename: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastname: Rep[String] = column[String]("lastname")

    def username: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def mobilenumber: Rep[Long] = column[Long]("mobilenumber")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def isEnable: Rep[Boolean] = column[Boolean]("isenable")
  }

}
