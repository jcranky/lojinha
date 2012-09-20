package controllers

import models.dao.{DAOFactory, User}
import play.api.mvc._

trait Secured {
  val userDAO = DAOFactory.userDAO

  private def username(request: RequestHeader) = request.session.get("email")

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

  def isAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  implicit def emailToUser(email: String): Option[User] = userDAO.findByEmail(email)
}
