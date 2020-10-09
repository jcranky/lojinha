package controllers

import javax.inject.Inject
import models.BidHelper
import models.dao._
import models.images.Images
import play.api.Configuration
import play.api.cache.Cached
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views._

class Items @Inject() (mainMenu: MainMenu, itemDAO: ItemDAO, bidDAO: BidDAO, categoryDAO: CategoryDAO, val userDAO: UserDAO,
                       bidHelper: BidHelper, cached: Cached, val controllerComponents: ControllerComponents,
                       indexTemplate: views.html.index)
                      (implicit configuration: Configuration, images: Images) extends SecuredController with I18nSupport {

  def bidForm(minValue: Int = 1): Form[(String, Int, Boolean)] = Form(
    tuple(
      "email" -> email,
      "value" -> number(min = minValue),
      "notifyBetterBids" -> boolean
    )
  )

  def itemDetailsPage(item: Item, form: Form[(String, Int, Boolean)] = bidForm())(implicit request: Request[AnyContent]) = {
    val user: Option[User] = request.session.get("email").map(emailToUser(_).get)

    indexTemplate(body = html.itemDetails(item, bidDAO.highest(item.id), form), menu = mainMenu.menu, user = user)
  }

  def newBid(itemId: Int) = Action { implicit request =>
    itemDAO.findById(itemId) match {
      case Some(item) =>
        val maxBid = bidDAO.highest(itemId).map(_.value.toInt + 1).getOrElse(1)
        val minValue = math.max(maxBid, item.minValue.toInt)
        
        bidForm(minValue).bindFromRequest.fold(
          formWithErrors => BadRequest(itemDetailsPage(item, formWithErrors)),
          { case (email, value, notify) =>
              bidHelper.processBid(email, value, notify, itemId, routes.Items.details(itemId).absoluteURL())
              Redirect(routes.Items.details(itemId))
          }
        )

      case None => NotFound("")
    }
  }

  def details(itemId: Int) = cached((_: RequestHeader) => s"item-${itemId}", 5) {
    Action { implicit request =>
      itemDAO.findById(itemId) match {
        case Some(item) => Ok(itemDetailsPage(item))
        case None => NotFound("that product doesn't exist!")  //TODO: create a nice 404 page
      }
    }
  }

  def highestBid(itemId: Int) = Action {
    bidDAO.highest(itemId) match {
      case Some(bid) => Ok(bid.value.toString)
      case None => NotFound
    }
  }

  def list = l(sold = false)
  def listSold = l(sold = true)

  def listCat(cat: String) = l(Some(cat), false)
  def listCatSold(cat: String) = l(Some(cat), true)

  def l(category: Option[String] = None, sold: Boolean) = Action { implicit request =>
    category.map{ cat => categoryDAO.findByName(cat).map { c =>
        Ok(indexTemplate(body = html.body(itemsHigherBids(itemDAO.all(c, sold))), menu = mainMenu.menu))
      } getOrElse Redirect("/")
    } getOrElse {
      Ok(indexTemplate(body = html.body(itemsHigherBids(itemDAO.all(sold))), menu = mainMenu.menu))
    }
  }
  
  def itemsHigherBids(items: Seq[Item]) = items.map(i => (i, bidDAO.highest(i.id)))
}
