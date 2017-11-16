package models

object Movies{
  val newMovies = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://api.themoviedb.org/3/movie/upcoming?api_key=0e1152c2ffcd8427363a93caf8553082&language=en-UK&page=1").mkString).
    get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]

  val currentMovies = scala.util.parsing.json.JSON.parseFull(scala.io.Source.fromURL("https://api.themoviedb.org/3/movie/now_playing?api_key=0e1152c2ffcd8427363a93caf8553082&language=en-UK&page=1").mkString).
    get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]

  def id(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("id","No Value")
  def voteAvg(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("vote_average","No Value")
  def title(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("title","No Value")
  def popularity(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("popularity","No Value")
  def posterPath(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("poster_path","No Value")
  def originalLanguage(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("original_language","No Value")
  def genreIDs(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("genre-ids".mkString,"No Value")
  def backdropPath(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("backdrop_path","No Value")
  def adult(value:Int, movList:List[Map[String,String]]):String = movList(value).getOrElse("adult","No Value")
  def overview(value:Int,movList:List[Map[String,String]]):String = movList(value).getOrElse("overview", "No Value")
  def releaseData(value:Int,movList:List[Map[String,String]]):String = movList(value).getOrElse("release_date", "No Value")
  def length(movList:List[Map[String,String]]):Int = movList.length
}
