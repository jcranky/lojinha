package controllers

import javax.inject.Inject
import models.dao.{CategoryDAO, UserDAO}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Request}
import views.html

class MainMenu @Inject() (categoryDAO: CategoryDAO, val userDAO: UserDAO, val messagesApi: MessagesApi) extends SecuredController with I18nSupport {

  def menu(implicit request: Request[AnyContent]) =
    html.menu(categoryDAO.all())
}
