package me.rotemfo.sse

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * project: akka-sse-example
  * package: me.rotemfo.sse
  * file:    DispatcherActor
  * created: 2019-07-03
  * author:  rotem
  */
class DispatcherActor() extends AkkaSystem with Actor with ActorLogging {
  override def receive: Receive = LoggingReceive {
    case record: ConsumerRecord[_, _] => context.children.foreach(_ ! record.value())
    case s: String => {
      context.actorOf(UserActor.props(s))
      log.info(s"created actor $s")
    }
  }

  private def startConsumer: Control = {
    val config = context.system.settings.config.getConfig("akka.kafka.consumer")
    val stringSerializer = new StringDeserializer
    val kafka = "127.0.0.1:9092"
    val clientId = "akka-sse"
    val consumerGroup = "akka-sse-dispatcher"
    val topics = Set("users")

    val consumerSettings =
      ConsumerSettings(config, stringSerializer, stringSerializer)
        .withBootstrapServers(kafka)
        .withClientId(clientId)
        .withGroupId(consumerGroup)
        .withPollInterval(1.seconds)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

    Consumer.atMostOnceSource(consumerSettings, Subscriptions.topics(topics))
      .mapAsync(1) { msg =>
        log.info(msg.value())
        Future.successful(context.children.foreach(c => c ! msg))
      }
      .to(Sink.ignore).run()


    //    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    //      import GraphDSL.Implicits._
    //
    //      val kafkaSource = Consumer.plainSource(consumerSettings, Subscriptions.topics("users"))
    //      val mapFromConsumerRecord = Flow[ConsumerRecord[String, String]].map(record => record.value())
    //      val actorSink = Sink.foreach(msg => context.children.foreach(child => child ! msg))
    //
    //      kafkaSource ~> mapFromConsumerRecord ~> actorSink
    //
    //      ClosedShape
    //    }).run()
  }

  override def preStart(): Unit = {
    startConsumer
    log.info("started consumer")
  }

  override def postStop(): Unit = context.children.foreach(c => {
    context.unwatch(c)
    context.stop(c)
  })
}

object DispatcherActor {
  def props: Props = Props(classOf[DispatcherActor])
}