package com.emobi.controllers

import com.emobi.service.MGageService
import com.emobi.utils.SubscriptionsWSTestData.createMGageResponse
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.specs2.mock._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSResponse
import play.api.mvc._
import play.api.test.Helpers.{GET => GET_REQUEST, POST => POST_REQUEST, _}
import play.api.test._

import scala.concurrent.Future

/**
  * This test suite is only for routes mapping testing
  */
class MGageSubscriptionsRouteTest extends PlaySpec with GuiceOneAppPerSuite with Results with Mockito {

  import scala.concurrent.ExecutionContext.Implicits.global

  val mockService = mock[MGageService]
  val result = mock[WSResponse]
  result.json returns createMGageResponse()
  result.status returns OK

  mockService.getSubscriptionStatus(1) returns Future(result)

  override def fakeApplication =
    new GuiceApplicationBuilder()
      .overrides(bind[MGageService].toInstance(mockService))
      .build()

  "Subscription routes" must {
      "work as the mapping in the route file" in {
        route(app, FakeRequest(GET_REQUEST, "/subscriptions/1/status")).map(status(_)) mustBe Some(OK)
      }
  }

  "Subscription routes" must {
    "get 404 once wrong url is invoked" in {
      route(app, FakeRequest(GET_REQUEST, "/subscription/1/status")).map(status(_)) mustBe Some(NOT_FOUND)
    }
  }

  //TODO Should have a way to test Play exception handler
}


