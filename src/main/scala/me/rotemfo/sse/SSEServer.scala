package me.rotemfo.sse

import java.util.UUID

import akka.NotUsed
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.scaladsl.Source

import scala.concurrent.duration._

object SSEServer extends HttpApp with SSEApp {

  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

  override protected def routes: Route = {
    path("events") {
      get {
        complete {
          Source
            .tick(2.seconds, 2.seconds, NotUsed)
            .map(_ => ServerSentEvent(UUID.randomUUID().toString))
            .keepAlive(1.second, () => ServerSentEvent.heartbeat)
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    startServer("0.0.0.0", 8000);
  }
}
