package models

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.i18n.MessagesApi
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UserData(id: Int, firstname: String, middlename: Option[String], lastname: String,
                    username: String, password: String, mobilenumber: Long, gender: String, age: Int)

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

}

trait UserDataRepository extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userInfoQuery: TableQuery[UserDataTable] = TableQuery[UserDataTable]

  class UserDataTable(tag: Tag) extends Table[UserData](tag, "userdata") {

    def * : ProvenShape[UserData] = (id, firstname, middlename, lastname, username, password, mobilenumber, gender,
      age) <> (UserData.tupled, UserData.unapply)

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstname: Rep[String] = column[String]("firstname")

    def middlename: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastname: Rep[String] = column[String]("lastname")

    def username: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def mobilenumber: Rep[Long] = column[Long]("mobilenumber")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")
  }

}
