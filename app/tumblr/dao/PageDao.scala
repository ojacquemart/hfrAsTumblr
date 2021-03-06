package tumblr.dao

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

import play.api.Logger
import play.api.libs.json._

import play.modules.reactivemongo.json.BSONFormats._

import reactivemongo.bson._

import tumblr.model._
import tumblr.model.Page.{format, writes}
import reactivemongo.core.commands._

object PageDao extends MongoDao[Page, BSONObjectID] {

  val collectionName = "pages"

  implicit val formatImage: Format[Image] = Json.format[Image]
  implicit val writesImage: Writes[Image] = Json.writes[Image]

  def saveOrUpdate(page: Page) {
    val futurePage = find(page)

    futurePage onSuccess { case result =>
      result match {
        case None => {
          Logger.debug("Save the page, no existing one")
          save(page)
        }
        case Some(p: Page) => {
          Logger.debug(s"Page found: $p")
          update(p)
        }
      }
    }

    futurePage.onFailure { case failure =>
      Logger.warn("Failed to retrieve page " + page, failure)
    }
  }

  def update(page: Page): Future[reactivemongo.core.commands.LastError] = {
    Logger.debug(s"$collectionName - Update page " + page)

    val selector = Json.obj("siteId" -> page.siteId, "pageNumber" -> page.pageNumber)
    val modifier = Json.obj(
      "$set" -> Json.obj(
        "images_1" -> page.images_1,
        "images_2" -> page.images_2
      ),
      "$inc" -> Json.obj("nbViews" -> 1)
    )

    collection.update(selector, modifier)
  }

  def find(page: Page): Future[Option[Page]] = findBySlugAndPageNumber(page.siteId, page.pageNumber)

  def findBySlugAndPageNumber(siteId: String, pageNumber: Int): Future[Option[Page]] = {
    Logger.debug(s"Find head for $siteId and $pageNumber")

    collection
      .find(Json.obj("siteId" -> siteId, "pageNumber" -> pageNumber))
      .one[Page]
  }

  def changeSiteId(oldSiteId: String, newSiteId: String) = {
    Logger.debug(s"Change siteId from $oldSiteId to $newSiteId")

    val selector = Json.obj("siteId" -> oldSiteId)
    val modifier = Json.obj(
      "$set" -> Json.obj("siteId" -> newSiteId)
    )
    collection.update(selector, modifier, multi = true)
  }

  def deleteBySiteId(siteId: String) = {
    Logger.debug(s"$collectionName - deletePageBySiteId($siteId)")
    collection.remove(Json.obj("siteId" -> siteId))
  }

  def statsBySiteId() = {
    Logger.debug("Count pages by siteId aka slug.")

    val cmd = Aggregate(collectionName,
      Seq(
        GroupField("siteId")(("nbDocs" -> SumValue(1)), ("nbViews" -> SumField("nbViews"))),
        Sort(Seq(Descending("nbDocs")))
      )
    )
    db.command(cmd)
  }

}
