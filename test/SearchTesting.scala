import models.Movies
import org.scalatest._
import selenium._

class SearchTesting extends FlatSpec with Matchers with HtmlUnit {

  val host = "http://localhost:9000/"

  "Search Form Character" should "should search and display results based on the search inputted" in {
    go to (host)
    pageTitle should be ("Homepage")
    click on cssSelector("#value")
    enter("a")
    submit()
    for (value <- 0 until Movies.filterList("a").length -1){
      id("title" + value).webElement.getText.toLowerCase should contain ('a')
    }
  }

  "Searching Non-Found" should "Give a page with the text 'No Results Found' when entering a string that isn't a result" in {
    go to (host)
    click on cssSelector("#value")
    enter("zzzzzzzzzzzzzzzzzzzzzzzzzzzzz")
    submit()
    id("noResult").webElement.getText should equal ("No Results Found")
  }

  "Search Nothing" should "Show the homepage when you enter nothing into the search box and submit" in {
    go to (host)
    click on cssSelector("#value")
    enter("")
    submit()
    pageTitle should be ("Homepage")
  }
}
