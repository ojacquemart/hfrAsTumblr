import java.util.concurrent.TimeUnit

import concurrent.duration.Duration
import concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global

import util.Try

import org.specs2.mutable._
import org.specs2.execute._

import play.api.Play.current
import play.api.Logger
import play.api.test._
import play.api.test.Helpers._
import play.modules.reactivemongo.ReactiveMongoPlugin

import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter

import TestHelpers._

import hfr._

class PageCollectionSpec extends Specification {

  "The ContentFinder class" should {

    "save new page and retrieve it" in new FakeApp {
      val images_1 = List(new Image("a"), new Image("b"), new Image("c"))
      val images_2 = List(new Image("d"), new Image("e"), new Image("f"))
      val newPage = new Page("foo", 1, images_1, images_2)
      PageCollection.save(newPage)

      val futureOptionPage = PageCollection.findHeadByTopicIdAndPageOffset("foo", 1)
      Await.ready(futureOptionPage, Duration(5, TimeUnit.SECONDS))

      val optionPage = option(futureOptionPage)
      val page = optionPage.get
      page.topicId must be equalTo ("foo")
      page.title must be equalTo ("Page 1")
      page.offset must be equalTo (1)
      page.images_1 must be equalTo images_1
      page.images_2 must be equalTo images_2
    }

    "save and then update existing page" in new FakeApp {
      val images_1 = List(new Image("a"))
      val images_2 = List(new Image("d"))
      val newPage = new Page("foo2", 2, images_1, images_2)
      PageCollection.save(newPage)

      // check save

      val futureOptionPage = PageCollection.findHeadByTopicIdAndPageOffset("foo2", 2)
      Await.ready(futureOptionPage, Duration(60, TimeUnit.SECONDS))

      val optionPage = option(futureOptionPage)
      optionPage must not be equalTo(None)

      val page = optionPage.get
      page.images_1 must be equalTo images_1
      page.images_2 must be equalTo images_2

      // update

      val images_1ToUpdate = List(new Image("a"), new Image("b"), new Image("c"))
      val images_2ToUpdate = List(new Image("d"), new Image("e"), new Image("g"))
      val pageToUpdate = new Page("foo2", 2, images_1ToUpdate, images_2ToUpdate)
      val futureUpdate = PageCollection.update(pageToUpdate)
      Await.ready(futureUpdate, Duration(60, TimeUnit.SECONDS))

      // check update

      val futureOptionPageUpdated = PageCollection.findHeadByTopicIdAndPageOffset("foo2", 2)
      Await.ready(futureOptionPageUpdated, Duration(60, TimeUnit.SECONDS))

      val optionPageUpdated = option(futureOptionPageUpdated)
      optionPageUpdated must not be equalTo(None)

      val pageUpdated = optionPageUpdated.get
      pageUpdated.images_1 must be equalTo(images_1ToUpdate)
      pageUpdated.images_2 must be equalTo(images_2ToUpdate)
    }

  }

}