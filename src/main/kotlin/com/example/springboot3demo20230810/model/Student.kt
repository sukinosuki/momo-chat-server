package com.example.springboot3demo20230810.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

// 需要该注解忽略json有的但是data class没有的字段, 不然会报错
// com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException:
// Unrecognized field "IsReleased" (class com.example.springboot3demo20230810.model.Student), not marked as ignorable (8 known properties: "DevName", "Id", "CollectionTexture", "FamilyName", "devName", "id", "collectionTexture", "familyName"])
@JsonIgnoreProperties(ignoreUnknown = true)
data class Student(
    // jackson会自动把Id转为id这样的驼峰类型
    var Id: Long,
    var CollectionTexture: String,
    var DevName: String,
    var FamilyName: String,

    var unreadCount: Int,
//    @JsonIgnore
    // 是否在线
    var isOnline: Boolean,
    // 是否最近有登录
    var isLatestLogin: Boolean
)
