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

class AddAssignmentTest extends PlaySpec with MockitoSugar {

  "Add Assignment should" should {
    "render Add Assignment form " in {
      val mockMessage = mock[Messages]
      val mockFlash = mock[Flash]
      val form = new FormEg
      when(mockFlash.get("error")) thenReturn None
      val html = views.html.AddAssignment.render(form.AssignmentConstraints,mockMessage,mockFlash)
      assert(html.toString.contains("Add"))
    }
  }

}