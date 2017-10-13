package com.emobi.controllers

import javax.inject.Inject

import com.emobi.service.MGageService
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.mvc.{AbstractController, ControllerComponents}


class MGageSubscriptionsController @Inject()(
                                              cc: ControllerComponents,
                                              mGageService: MGageService
                                            )
  extends AbstractController(cc)  {

  //TODO Investigate which one should be used: https://doc.akka.io/docs/akka/2.5/scala/dispatchers.html
  //implicit lazy val executionContext = defaultExecutionContext
  import scala.concurrent.ExecutionContext.Implicits.global


  //TODO Consider some error codes that help business, but should not be over 8 codes
  def getStatus(id: Long) = Action.async {
    val result = mGageService.getSubscriptionStatus(id)
    result.map(r => {
      //TODO Make an log here for the status and statusText
      r.status match {

        case OK => {
          val response = r.json \ "response"
          Ok(response.get)
        }

        case NOT_FOUND => new Status(NOT_FOUND)(notFoundResponse)
        case REQUEST_TIMEOUT => new Status(INTERNAL_SERVER_ERROR)(timeoutResponse)
        case INTERNAL_SERVER_ERROR => new Status(INTERNAL_SERVER_ERROR)(gatewayError)
        case BAD_REQUEST => new Status(BAD_REQUEST)(tailorWSErrorResponse(r))
        case _ => new Status(OK)(tailorWSErrorResponse(r))
      }
    })
  }

  //TODO Extract messages to use play.i18n
  def gatewayError() = {
    Json.obj(
      "message" -> "Gateway internal error"
    )
  }

  def notFoundResponse() = {
    Json.obj(
      "message" -> "Invalid subscription or invalid gateway url"
    )
  }

  def timeoutResponse() = {
    Json.obj(
      "message" -> "Timeout to payment gateway"
    )
  }

  def tailorWSErrorResponse(response: WSResponse) = {
    Json.obj(
      "message" -> response.statusText
    )
  }
}
