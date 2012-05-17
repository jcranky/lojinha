package controllers

import models.Item
import play.api._
import play.api.mvc._

//TODO: add i18n to the entire application

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index(body = views.html.body(Item.all())))
  }
}
