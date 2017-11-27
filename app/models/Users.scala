package models

case class Users (
                   _id:Int,
                   firstName:String,
                   lastName:String,
                   userName:String,
                   email:String,
                   hashedPass:String
                  )

