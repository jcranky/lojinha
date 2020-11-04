package controllers

import javax.inject.Inject
import models.dao._
import play.api.Configuration
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.mvc._
import views._

class Admin @Inject() (
  val userDAO: UserDAO,
  val controllerComponents: ControllerComponents,
  indexTemplate: views.html.index
)(implicit configuration: Configuration)
    extends SecuredController
    with I18nSupport {

  val changePassForm: Form[(String, String, String)] = Form(
    tuple(
      "currPass"      -> nonEmptyText,
      "newPass"       -> nonEmptyText,
      "newPassRepeat" -> nonEmptyText
    ).verifying(
      "new password and the repeated new password are different",
      fields =>
        fields match {
          case (_, newPass, newPassRepeat) =>
            // fixme: check current password before proceeding!
            newPass == newPassRepeat
        }
    )
  )

  def adminHome(user: Option[User], changePassForm: Form[(String, String, String)] = changePassForm)(implicit
    request: Request[AnyContent]
  ): Result =
    user
      .map { u =>
        Ok(indexTemplate(body = html.admin.body(changePassForm), menu = html.admin.menu(), user = Some(u)))
      }
      .getOrElse(
        Forbidden
      )

  def index: EssentialAction = IsAuthenticated(username => implicit request => adminHome(username))

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def changePass: EssentialAction = IsAuthenticated { username => implicit request =>
    changePassForm
      .bindFromRequest()
      .fold(
        formWithErrors => adminHome(username, formWithErrors.fill(("", "", ""))),
        itemTuple =>
          userDAO
            .authenticate(username, itemTuple._1)
            .map { user =>
              userDAO.changePassword(user.email, itemTuple._2)
              Redirect(routes.Admin.index()).flashing("chgPassMsg" -> "password changed successfully")
            }
            .getOrElse {
              Redirect(routes.Admin.index()).flashing("chgPassMsg" -> "current password is wrong")
            }
      )
  }
}
