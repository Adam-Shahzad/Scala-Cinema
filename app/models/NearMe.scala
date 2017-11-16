package models
import scala.util.parsing._

class NearMe(typeOfAttraction:Int) {

  private def attractions: List[Map[String,Any]] = {
    typeOfAttraction match {
      case 0 => {
        val a = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=restaurant&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
          get.asInstanceOf[Map[String, Any]].get("results").get.asInstanceOf[List[Map[String, Any]]]
        println(a)
        a
      }
      case 1 => {
        scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=bar&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
          get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
      }
      case 2 => {
        scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=night_club&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
          get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
      }
      case 3 => {
        scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=museum&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
          get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
      }
      case 4 => {
        scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=shopping_mall&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
          get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
      }
    }
  }

  private val attractionList = attractions
  def name(value:Int) = attractionList(value).getOrElse("name", "No Value")
  //def opening_hours(value:Int):Map[String, Any] = attractionList(value).getOrElse("opening_hours", "No Value")
  def vicinity(value:Int) = attractionList(value).getOrElse("vicinity", "No Value")
  def rating(value:Int):Any = attractionList(value).getOrElse("rating", "No Value")
  def types(value:Int):Any = attractionList(value).getOrElse("types", "No Value") //returns List
  def geometry(value:Int):Any = attractionList(value).getOrElse("geometry", "No Value") //returns lat and long for gmaps
  def photos(value:Int):Any = {
    val photoRef = attractionList(value).getOrElse("photos", "No Value").toString()
    photoRef.substring(photoRef.indexOf("photo_reference")+19,photoRef.indexOf("width")-2)
  }
  def icon(value:Int):Any = attractionList(value).getOrElse("icon", "No Value")



}
