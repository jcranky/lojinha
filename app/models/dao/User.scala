package models.dao

case class User(email: String, name: String, password: String)

trait UserDAO {
  def authenticate(email: String, password: String): Option[User]
  
  def findByEmail(email: String): Option[User]
}
