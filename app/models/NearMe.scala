package models
import scala.util.parsing._

object NearMe {
  val restaurant = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=restaurant&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
    get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
  val bar = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=bar&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
    get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
  val nightClub = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=night_club&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
    get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
  val museum = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=museum&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
    get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]
  val shoppingMall = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.474300,-2.286038&radius=8045&types=shopping_mall&key=AIzaSyCIcbTDyu2WmBu4kcGApLaXMIxEjhN6aKg").mkString).
    get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]

  def firstFiveResultsEach:List[List[Map[String,String]]] = List(restaurant.take(5),bar.take(5),nightClub.take(5), museum.take(5), shoppingMall.take(5))
  def names = List("Restaurant","Bar","Night Club","Museum","Shopping Mall")

  def name(value:Int, itemList:List[Map[String,String]]) = itemList(value).getOrElse("name", "No Value")
  def vicinity(value:Int,itemList:List[Map[String,String]]) = itemList(value).getOrElse("vicinity", "No Value")
  def rating(value:Int,itemList:List[Map[String,String]]):Any = itemList(value).getOrElse("rating", "No Value")
  def types(value:Int,itemList:List[Map[String,String]]):Any = itemList(value).getOrElse("types", "No Value") //returns List
  def geometry(value:Int,itemList:List[Map[String,String]]):Any = itemList(value).getOrElse("geometry", "No Value") //returns lat and long for gmaps
  def photos(value:Int,itemList:List[Map[String,String]]):Any = itemList(value).getOrElse("photos", "No Value")
  def icon(value:Int,itemList:List[Map[String,String]]):Any = itemList(value).getOrElse("icon", "No Value")
}
