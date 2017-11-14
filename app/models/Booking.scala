package models

case class Booking (
                     _id: Int,
                     bookerName:String,
                     movieTitle:String,
                     screenNo:Int,
                     movieTime: String,
                     noOfAdult: String,
                     noOfChild:String,
                     noOfConcession:String
                   )

