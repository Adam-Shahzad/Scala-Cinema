package models

class Movies {

  private val urlData = scala.io.Source.fromURL("https://api.themoviedb.org/3/movie/upcoming?api_key=0e1152c2ffcd8427363a93caf8553082&language=en-UK&page=1").mkString
  private val moviesJson = scala.util.parsing.json.JSON.parseFull(urlData)
  private val movieList = moviesJson.get.asInstanceOf[Map[String, String]].get("results").get.asInstanceOf[List[Map[String, String]]]

  def id(value:Int):String = movieList(value).getOrElse("id","No Value")
  def voteAvg(value:Int):String = movieList(value).getOrElse("vote_average","No Value")
  def title(value:Int):String = movieList(value).getOrElse("title","No Value")
  def popularity(value:Int):String = movieList(value).getOrElse("poster_path","No Value")
  def originalLanguage(value:Int):String = movieList(value).getOrElse("original_language","No Value")
  def genreIDs(value:Int):String = movieList(value).getOrElse("genre-ids","No Value")
  def backdropPath(value:Int):String = movieList(value).getOrElse("backdrop_path","No Value")
  def adult(value:Int):String = movieList(value).getOrElse("adult","No Value")
  def overview(value:Int):String = movieList(value).getOrElse("overview", "No Value")
  def releaseData(value:Int):String = movieList(value).getOrElse("release_date", "No Value")
}
