package models.dao

import com.google.inject.ImplementedBy
import models.dao.anorm.AnormUserDAO

case class User(id: Int, email: String, name: String, password: String)

@ImplementedBy(classOf[AnormUserDAO])
trait UserDAO {
  def authenticate(email: String, password: String): Option[User]

  def findByEmail(email: String): Option[User]

  def changePassword(email: String, newPasswd: String): Unit
}
