package controllers.admin

import java.io.File

import controllers._
import javax.inject.Inject
import models.dao._
import models.images._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views._

class ItemAdmin @Inject() (items: Items, admin: Admin, val messagesApi: MessagesApi) extends Controller with SecuredController with I18nSupport {
  val categoryDAO: CategoryDAO = DAOFactory.categoryDAO
  val itemDAO: ItemDAO = DAOFactory.itemDAO

  val addItemForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "minValue" -> bigDecimal(8, 2),
      "category" -> nonEmptyText,
      "pictures" -> list(text)
    )
  )

  def itemAddFormPage(form: Form[(String, String, BigDecimal, String, List[String])] = addItemForm)(implicit request: Request[_]) =
    html.index(body = html.admin.newItemForm(form, categoryDAO.all.map(c => c.urlName -> c.displayName)),
      menu = html.admin.menu())

  def newItemForm = IsAuthenticated { username => implicit request =>
    Ok(itemAddFormPage())
  }

  def newItem = IsAuthenticatedMultipart { username => implicit request =>
    addItemForm.bindFromRequest().fold(
      formWithErrors  => BadRequest(itemAddFormPage(formWithErrors)),
      { case (name, descr, minValue, cat, imgs) =>
        val pictureKeys = request.body.files map {filePart =>
          val newFile = File.createTempFile("temp-uploaded-", filePart.filename)
          filePart.ref.moveTo(newFile, true)

          Images.processImage(newFile)
        }
        val imageKeys = if(pictureKeys.isEmpty) None else Some(pictureKeys.mkString("|"))

        itemDAO.create(name, descr, minValue, imageKeys, categoryDAO.findByName(cat).get)
        Redirect(controllers.routes.Application.index())
      }
    )
  }

  def itemSold(id: Int) = IsAuthenticated { username => implicit request =>
    itemDAO.sell(id).map(item => Ok(items.itemDetailsPage(item))).getOrElse(NotFound)
  }

  def deleteItem(id: Int) = IsAuthenticated { username => implicit request =>
    itemDAO.delete(id)
    admin.adminHome(username)
  }
}