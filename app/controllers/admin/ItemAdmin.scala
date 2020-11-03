package controllers.admin

import java.io.File

import controllers._
import javax.inject.Inject
import models.dao._
import models.images._
import play.api.Configuration
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.HtmlFormat
import views._

class ItemAdmin @Inject() (items: Items, admin: Admin, images: Images, itemDAO: ItemDAO, categoryDAO: CategoryDAO,
                           val userDAO: UserDAO, val controllerComponents: ControllerComponents,
                           indexTemplate: views.html.index)
                          (implicit configuration: Configuration) extends SecuredController with I18nSupport {

  val addItemForm: Form[(String, String, BigDecimal, String, List[String])] = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "minValue" -> bigDecimal(8, 2),
      "category" -> nonEmptyText,
      "pictures" -> list(text)
    )
  )

  def itemAddFormPage(form: Form[(String, String, BigDecimal, String, List[String])] = addItemForm)(implicit request: Request[_]): HtmlFormat.Appendable =
    indexTemplate(body = html.admin.newItemForm(form, categoryDAO.all().map(c => c.urlName -> c.displayName)),
      menu = html.admin.menu())

  def newItemForm: EssentialAction = IsAuthenticated { username =>implicit request =>
    Ok(itemAddFormPage())
  }

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial", "org.wartremover.warts.NonUnitStatements"))
  def newItem: EssentialAction = IsAuthenticatedMultipart { username =>implicit request =>
    addItemForm.bindFromRequest().fold(
      formWithErrors  => BadRequest(itemAddFormPage(formWithErrors)),
      { case (name, descr, minValue, cat, imgs) =>
        val pictureKeys = request.body.files map {filePart =>
          val newFile = File.createTempFile("temp-uploaded-", filePart.filename)
          filePart.ref.moveTo(newFile, replace = true)

          images.processImage(newFile)
        }
        val imageKeys = if(pictureKeys.isEmpty) None else Some(pictureKeys.mkString("|"))

        itemDAO.create(name, descr, minValue, imageKeys, categoryDAO.findByName(cat).get)
        Redirect(controllers.routes.Application.index())
      }
    )
  }

  def itemSold(id: Int): EssentialAction = IsAuthenticated { username =>implicit request =>
    itemDAO.sell(id).map(item => Ok(items.itemDetailsPage(item))).getOrElse(NotFound)
  }

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def deleteItem(id: Int): EssentialAction = IsAuthenticated { username =>implicit request =>
    itemDAO.delete(id)
    admin.adminHome(username)
  }
}