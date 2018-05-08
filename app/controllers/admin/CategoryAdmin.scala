package controllers.admin

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import controllers._
import models.dao._
import views._

trait CategoryAdmin extends Controller with Secured {
  val categoryDAO: CategoryDAO

  val catForm = Form(
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
        Redirect(controllers.routes.Application.index)
      }
    )
  }
}