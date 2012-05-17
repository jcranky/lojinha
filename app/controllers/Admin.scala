package controllers

import models._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

object Admin extends Controller {
  
  val addItemForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "pictures" -> list(text)
    )
  )
  
  def index = Action {
    Ok(views.html.index(body = views.html.admin.body(), menu = views.html.admin.menu()))
  }
  
  def itemAddFormPage(form: Form[(String, String, List[String])] = addItemForm) =
    views.html.index(body = views.html.admin.newItemForm(form), menu = views.html.admin.menu())
  
  def newItemForm = Action {
    Ok(itemAddFormPage())
  }
  
  def newItem = Action(parse.multipartFormData) { implicit request =>
    addItemForm.bindFromRequest.fold(
      formWithErrors => BadRequest(itemAddFormPage(formWithErrors)),
      itemTuple => {
        val pictureKeys = request.body.files map {filePart =>
          Images.processImage(filePart.ref.file)
        }
        
        Item.create(itemTuple._1, itemTuple._2, Option(pictureKeys.mkString("|")))
        Redirect(routes.Application.index)
      }
    )
  }
}
