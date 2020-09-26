package controllers

import javax.inject.Inject
import models.dao.{CategoryDAO, DAOFactory}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Request}
import views.html

class MainMenu @Inject() (val messagesApi: MessagesApi) extends SecuredController with I18nSupport {
  val categoryDAO: CategoryDAO = DAOFactory.categoryDAO

  def menu(implicit request: Request[AnyContent]) = html.menu(categoryDAO.all())
}
