import org.scalatest._
import selenium._

class BuyingATicket extends FlatSpec with Matchers with HtmlUnit {

  val host = "http://localhost:9000/"

  "Clicking on the listings Gallery button" should "navigate user to the listings gallery" in {
    go to (host)
    click on id("listingsGallery")
    pageTitle should be("Listing Gallery")
  }
  it should "then go to ticket selection page after clicking book movie button" in {
    click on id("BookMovie0")
    pageTitle should be("Ticket Selection")
  }
  it should "after fields are filled in and select button pressed take you to the payment page" in {
    textField("bookerName").value = "Marianne"
    textField("bookerEmail").value = "mariannepearson0@gmail.com"
    textField("adultTicket").value = "2"
    click on id("selectButton")
    pageTitle should be ("Payment")
  }
  it should "after filling in payment details and pressing make payment, go to successful payment page" in {
    textField("name").value = "Marianne"
    textField("number").value = "1234567891012345"
    textField("expiry").value = "08/21"
    textField("csv").value = "123"
    click on id("paymentButton")
    pageTitle should be ("Payment Confirmation")
    id("payConfMessage").webElement.getText should include ("mariannepearson0@gmail.com")
  }

  "Entering the incorrect details into the fields" should "throw an error and refresh the page" in {
    go to (host)
    click on id("listingsGallery")
    click on id("BookMovie0")
    textField("bookerName").value = "Marianne"
    textField("bookerEmail").value = "fakeEmail"
    textField("adultTicket").value = "2"
    click on id("selectButton")
    pageTitle should be ("Ticket Selection")
    id("bookerEmail_field").webElement.getText should include ("Valid email required")
  }
}
