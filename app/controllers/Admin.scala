package controllers

import java.io.File
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import models._
import models.dao._
import views._

object Admin extends Controller with Secured {
  val categoryDAO = DAOFactory.categoryDAO
  val itemDAO = DAOFactory.itemDAO

  val addItemForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> nonEmptyText,
      "pictures" -> list(text)
    )
  )

  def adminHome(user: Option[User], changePassForm: Form[(String, String, String)] = changePassForm)(implicit request: Request[AnyContent]) =
    user.map { u =>
      Ok(html.index(body = html.admin.body(changePassForm), menu = html.admin.menu(), user = Some(u)))
    }.getOrElse(
      Forbidden
    )

  def index = isAuthenticated { username => implicit request => adminHome(username) }

  def itemAddFormPage(form: Form[(String, String, String, List[String])] = addItemForm) =
    html.index(body = html.admin.newItemForm(form), menu = html.admin.menu())

  def newItemForm = Action {
    Ok(itemAddFormPage())
  }

  def newItem = Action(parse.multipartFormData) { implicit request =>
    addItemForm.bindFromRequest.fold(
      formWithErrors => BadRequest(itemAddFormPage(formWithErrors)),
      itemTuple => {
        val pictureKeys = request.body.files map {filePart =>
          val newFile = File.createTempFile("temp-uploaded-", filePart.filename)
          filePart.ref.moveTo(newFile, true)

          Images.processImage(newFile)
        }

        itemDAO.create(itemTuple._1, itemTuple._2, Option(pictureKeys.mkString("|")), categoryDAO.getByName(itemTuple._3))
        Redirect(routes.Application.index)
      }
    )
  }

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

  def changePass = isAuthenticated { username => implicit request =>
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
