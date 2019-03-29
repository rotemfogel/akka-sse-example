package me.rotemfo.sse

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source

object SSEClient extends SSEApp {

  private def Get(url: String): HttpRequest = {
    HttpRequest(HttpMethods.GET, url)
  }

  import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._

  def main(args: Array[String]): Unit = {
    Http()
      .singleRequest(Get("http://localhost:8000/events"))
      .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
      .foreach(_.runForeach(println))
  }
}
