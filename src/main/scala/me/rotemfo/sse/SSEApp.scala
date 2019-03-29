package me.rotemfo.sse

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}

import scala.concurrent.ExecutionContext

private[sse] trait SSEApp {
  protected final implicit val actorSystem: ActorSystem = ActorSystem("akka-sse")
  protected final implicit val executionContext: ExecutionContext = actorSystem.dispatcher
  protected final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(actorSystem))

}
