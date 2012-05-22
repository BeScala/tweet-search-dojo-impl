package controllers

import play.api._
import libs.json.JsValue
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models.SearchTerm

object Application extends Controller {
  val twitterUrl = "http://search.twitter.com/search.json?q="

  def index = Action {
    Ok(views.html.index("Your new application is ready.", searchTermForm))
  }

  val searchTermForm = Form(
    mapping(
      "term" -> nonEmptyText
    )(SearchTerm.apply)(SearchTerm.unapply)
  )

   def addSearch = Action { req =>
     searchTermForm.bindFromRequest()(req).fold(
        errorCase => BadRequest(views.html.index("Your new application is ready.", errorCase)) ,
        searchTerm => fetchSearch(searchTerm.term)
     )
   }

  def fetchSearch(term:String) = {
    import play.api.libs.ws.WS
            Async{
      WS.url(twitterUrl+term).get().map{
        response => Ok(getText(response.json))
      }
            }

  }

  def getText(jsonResult:JsValue)  ={
    //jsonResult.\\("results") map ((s:JsValue) => (s \ "text")) toString()
    jsonResult.\("results").\\ ("text") toString()
  }

  
}