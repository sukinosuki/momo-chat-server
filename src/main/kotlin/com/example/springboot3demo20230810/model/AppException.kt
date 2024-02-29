package com.example.springboot3demo20230810.model


// 自定义的exception类需要继承RuntimeException，否则mybatis的@Transactional注解下在service方法里抛出AppException时事务不会回滚
class AppException(errCode: AppErrorCode, msg: String? = null) : RuntimeException() {

    var code: Int = 0

    override var message: String = ""

    init {
        code = errCode.code
        message = msg ?: errCode.msg
    }

    companion object {
        // 参数错误异常
        fun paramError(msg: String? = "参数错误"): AppException {
            return AppException(AppErrorCode.PARAM_ERR, msg)
        }

        // 资源重复资源(例如唯一索引重复
        fun repeatedSourceError(msg: String? = null) = AppException(AppErrorCode.REPEATED_SOURCE, msg)

        /**
         * 操作失败异常
         */
        fun actionFailError(msg: String? = null): AppException {
            return AppException(AppErrorCode.ACTION_FAIL, msg)
        }

        fun unAuthorizeError(msg: String? = null): AppException {
            return AppException(AppErrorCode.UN_AUTHORIZE, msg)
        }

        /**
         * 图片上传失败
         */
        fun uploadError(msg: String? = null): AppException {
            return AppException(AppErrorCode.UPLOAD, msg)
        }

        /**
         * 资源不存在异常
         */
        fun recordNotFoundError(msg: String? = null): AppException {
            return AppException(AppErrorCode.RECORD_NOT_FOUND, msg)
        }

        fun loginError(msg: String? = null): AppException {
            return AppException(AppErrorCode.LOGIN_ERR, msg)
        }

        fun loginExpired(msg: String? = null): AppException {
            return AppException(AppErrorCode.LOGIN_EXPIRED, msg)
        }
    }
}