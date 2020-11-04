package models.dao.anorm

import anorm.SqlParser._
import anorm._
import javax.inject.{ Inject, Singleton }
import models.dao.{ Category, CategoryDAO }
import play.api.db.Database

@Singleton
class AnormCategoryDAO @Inject() (db: Database) extends CategoryDAO {

  private val category: RowParser[Category] = {
    (int("id") ~ str("display_name") ~ str("url_name")).map {
      case id ~ displayName ~ urlName => Category(id, displayName, urlName)
    }
  }

  def create(displayName: String, urlName: String): Int = db.withConnection { implicit c =>
    SQL("INSERT INTO category(display_name, url_name) VALUES({displayName}, {urlName})")
      .on(Symbol("displayName") -> displayName, Symbol("urlName") -> urlName)
      .executeUpdate()
  }

  def findById(id: Int): Option[Category] = db.withConnection { implicit c =>
    SQL("SELECT * FROM category WHERE id = {id}")
      .on(Symbol("id") -> id)
      .as(category singleOpt)
  }

  def findByName(urlName: String): Option[Category] = db.withConnection { implicit c =>
    SQL("SELECT * FROM category WHERE url_name = {urlName}")
      .on(Symbol("urlName") -> urlName)
      .as(category singleOpt)
  }

  def all(): List[Category] = db.withConnection { implicit c =>
    SQL("SELECT * FROM category ORDER BY display_name").as(category *)
  }
}
