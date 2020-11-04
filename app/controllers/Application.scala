package controllers

import javax.inject.Inject
import models.dao._
import models.images.Images
import play.api.Configuration
import play.api.cache.Cached
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{ I18nSupport, Lang }
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import play.twirl.api.Html
import views._

class Application @Inject() (
  items: Items,
  mainMenu: MainMenu,
  userDAO: UserDAO,
  itemDAO: ItemDAO,
  cached: Cached,
  val controllerComponents: ControllerComponents,
  indexTemplate: views.html.index
)(implicit configuration: Configuration, images: Images)
    extends BaseController
    with I18nSupport {

  val loginForm: Form[(String, String)] = Form(
    tuple(
      "email"    -> text,
      "password" -> text
    ).verifying(
      "Invalid email or password",
      result =>
        result match {
          case (email, password) => userDAO.authenticate(email, password).isDefined
        }
    )
  )

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(indexTemplate(body = html.login(loginForm), menu = mainMenu.menu))
  }

  def authenticate: Action[AnyContent] = Action { implicit request =>
    loginForm
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(indexTemplate(body = html.login(formWithErrors), menu = mainMenu.menu)),
        user => Redirect(routes.Admin.index()).withSession("email" -> user._1)
      )
  }

  def logout: Action[AnyContent] = Action {
    Redirect(routes.Application.login()).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def index: EssentialAction = cached((_: RequestHeader) => "index", 5) {
    Action { implicit request =>
      Ok(indexTemplate(body = html.body(items.itemsHigherBids(itemDAO.all(false))), menu = mainMenu.menu))
    }
  }

  def about: Action[AnyContent] = Action { implicit request =>
    def findPage(l: Lang)                 = l match {
      case Lang(locale) if locale.getLanguage == "pt" => Some(indexTemplate(body = html.about(), menu = mainMenu.menu))
      case Lang(locale) if locale.getLanguage == "en" =>
        Some(indexTemplate(body = html.about_en(), menu = mainMenu.menu))
      case _                                          => None
    }
    def findLang(langs: List[Lang]): Html = langs match {
      case Nil          => indexTemplate(body = html.about(), menu = mainMenu.menu)
      case head :: tail => findPage(head).getOrElse(findLang(tail))
    }

    Ok(findLang(request.acceptLanguages.toList))
  }

  def lang(code: String): Action[AnyContent] = Action { implicit request =>
    Redirect(routes.Application.index()).withLang(Lang(code))
  }

  def javascriptRoutes: Action[AnyContent] = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(routes.javascript.Application.lang)
    ).as("text/javascript")
  }
}
