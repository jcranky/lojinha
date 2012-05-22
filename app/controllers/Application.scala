package controllers

import models.DAOFactory
import models.Item
import play.api._
import play.api.mvc._

//TODO: add i18n to the entire application

object Application extends Controller {
  val itemDAO = DAOFactory.itemDAO
  
  def index = Action {
    Ok(views.html.index(body = views.html.body(itemDAO.all())))
  }
}
