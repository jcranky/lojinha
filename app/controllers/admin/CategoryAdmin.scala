package controllers.admin

import controllers._
import javax.inject.Inject
import models.dao._
import play.api.Configuration
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views._

class CategoryAdmin @Inject() (categoryDAO: CategoryDAO, val userDAO: UserDAO, val messagesApi: MessagesApi)
                              (implicit webJarAssets: WebJarAssets, configuration: Configuration) extends SecuredController with I18nSupport {

  val catForm: Form[(String, String)] = Form(
    tuple(
      "displayName" -> nonEmptyText,
      "urlName" -> nonEmptyText
    )
  )

  def categoryFormPage(form: Form[(String, String)] = catForm)(implicit request: Request[AnyContent]) =
    html.index(body = html.admin.newCategoryForm(form), menu = html.admin.menu())

  def newCategoryForm = IsAuthenticated { username => implicit request =>
    Ok(categoryFormPage())
  }

  def newCategory = IsAuthenticated { username => implicit request =>
    catForm.bindFromRequest.fold(
      formWithErrors => BadRequest(categoryFormPage(formWithErrors)),
      catTuple => {
        categoryDAO.create(catTuple._1, catTuple._2)
        Redirect(controllers.routes.Application.index())
      }
    )
  }
}
