package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import models._
import models.dao._
import views._

object Application extends Controller {
  val userDAO = DAOFactory.userDAO
  val itemDAO = DAOFactory.itemDAO

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
        case (email, password) => userDAO.authenticate(email, password).isDefined
      })
  )

  def login = Action { implicit request =>
    Ok(html.index(body = html.login(loginForm)))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index(body = html.login(formWithErrors))),
      user => Redirect(routes.Admin.index).withSession("email" -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def index = Action {
    Ok(html.index(body = html.body(itemDAO.all())))
  }

  def about = Action {
    Ok(html.index(body = html.about()))
  }
}
