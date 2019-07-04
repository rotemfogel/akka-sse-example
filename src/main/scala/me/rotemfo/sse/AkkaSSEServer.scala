package me.rotemfo.sse

import java.util.UUID

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.{HttpApp, Route}

object AkkaSSEServer extends HttpApp with AkkaSystem {

  private final val headers =
    `Access-Control-Allow-Origin`.* ::
      `Access-Control-Allow-Credentials`(true) ::
      `Access-Control-Allow-Headers`("Content-Type", "Authorization") ::
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE) :: Nil

  private final val dispatcher = actorSystem.actorOf(DispatcherActor.props, "dispatcher")

  override protected def routes: Route = {
    path("users") {
      get {
        respondWithDefaultHeaders(headers) {
          complete {
            dispatcher ! UUID.randomUUID().toString
            StatusCodes.OK
          }
        }
      }
    }
  }


  def main(args: Array[String]): Unit = {
    startServer("0.0.0.0", 8000)
  }
}
