package com.example.springboot3demo20230810

import com.example.springboot3demo20230810.properties.JWTProperty
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication(exclude = [RabbitAutoConfiguration::class])
@EnableConfigurationProperties(value = [JWTProperty::class])
class SpringBoot3Demo20230810Application

fun main(args: Array<String>) {
	runApplication<SpringBoot3Demo20230810Application>(*args)
}
