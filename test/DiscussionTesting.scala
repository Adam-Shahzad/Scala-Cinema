import org.scalatest._
import org.scalatest.time.{Seconds, Span}
import selenium._


class DiscussionTesting  extends FlatSpec with Matchers with HtmlUnit {

  val host = "http://localhost:9000/"

  "Clicking on the discussion board link" should "take you to the discussion board page" in {
    go to (host)
    click on id("discussionBoard")
    pageTitle should be ("Discussion Board")
  }
  it should "after filling in form fields and pressing post, be posted at the bottom of the page" in {
    textField("name").value = "Marianne"
    textField("email").value = "mariannepearson0@gmail.com"
    textField("filmName").value = "Avengers"
    textField("rating").value = "2"
    textArea("desc").value = "It was a good film, i enjoyed it but i didn't enjoy one of the actors because he is a bad actor"
    click on id("postDiscussion")
    pageTitle should be ("Discussion Board")
    implicitlyWait(Span(5, Seconds))
    id("discussionDiv").webElement.getText should include ("It was a good film,")
  }
}
