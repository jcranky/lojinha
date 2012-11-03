package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import models.BidHelper
import models.dao.{DAOFactory, Item, User}
import views._

object Items extends Controller with Secured {
  val categoryDAO = DAOFactory.categoryDAO
  val itemDAO = DAOFactory.itemDAO
  val bidDAO = DAOFactory.bidDAO

  val bidForm = Form(
    tuple(
      "email" -> email,
      "value" -> number
    )
  )

  def itemDetailsPage(item: Item, form: Form[(String, Int)] = bidForm)(implicit request: Request[AnyContent]) = {
    val user: Option[User] = request.session.get("email").map(emailToUser(_).get)

    html.index(body = html.itemDetails(item, bidDAO.highest(item.id), form),
               menu = Application.mainMenu, user = user)
  }

  def newBid(itemId: Int) = Action { implicit request =>
    itemDAO.findById(itemId) match {
      case Some(item) =>
        bidForm.bindFromRequest.fold(
          formWithErrors => BadRequest(itemDetailsPage(item, formWithErrors)),
          bidTuple => {
            BidHelper.processBid(bidTuple._1, bidTuple._2, itemId)
            Redirect(routes.Items.details(itemId))
          }
        )

      case None => NotFound("")
    }
  }

  def details(itemId: Int) = Action { implicit request =>
    itemDAO.findById(itemId) match {
      case Some(item) => Ok(itemDetailsPage(item))
      case None => NotFound("that product doesn't exist!")  //TODO: create a nice 404 page
    }
  }

  def highestBid(itemId: Int) = Action {
    bidDAO.highest(itemId) match {
      case Some(bid) => Ok(bid.value.toString)
      case None => NotFound
    }
  }

  def list(category: String) = Action { implicit request =>
    categoryDAO.findByName(category).map { c =>
      Ok(html.index(body = html.body(itemDAO.all(c)), menu = Application.mainMenu))
    } getOrElse Redirect("/")
  }
}
