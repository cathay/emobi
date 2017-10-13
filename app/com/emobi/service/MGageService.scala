package com.emobi.service

import com.google.inject.Inject
import com.typesafe.config.Config
import play.api.libs.ws.{EmptyBody, WSClient}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MGageService @Inject()(
                              val ws: WSClient,
                              val config: Config
                            ) {

  import MGageService._

  //TODO Consider to inject executionContext to constructor
  def getSubscriptionStatus(id: Long)(implicit executionContext: ExecutionContext) = {

    val url  = s"${getUrl(config)}/subscriptions/$id/status"
    ws.url(url)
      .addHttpHeaders(createRequestHeaders(config).toSeq: _*)
      .withRequestTimeout(getGatewayTimeout(config).millis)
      .post(EmptyBody)
  }
}

object MGageService {
  val URL_CONFIG = "com.emobi.gateway.mgage.url"
  val API_KEY_CONFIG = "com.emobi.gateway.mgage.apiKey"
  val API_TIME_OUT = "com.emobi.gateway.timeout"

  //TODO This function should be extracted to an utils if it is invoked by multiple services
  def createRequestHeaders(config: Config) = {
    Map(
      "Accept" -> "application/json",
      "MigPay-API-Key" -> config.getString(API_KEY_CONFIG)
    )
  }

  def getUrl(config: Config) = config.getString(URL_CONFIG)

  def getGatewayTimeout(config: Config) = config.getInt(API_TIME_OUT)
}
