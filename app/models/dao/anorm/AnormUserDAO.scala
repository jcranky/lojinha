package models.dao.anorm

import anorm.SqlParser._
import anorm._
import javax.inject.{Inject, Singleton}
import models.dao.{User, UserDAO}
import play.api.db.Database

@Singleton
class AnormUserDAO @Inject() (db: Database) extends UserDAO {
  val user = {
    int("id") ~ str("email") ~ str("name") ~ str("passwd") map {
      case id~email~name~passwd => User(id, email, name, passwd)
    }
  }

  def authenticate(email: String, passwd: String): Option[User] = db.withConnection { implicit c =>
    SQL("SELECT * FROM _user WHERE email = {email} AND passwd = {passwd}")
      .on(Symbol("email") -> email, Symbol("passwd") -> passwd).as(user singleOpt)
  }

  def findByEmail(email: String): Option[User] = db.withConnection { implicit c =>
    SQL("SELECT * FROM _user WHERE email = {email}")
      .on(Symbol("email") -> email).as(user singleOpt)
  }

  def changePassword(email: String, newPasswd: String): Unit = db.withConnection { implicit c =>
    SQL("UPDATE _user set passwd = {password} WHERE email = {email}")
      .on(Symbol("password") -> newPasswd, Symbol("email") -> email).executeUpdate()
  }
}
