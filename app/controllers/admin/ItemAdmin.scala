package controllers.admin

import java.io.File
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import controllers._
import models._
import models.dao._
import models.images._
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
    html.index(body = html.admin.newItemForm(form, categoryDAO.all.map(c => c.urlName -> c.displayName)),
               menu = html.admin.menu())

  def newItemForm = IsAuthenticated { username => implicit request =>
    Ok(itemAddFormPage())
  }

  def newItem = IsAuthenticatedMultipart { username => implicit request =>
    addItemForm.bindFromRequest.fold(
      formWithErrors => BadRequest(itemAddFormPage(formWithErrors)),
      { case (name, descr, cat, imgs) =>
          val pictureKeys = request.body.files map {filePart =>
            val newFile = File.createTempFile("temp-uploaded-", filePart.filename)
            filePart.ref.moveTo(newFile, true)

            Images.processImage(newFile)
          }
          val imageKeys = if(pictureKeys.size == 0) None else Some(pictureKeys.mkString("|"))

          itemDAO.create(name, descr, imageKeys, categoryDAO.findByName(cat).get)
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
