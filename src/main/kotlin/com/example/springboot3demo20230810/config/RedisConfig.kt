package com.example.springboot3demo20230810.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

// redis配置中jackson处理localdatetime: https://www.jianshu.com/p/5fb3da809600
@Configuration
class RedisConfig {

//    private fun serializeCfg(): ObjectMapper {
//        var objectMapper = ObjectMapper()
//        val javaTimeModule = JavaTimeModule()
//        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//        javaTimeModule.addSerializer(
//            LocalDateTime::class.java,
//            LocalDateTimeSerializer(dtf)
//        )
//        javaTimeModule.addDeserializer(
//            LocalDateTime::class.java,
//            LocalDateTimeDeserializer(dtf)
//        )
//        objectMapper.registerModule(javaTimeModule)
//        val simpleModule = SimpleModule()
//        simpleModule.addSerializer(Long::class.java, ToStringSerializer.instance)
//        simpleModule.addSerializer(java.lang.Long.TYPE, ToStringSerializer.instance)
//        objectMapper.registerModule(simpleModule)
//        //必须加上
//        objectMapper.activateDefaultTyping(
//            objectMapper.polymorphicTypeValidator,
//            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY
//        )
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//
//        return objectMapper
//    }

    @Bean
    fun  redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any?> {
//        val objectMapper = ObjectMapper()
//        objectMapper.registerModule(JavaTimeModule())
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val objectMapper = jacksonObjectMapper()
//        objectMapper.registerModule(javaTimeModule)
//        objectMapper.findAndRegisterModules()

//        val objectMapper: ObjectMapper = Jackson2ObjectMapperBuilder().failOnEmptyBeans(false)
//            .failOnUnknownProperties(false)
//            .indentOutput(false)
//            .serializationInclusion(JsonInclude.Include.NON_NULL)
//            .modules( // Optional
//                Jdk8Module(),  // Dates/Times
//                JavaTimeModule()
//            )
//            .featuresToDisable(
//                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
//                DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS,
//                SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
//            ).build<ObjectMapper>()

        // https://stackoverflow.com/questions/65886082/how-to-use-genericjackson2jsonredisserializer
        // GenericJackson2JsonRedisSerializer使用默认的objectMapper, 会选择@class
//        val jacksonSerializer = GenericJackson2JsonRedisSerializer()

        val jacksonSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        // 为了能使用自定义的ObjectMapper而不丢失@class, 需要执行这两句(这里是 GenericJackson2JsonRedisSerializer() 构造方法里的
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper,null)
        // 保留@class
        objectMapper.activateDefaultTyping(objectMapper.polymorphicTypeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)

        val redisTemplate = RedisTemplate<String, Any?>()
        redisTemplate.setConnectionFactory(connectionFactory)

        val stringRedisSerializer = StringRedisSerializer()

        redisTemplate.keySerializer = stringRedisSerializer
        redisTemplate.valueSerializer = jacksonSerializer

        redisTemplate.hashKeySerializer = stringRedisSerializer
        redisTemplate.hashValueSerializer = jacksonSerializer

//        redisTemplate.afterPropertiesSet() // TODO: 不知道作用

        return redisTemplate
    }
}