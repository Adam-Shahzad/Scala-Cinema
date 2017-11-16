package controllers

import models.NearMe
import play.api.mvc.{Action, Controller}

class NearMeController extends Controller{
    val restaurant = new NearMe(0)
  def nearByList(): List[NearMe] ={
    val restaurant = new NearMe(0)
    val bar = new NearMe(1)
    val nightClub = new NearMe(2)
    val museum = new NearMe(3)
    val shoppingMall = new NearMe(4)
    List(restaurant,bar,nightClub, museum, shoppingMall)
  }

  def aroundUs = Action{
    Ok(views.html.aroundUs(restaurant))
  }

}
