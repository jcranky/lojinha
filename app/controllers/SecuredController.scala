package controllers

import models.dao.{DAOFactory, User, UserDAO}
import play.api.mvc._

trait SecuredController extends Controller {
  val userDAO: UserDAO = DAOFactory.userDAO

  private def username(request: RequestHeader) = request.session.get("email")

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login())

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  def IsAuthenticatedMultipart(f: => String => Request[play.api.mvc.MultipartFormData[play.api.libs.Files.TemporaryFile]] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(parse.multipartFormData) { request => f(user)(request) }
  }

  implicit def emailToUser(email: String): Option[User] = userDAO.findByEmail(email)
}
