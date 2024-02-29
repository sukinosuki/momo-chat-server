package com.example.springboot3demo20230810.modules

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception

@RestController
class ExceptionController {

    @RequestMapping("/error/exthrow")
    fun rethrow(request: HttpServletRequest): String {
        val e = request.getAttribute("filter.error")
        if (e is Exception) {
            throw e
        }

        return "ok"
    }
}