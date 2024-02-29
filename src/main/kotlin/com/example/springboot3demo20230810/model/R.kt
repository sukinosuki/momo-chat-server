package com.example.springboot3demo20230810.model

data class R<T>(
    val code: Int,
    val msg: String,
    val data: T? = null,
    val errMsg: String? = null
) {

    companion object {
        fun <T> ok(data: T? = null): R<T> {

            return R(
                code = 0,
                msg = "ok",
                data = data
            )
        }

        /**
         * 参数错误
         */
        fun paramError(msg: String? = null, errMsg: String? = null): R<Any?> {
            return R(
                code = AppErrorCode.PARAM_ERR.code,
                msg = msg ?: AppErrorCode.PARAM_ERR.msg,
                errMsg = errMsg
            )
        }

        fun notFoundError(msg: String? = null, errMsg: String? = null): R<Any?> {
            return R(
                code = AppErrorCode.RECORD_NOT_FOUND.code,
                msg = msg ?: AppErrorCode.RECORD_NOT_FOUND.msg,
                errMsg = errMsg,
            )
        }

        fun serverError(msg: String? = null, errMsg: String? = null): R<Any?> {
            val errorCode = AppErrorCode.SERVER_ERROR

            return R(errorCode.code, msg ?: errorCode.msg, null, errMsg)
        }

        // 返回code错误
        fun errCode(e: AppErrorCode): R<Any?> {
            return R(e.code, e.msg)
        }
    }
}
