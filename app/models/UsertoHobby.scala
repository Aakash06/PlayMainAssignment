package models

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.i18n.MessagesApi
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UsertoHobby(id: Int, userid: Int, hobbyid: Int)

class UsertoHobbyServices @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,val messagesApi: MessagesApi)
  extends usertoHobbyRepository {

  implicit val messages: MessagesApi = messagesApi

  import driver.api._

  def store(userID: Int, hobbyID: List[Int]): Future[Boolean] = {
    val listValidHobbyID = hobbyID.filter(_ != Nil)
    val listOfResult: List[Future[Boolean]] = listValidHobbyID.map (
      hobbyID => db.run(usertohobbyInfoQuery += UsertoHobby(0,userID, hobbyID)).map(_ > 0)
    )
    Future.sequence(listOfResult).map {
      result =>
        if (result.contains(false)) false else true
    }
  }

  def getUserHobby(userid:Int): Future[List[Int]] = {
    db.run(usertohobbyInfoQuery.filter(_.userid===userid).map(_.hobbyid).to[List].sorted.result)
  }

  def updateUserHobby(userID:Int, hobbyID:List[Int]): Future[Boolean] ={
    db.run(usertohobbyInfoQuery.filter(_.userid===userID).delete).map(_ > 0)
    store(userID,hobbyID)
  }
}

trait usertoHobbyRepository extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val usertohobbyInfoQuery: TableQuery[UsertoHobbyTable] = TableQuery[UsertoHobbyTable]

  class UsertoHobbyTable(tag: Tag) extends Table[UsertoHobby](tag, "usertohobby") {

    def * : ProvenShape[UsertoHobby] = (id,userid,hobbyid) <> (UsertoHobby.tupled, UsertoHobby.unapply)

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def userid: Rep[Int] = column[Int]("userid")

    def hobbyid: Rep[Int] = column[Int]("hobbyid")
  }

}
