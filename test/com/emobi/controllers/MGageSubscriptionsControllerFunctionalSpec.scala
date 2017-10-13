package com.emobi.controllers

import com.emobi.service.MGageService
import com.emobi.utils.SubscriptionsWSTestData._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.specs2.mock._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsValue
import play.api.libs.ws.WSResponse
import play.api.mvc._
import play.api.test.Helpers.{GET => GET_REQUEST, _}
import play.api.test.{FakeRequest, WithApplication}

import scala.concurrent.Future

class MGageSubscriptionsControllerFunctionalSpec extends PlaySpec
  with GuiceOneAppPerSuite with Results with Mockito {

  import scala.concurrent.ExecutionContext.Implicits.global

  //TODO Add more tests for some error code if they are important to the business

  "Subscription Controller" should {

    "get status properly when the service is in a good mod" in new WithApplication(app = createApplication(OK)) {

      val controller = app.injector.instanceOf[MGageSubscriptionsController]
      val result = controller.getStatus(1)(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result).toString() mustBe """{"mig_sid":1,"status":"active"}"""
    }

    "return proper message for and 500 gateway timeout exception" in new WithApplication(app = createApplication(REQUEST_TIMEOUT)) {

      val controller = app.injector.instanceOf[MGageSubscriptionsController]
      val result = controller.getStatus(1)(FakeRequest())

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentAsJson(result).toString() mustBe """{"message":"Timeout to payment gateway"}"""
    }

    "return proper message and 500 for gateway internal exception" in  new WithApplication(app = createApplication(INTERNAL_SERVER_ERROR)) {

      val controller = app.injector.instanceOf[MGageSubscriptionsController]
      val result = controller.getStatus(1)(FakeRequest())

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentAsJson(result).toString() mustBe """{"message":"Gateway internal error"}"""
    }

    "return proper message and 400 for bad request exception" in  new WithApplication(app = createApplication(BAD_REQUEST, "wrong json format")) {

      val controller = app.injector.instanceOf[MGageSubscriptionsController]
      val result = controller.getStatus(1)(FakeRequest())

      status(result) mustBe BAD_REQUEST
      contentAsJson(result).toString() mustBe """{"message":"wrong json format"}"""
    }

    "return proper message and 200 for the other exceptions" in  new WithApplication(app = createApplication(PAYMENT_REQUIRED, "not enough money")) {

      val controller = app.injector.instanceOf[MGageSubscriptionsController]
      val result = controller.getStatus(1)(FakeRequest())

      status(result) mustBe OK
      contentAsJson(result).toString() mustBe """{"message":"not enough money"}"""
    }
  }

  def createApplication(status: Int, statusText: String = "", response: JsValue = createMGageResponse) = {

    val mockService = mock[MGageService]
    val result = mock[WSResponse]
    result.json returns response
    result.status returns status
    result.statusText returns statusText

    mockService.getSubscriptionStatus(1) returns Future(result)
    GuiceApplicationBuilder().overrides(bind[MGageService].toInstance(mockService)).build()

  }
}
