package models
import scala.util.parsing._

class NearMe {

  private def attractions: List[Map[String,String]] = {
    scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=food&name=cruise&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
      get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
  }

  private val attractionList = attractions
  def name(value:Int) = attractionList(value).getOrElse("name", "No Value")
  def opening_hours(value:Int) = attractionList(value).getOrElse("opening_hours", "No Value")
  def vicinity(value:Int) = attractionList(value).getOrElse("vicinity", "No Value")
  def rating(value:Int) = attractionList(value).getOrElse("rating", "No Value")
  def types(value:Int) = attractionList(value).getOrElse("types", "No Value") //returns List
  def geometry(value:Int) = attractionList(value).getOrElse("geometry", "No Value") //returns lat and long for gmaps
  def photos(value:Int) = attractionList(value).getOrElse("photos", "No Value")
  def icon(value:Int) = attractionList(value).getOrElse("icon", "No Value")

}
