package com.emobi

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
  * This test suite is for module subscription wiring
  */
class MGageSubscriptionsWiringTest extends PlaySpec with GuiceOneAppPerSuite with Results with Mockito {

  import MGageService._

  //TODO Should find a way not using application.conf for test
  "Subscription service config" must {
      "have expected values" in {
        val service = app.injector.instanceOf[MGageService]
        service.config.getString(API_KEY_CONFIG) mustBe "da39a3ee5e6b4b0d3255bfef95601890afd80709"
        service.config.getString(URL_CONFIG) mustBe "https://api.migpay.com"
      }
  }
}


