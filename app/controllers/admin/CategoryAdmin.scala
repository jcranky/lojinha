package controllers.admin

import controllers._
import javax.inject.Inject
import models.dao._
import play.api.Configuration
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.HtmlFormat
import views._

class CategoryAdmin @Inject() (
  categoryDAO: CategoryDAO,
  val userDAO: UserDAO,
  val controllerComponents: ControllerComponents,
  indexTemplate: views.html.index
)(implicit configuration: Configuration)
    extends SecuredController
    with I18nSupport {

  val catForm: Form[(String, String)] = Form(
    tuple(
      "displayName" -> nonEmptyText,
      "urlName"     -> nonEmptyText
    )
  )

  def categoryFormPage(form: Form[(String, String)] = catForm)(implicit
    request: Request[AnyContent]
  ): HtmlFormat.Appendable =
    indexTemplate(body = html.admin.newCategoryForm(form), menu = html.admin.menu())

  def newCategoryForm: EssentialAction = IsAuthenticated { username => implicit request =>
    Ok(categoryFormPage())
  }

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def newCategory: EssentialAction = IsAuthenticated { username => implicit request =>
    catForm
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(categoryFormPage(formWithErrors)),
        catTuple => {
          categoryDAO.create(catTuple._1, catTuple._2)
          Redirect(controllers.routes.Application.index())
        }
      )
  }
}
