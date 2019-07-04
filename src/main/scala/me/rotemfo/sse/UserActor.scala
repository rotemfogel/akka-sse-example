package me.rotemfo.sse

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive

/**
  * project: akka-sse-example
  * package: me.rotemfo.sse
  * file:    UserActor
  * created: 2019-07-03
  * author:  rotem
  */
class UserActor extends Actor with ActorLogging{
  override def receive: Receive = LoggingReceive {
    case x @ _ => log.info(s"$x")
  }
}

object UserActor {
  def props(name: String): Props = Props(classOf[UserActor], name)
}