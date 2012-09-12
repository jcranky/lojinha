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
  val userDAO = DAOFactory.userDAO
  val itemDAO = DAOFactory.itemDAO

  val addItemForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> nonEmptyText,
      "pictures" -> list(text)
    )
  )

  def index = isAuthenticated { username => _ =>
    userDAO.findByEmail(username).map { user =>
      Ok(html.index(body = html.admin.body(), menu = html.admin.menu(), user = Some(user)))
    }.getOrElse(Forbidden)
  }

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
}
