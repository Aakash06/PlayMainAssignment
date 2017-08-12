package controllers

import javax.inject._
import play.api.data._
import play.api.data.Form._

import play.api.mvc._

@Singleton
case class Customer(name: String, age: Int, address: String)

case class Item(itemName: String, price: Double)

class HomeController extends Controller {

  val customer = Customer("Aakash", 22, "Delhi")
  val order = List(Item("Chocolates", 10), Item("Chips", 20), Item("Polo", 10))

  def users = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.user("Home", customer, order))
  }

}
