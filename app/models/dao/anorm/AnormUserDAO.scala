package models.dao.anorm

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import models.dao.{User, UserDAO}

object AnormUserDAO extends UserDAO {
  val user = {
    int("id") ~ str("email") ~ str("name") ~ str("passwd") map {
      case id~email~name~passwd => User(id, email, name, passwd)
    }
  }

  def authenticate(email: String, passwd: String): Option[User] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM _user WHERE email = {email} AND passwd = {passwd}").on('email -> email, 'passwd -> passwd).as(user singleOpt)
  }

  def findByEmail(email: String): Option[User] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM _user WHERE email = {email}").on('email -> email).as(user singleOpt)
  }

  def changePassword(email: String, newPasswd: String) = DB.withConnection { implicit c =>
    SQL("UPDATE _user set passwd = {password} WHERE email = {email}").on(
      'password -> newPasswd, 'email -> email).executeUpdate()
  }
}
