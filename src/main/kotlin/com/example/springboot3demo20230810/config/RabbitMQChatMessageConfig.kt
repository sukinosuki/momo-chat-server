//package com.example.springboot3demo20230810.config
//
//import org.slf4j.LoggerFactory
//import org.springframework.amqp.core.*
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
//import org.springframework.amqp.rabbit.core.RabbitTemplate
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//
////@ConditionalOnProperty(prefix = "app-config", value = ["use-rabbit"], havingValue = "true")
////@Configuration
//class RabbitMQChatMessageConfig {
//
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    @Autowired
//    lateinit var connectionFactory: CachingConnectionFactory
//
//    companion object {
//        const val CHAT_MESSAGE_EXCHANGE_NAME = "momotalk_chat_message_exchange"
//        const val CHAT_MESSAGE_QUEUE_NAME = "momotalk_chat_message_queue"
//    }
//
//    @Bean
//    fun queue(): Queue {
//        /**
//         * 1、name:    队列名称
//         * 2、durable: 是否持久化
//         * 3、exclusive: 是否独享、排外的。如果设置为true，定义为排他队列。则只有创建者可以使用此队列。也就是private私有的。
//         * 4、autoDelete: 是否自动删除。也就是临时队列。当最后一个消费者断开连接后，会自动删除。
//         * */
//        return Queue(CHAT_MESSAGE_QUEUE_NAME)
//    }
//
////    @Bean
//    fun exchange(): FanoutExchange {
//        return FanoutExchange(CHAT_MESSAGE_EXCHANGE_NAME)
//    }
//
//    @Bean
//    fun binding(queue: Queue, exchange: FanoutExchange): Binding {
//
//        return BindingBuilder.bind(queue).to(exchange)
//    }
//
//    @Bean
//    fun rabbitTemplate(): RabbitTemplate {
//        val rabbitTemplate = RabbitTemplate()
//
//        rabbitTemplate.connectionFactory = connectionFactory
//
//        logger.info("自定义rabbit template")
//
//        rabbitTemplate.setMandatory(true)
//
//        // TODO: 不生效
//        rabbitTemplate.setReturnsCallback {
//            logger.warn("消息从Exchange路由到Queue失败: exchange: ${it.exchange}, route: ${it.routingKey}, reply code: ${it.replyCode}, reply text: ${it.replyText}, message: ${it.message}")
//        }
//
//        rabbitTemplate.setConfirmCallback { correlationData, ack, cause ->
//            if (ack) {
//                logger.info("消息发送到队列成功, data: $correlationData, id: ${correlationData?.id}")
//            } else {
//                logger.info("消息发送到队列失败, cause: $cause")
//            }
//        }
//
//        return rabbitTemplate
//    }
//}
//
////2023-09-01 18:19:54.158 ERROR 18888 --- [168.10.101:5672] o.s.a.r.c.CachingConnectionFactory       : Shutdown Signal: channel error; protocol method: #method<channel.close>(reply-code=406, reply-text=PRECONDITION_FAILED - unknown delivery tag 1, class-id=60, method-id=80)
////2023-09-01 18:19:55.156  INFO 18888 --- [ntContainer#0-2] o.s.a.r.l.SimpleMessageListenerContainer : Restarting Consumer@7a449ee1: tags=[[amq.ctag-332IVVl_J2Z8P5ts2ZekSQ]], channel=Cached Rabbit Channel: PublisherCallbackChannelImpl: AMQChannel(amqp://hanami@192.168.10.101:5672/,2), conn: Proxy@366fd3cb Shared Rabbit Connection: SimpleConnection@da51aa8 [delegate=amqp://hanami@192.168.10.101:5672/, localPort=7675], acknowledgeMode=AUTO local queue size=0
