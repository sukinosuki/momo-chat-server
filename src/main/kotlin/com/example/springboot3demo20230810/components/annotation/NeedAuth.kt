package com.example.springboot3demo20230810.components.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class NeedAuth(
    val title: String = "",
)

