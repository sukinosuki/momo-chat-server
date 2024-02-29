package com.example.springboot3demo20230810.handler

import com.example.springboot3demo20230810.model.AppErrorCode
import com.example.springboot3demo20230810.model.AppException
import com.example.springboot3demo20230810.model.R
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class AppExceptionHandler {

    private val logger = LoggerFactory.getLogger(AppExceptionHandler::class.java)

    /**
     * unhandled exception
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception, request: HttpServletRequest): R<Any?> {
        logger.info("捕获全局异常, e: $e")

        // sql exception
        if (e.message?.contains("a foreign key constraint fails") == true) {
            val errorCode = AppErrorCode.SQL_OPERATE_FAIL_AS_FOREIGN_KEY_CONSTRAINT

            return R(errorCode.code, errorCode.msg, null, e.message!!)
        }

        return R.serverError(e.message, e.stackTraceToString())
//        return R(500, "服务器错误", null, e.stackTraceToString() ?: "服务器错误")
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): R<Any?> {

        return R.notFoundError(null, e.message)
    }

    /**
     * 请求方法不支持 exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
//    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED, reason = "bad request212") // 如果有reason参数，会返回默认的格式
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED) // 没有reason格式，返回R格式
    fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): R<Any?> {
        logger.info("请求方法不支持 异常, e: $e")

        return R.errCode(AppErrorCode.METHOD_NOT_ALLOWED)
    }

    /**
     * 请求content-type异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleHttpMediaTypeNotSupportedException(e: HttpMediaTypeNotSupportedException): R<Any?> {

        logger.info("请求content-type异常, e: $e")
        return R.errCode(AppErrorCode.UNSUPPORTED_MEDIA_TYPE)
    }

    /**
     * get 方法 @RequestParam 定义的类型和请求参数类型不匹配导致的类型转换异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): R<Any?> {
        logger.info("请求参数转换异常, e: $e")
//        var msg = "参数转换失败"
        val sList = e.requiredType?.simpleName?.split(".")
        val typeName = sList?.get(sList.lastIndex) ?: e.requiredType?.simpleName

        val msg = "[${e.name}] 类型错误, 期望类型 [$typeName]{${e.requiredType?.simpleName}}, 当前值: ${e.value}"
        return R.paramError(msg, e.message)
    }

    /**
     * post方法 @RequestBody 定义和类型和请求参数类型不匹配导致的类型转换异常
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): R<Any?> {
        logger.info("post方法参数转换异常 ${e.cause}")
        logger.info("post方法参数转换异常 ${e.message}")

//        var msg = "参数错误"
        var msg = when (e.cause) {
            is InvalidFormatException -> {

                val cause = (e.cause as InvalidFormatException)
                val fieldName = cause.path.get(0).fieldName

                "[${fieldName}] 类型错误, 期望类型 [${cause.targetType.simpleName}], 当前值:${cause.value}"
            }

            is MissingKotlinParameterException -> {
                val cause = e.cause as MissingKotlinParameterException
                val name = cause.parameter.name
                val type = cause.parameter.type
                "[${name}] 类型错误, 期望类型 [$type]"
            }

            is MismatchedInputException -> {

                val name = (e.cause as MismatchedInputException).path[0].fieldName
                val type = (e.cause as MismatchedInputException).targetType.simpleName
                "[$name] 类型错误, 期望类型 [$type]"
            }

            else -> "参数错误"
        }
//        if (e.cause is InvalidFormatException) {
//        }

//        if (e.cause is MissingKotlinParameterException) {
//        }
//
//        if (e.cause is MismatchedInputException) {
//        }

        return R.paramError(msg, e.cause?.message ?: e.message)
    }

    /**
     * method argument not valid exception
     *
     * exception called by valid - post method + @Valid @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidateException(e: MethodArgumentNotValidException): R<Any?> {
        var msg = ""
        if (e.hasErrors()) {
            msg = e.allErrors[0].defaultMessage ?: "参数错误"
        }

        return R(AppErrorCode.PARAM_ERR.code, msg)
    }

    /**
     * binding exception
     *
     * exception called by valid - get method + @Valid
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): R<Any?> {
        logger.info("[handleBindException] e $e")
        var msg: String = "参数错误"
        if (e.hasErrors()) {
            val error = e.allErrors[0]
            logger.info("[handleBindException] error $error")
            logger.info("[handleBindException] e.allErrors[0].code ${error.code}")
            logger.info("[handleBindException] e.allErrors[0].defaultMessage ${error.defaultMessage}")
            logger.info("[handleBindException] e.allErrors[0].objectName ${error.objectName}")

            msg = error.defaultMessage ?: msg
        }

        return R(AppErrorCode.PARAM_ERR.code, msg)
    }

    // 自定义异常
    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException, request: HttpServletRequest): R<Any?> {
        logger.info("捕获自定义错误, $e")

        return R(e.code, e.message, null)
    }

    /**
     * get 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): R<Any?> {
        logger.error("【handleConstraintViolationException】 e ${e}")
        logger.error("【handleConstraintViolationException】 e.message ${e.message}")
        logger.error("【handleConstraintViolationException】 e.constraintViolations ${e.constraintViolations}")

        val message = e.constraintViolations.first().message

        return R.paramError(message)
    }

    /**
     * 上传文件大小超出限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMaxUploadSizeExceededException(e: MaxUploadSizeExceededException): R<Any?> {
        return R.paramError("文件过大, 请控制在在2m以内")
    }

    /**
     * jwt exception
     */
    @ExceptionHandler(JwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleJwtException(e: JwtException): R<Any?> {
        logger.info("捕获jwt exception, e: $e ")

        val appErrorCode = when (e) {
            is ExpiredJwtException -> AppErrorCode.TOKEN_EXPIRED
            else -> AppErrorCode.TOKEN_ERR
        }

        return R.errCode(appErrorCode)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): R<Any?> {
        logger.error("e: ${e.message}")

        return R.paramError(e.message)
    }
}