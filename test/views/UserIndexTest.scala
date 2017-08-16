package views

import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.concurrent.Future

class UserIndexTest extends PlaySpec with MockitoSugar {

  "User Index should" should {
    "display" in {
      val mockFlash = mock[Flash]
      when(mockFlash.get("Error")).thenReturn(None)
      val html = views.html.UserIndex.render(mockFlash)
      assert(html.toString.contains("Welcome to Knoldus !"))
    }
  }
}