package views

import controllers.FormEg
import models.Hobby
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.concurrent.Future

class headerTest extends PlaySpec with MockitoSugar {

  "Header should" should {
    "display" in {
      val html = views.html.header.render()
      assert(html.toString.contains("Login"))
    }
  }

}