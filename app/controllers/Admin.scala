package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import controllers.admin._
import models._
import models.dao._
import views._

object Admin extends Controller with Secured with CategoryAdmin with ItemAdmin {
  val categoryDAO = DAOFactory.categoryDAO
  val itemDAO = DAOFactory.itemDAO

  val changePassForm = Form(
    tuple(
      "currPass" -> nonEmptyText,
      "newPass" -> nonEmptyText,
      "newPassRepeat" -> nonEmptyText
    ) verifying ("new password and the repeated new password are different", fields => fields match {
        case (currPass, newPass, newPassRepeat) => newPass == newPassRepeat
      }
    )
  )

  def adminHome(user: Option[User], changePassForm: Form[(String, String, String)] = changePassForm)(implicit request: Request[AnyContent]) =
    user.map { u =>
      Ok(html.index(body = html.admin.body(changePassForm), menu = html.admin.menu(), user = Some(u)))
    }.getOrElse(
      Forbidden
    )

  def index = IsAuthenticated { username => implicit request => adminHome(username) }

  def changePass = IsAuthenticated { username => implicit request =>
    changePassForm.bindFromRequest.fold(
      formWithErrors => {
        adminHome(username, formWithErrors.fill("", "", ""))
      },
      itemTuple => {
        userDAO.authenticate(username, itemTuple._1).map {user =>
          userDAO.changePassword(user.email, itemTuple._2)
          Redirect(routes.Admin.index).flashing("chgPassMsg" -> "password changed successfully")
        }.getOrElse {
          Redirect(routes.Admin.index).flashing("chgPassMsg" -> "current password is wrong")
        }
      }
    )
  }
}
