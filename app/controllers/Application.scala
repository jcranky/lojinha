package controllers

import javax.inject.Inject
import models.dao._
import play.api.Play.current
import play.api.cache.Cached
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import play.twirl.api.Html
import views._

class Application @Inject() (items: Items, mainMenu: MainMenu, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  val categoryDAO: CategoryDAO = DAOFactory.categoryDAO
  val userDAO: UserDAO = DAOFactory.userDAO
  val itemDAO: ItemDAO = DAOFactory.itemDAO

  val loginForm: Form[(String, String)] = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
        case (email, password) => userDAO.authenticate(email, password).isDefined
      })
  )

  def login = Action { implicit request =>
    Ok(html.index(body = html.login(loginForm), menu = mainMenu.menu))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index(body = html.login(formWithErrors), menu = mainMenu.menu)),
      user => Redirect(routes.Admin.index()).withSession("email" -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Application.login()).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def index = Cached((_: RequestHeader) => "index", 5) {
    Action { implicit request =>
      Ok(html.index(body = html.body(items.itemsHigherBids(itemDAO.all(false))), menu = mainMenu.menu))
    }
  }

  def about = Action { implicit request =>
    def findPage(l: Lang) = l match {
      case Lang(language, _) if language == "pt" => Some(html.index(body = html.about(), menu = mainMenu.menu))
      case Lang(language, _) if language == "en" => Some(html.index(body = html.about_en(), menu = mainMenu.menu))
      case _ => None
    }
    def findLang(langs: List[Lang]): Html = langs match {
      case Nil => html.index(body = html.about(), menu = mainMenu.menu)
      case head :: tail => findPage(head).getOrElse(findLang(tail))
    }

    Ok(findLang(request.acceptLanguages.toList))
  }

  def lang(code: String) = Action { implicit request =>
    Redirect(routes.Application.index()).withLang(Lang(code))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(routes.javascript.Application.lang)
    ).as("text/javascript")
  }
}
