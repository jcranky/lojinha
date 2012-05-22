package controllers

import models.DAOFactory
import play.api._
import play.api.mvc._

object Application extends Controller {
  val itemDAO = DAOFactory.itemDAO
  
  def index = Action {
    Ok(views.html.index(body = views.html.body(itemDAO.all())))
  }
}
