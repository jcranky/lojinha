package models

case class User(email: String, name: String, password: String)

object User {
  def authenticate(email: String, password: String): Option[User] = None
  
  def findByEmail(email: String): Option[User] = None
}
