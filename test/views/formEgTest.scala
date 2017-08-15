package views


import controllers.FormEg
import models.{Hobby, HobbyServices}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.concurrent.Future

class formEgTest extends PlaySpec with MockitoSugar {

  "signup should" should {
    "render signup form " in {
      val mockMessage = mock[Messages]
      val mockFlash = mock[Flash]
      val signUpForm = new FormEg
      when(mockFlash.get("error")) thenReturn None
      val html = views.html.formEg.render(signUpForm.userConstraints,List(Hobby(1,"Playing")),mockMessage,mockFlash)
      assert(html.toString.contains("Submit"))
    }
  }

}