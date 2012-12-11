package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Lang
import play.api.mvc._

import models._
import models.dao._
import play.api.templates.Html
import views._

object Application extends Controller {
  def mainMenu(implicit request: Request[AnyContent]) = html.menu(categoryDAO.all())

  val categoryDAO = DAOFactory.categoryDAO
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
    Ok(html.index(body = html.login(loginForm), menu = mainMenu))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index(body = html.login(formWithErrors), menu = mainMenu)),
      user => Redirect(routes.Admin.index).withSession("email" -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def index = Action { implicit request =>
    Ok(html.index(body = html.body(itemDAO.all(false)), menu = mainMenu))
  }

  def about = Action { implicit request =>
    def findPage(l: Lang) = l match {
      case Lang(language, _) if language == "pt" => Some(html.index(body = html.about(), menu = mainMenu))
      case Lang(language, _) if language == "en" => Some(html.index(body = html.about_en(), menu = mainMenu))
      case _ => None
    }
    def findLang(langs: List[Lang]): Html = langs match {
      case Nil => html.index(body = html.about(), menu = mainMenu)
      case head :: tail => findPage(head).getOrElse(findLang(tail))
    }

    Ok(findLang(request.acceptLanguages.toList))
  }
}
