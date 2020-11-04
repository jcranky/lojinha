package controllers

import models.dao.{ User, UserDAO }
import play.api.mvc._

trait SecuredController extends BaseController {
  val userDAO: UserDAO

  private def username(request: RequestHeader): Option[String] =
    request.session.get("email")

  private def onUnauthorized(request: RequestHeader): Result =
    Results.Redirect(routes.Application.login())

  def IsAuthenticated(f: => String => Request[AnyContent] => Result): EssentialAction =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }

  def IsAuthenticatedMultipart(
    f: => String => Request[play.api.mvc.MultipartFormData[play.api.libs.Files.TemporaryFile]] => Result
  ): EssentialAction =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(parse.multipartFormData)(request => f(user)(request))
    }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def emailToUser(email: String): Option[User] =
    userDAO.findByEmail(email)
}
