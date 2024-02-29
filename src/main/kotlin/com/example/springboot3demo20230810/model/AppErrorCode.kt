package com.example.springboot3demo20230810.model

enum class AppErrorCode(val code: Int, val msg: String) {
    PARAM_ERR(400, "参数错误"),
    UN_AUTHORIZE(401, "请先登录"),
    FORBIDDEN(403, "权限不足"),
    RECORD_NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    SERVER_ERROR(500, "服务器错误"),

    REPEATED_SOURCE(4010, "资源重复"),
    TOKEN_ERR(4100, "token错误"),
    TOKEN_EXPIRED(4101, "token过期"),

    LOGIN_ERR(4110, "用户名或密码错误"),
    LOGIN_EXPIRED(4111, "登录过期"),

    UPLOAD(4200, "图片上传失败"),

    ACTION_FAIL(5000, "操作失败"),

    SQL_OPERATE_FAIL_AS_FOREIGN_KEY_CONSTRAINT(5001, "资源与其它表的资源存在绑定"),
}