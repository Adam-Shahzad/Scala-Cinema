package models
import scala.util.parsing._

class nearMe {
  println(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670,151.1957&radius=500&types=food&name=cruise&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg"))
}
