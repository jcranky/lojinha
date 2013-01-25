package models.dao.anorm

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import models.dao.{Category, CategoryDAO}

object AnormCategoryDAO extends CategoryDAO {
  val category = {
    int("id") ~ str("display_name") ~ str("url_name") map {
      case id~displayName~urlName => Category(id, displayName, urlName)
    }
  }

  def create(displayName: String, urlName: String) = DB.withConnection { implicit c =>
    SQL("INSERT INTO category(display_name, url_name) VALUES({displayName}, {urlName})").on(
      'displayName -> displayName, 'urlName -> urlName).executeUpdate()
  }

  def findById(id: Int): Option[Category] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM category WHERE id = {id}").on('id -> id).as(category singleOpt)
  }

  def findByName(urlName: String): Option[Category] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM category WHERE url_name = {urlName}").on('urlName -> urlName).as(category singleOpt)
  }

  def all(): List[Category] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM category ORDER BY display_name").as(category *)
  }
}
