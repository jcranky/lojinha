package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.BidHelper
import models.dao.{Item, DAOFactory}

object Items extends Controller {
  val itemDAO = DAOFactory.itemDAO
  val bidDAO = DAOFactory.bidDAO
  
  val bidForm = Form(
    tuple(
      "email" -> email,
      "value" -> number
    )
  )
  
  def itemDetailsPage(item: Item, form: Form[(String, Int)] = bidForm) =
    views.html.index(body = views.html.itemDetails(item, bidDAO.highest(item.id), form))
  
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
  
  def details(itemId: Int) = Action {
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
  
  def list(category: String) = TODO
}
