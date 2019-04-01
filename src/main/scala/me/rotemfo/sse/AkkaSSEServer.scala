package me.rotemfo.sse

import java.util.UUID

import akka.NotUsed
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.scaladsl.Source
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.HttpMethods._

import scala.concurrent.duration._

object AkkaSSEServer extends HttpApp with AkkaSystem {

  private final val headers =
    `Access-Control-Allow-Origin`.* ::
      `Access-Control-Allow-Credentials`(true) ::
      `Access-Control-Allow-Headers`("Content-Type", "Authorization") ::
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE) :: Nil

  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

  override protected def routes: Route = {
    path("users") {
      get {
        respondWithDefaultHeaders(headers) {
          complete {
            Source
              .tick(2.seconds, 2.seconds, NotUsed)
              .map(_ => {
                val uuid = UUID.randomUUID().toString
                val user = User(uuid.hashCode, uuid).toString
                ServerSentEvent(user)
              })
              .keepAlive(1.second, () => ServerSentEvent.heartbeat)
          }
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    startServer("0.0.0.0", 8000)
  }
}
