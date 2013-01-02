package controllers.admin

import java.io.File
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import controllers._
import models._
import models.dao._
import views._

trait ItemAdmin extends Controller with Secured {
  val categoryDAO: CategoryDAO
  val itemDAO: ItemDAO

  val addItemForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> nonEmptyText,
      "pictures" -> list(text)
    )
  )

  def itemAddFormPage(form: Form[(String, String, String, List[String])] = addItemForm) =
    html.index(body = html.admin.newItemForm(form), menu = html.admin.menu())

  def newItemForm = IsAuthenticated { username => implicit request =>
    Ok(itemAddFormPage())
  }

  def newItem = IsAuthenticatedMultipart { username => implicit request =>
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

  def itemSold(id: Int) = IsAuthenticated { username => implicit request =>
    itemDAO.sell(id).map(item => Ok(Items.itemDetailsPage(item))).getOrElse(NotFound)
  }

  def deleteItem(id: Int) = IsAuthenticated { username => implicit request =>
    itemDAO.delete(id)
    Admin.adminHome(username)
  }
}
