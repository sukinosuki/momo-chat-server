package com.example.springboot3demo20230810.modules.app_config

import com.example.springboot3demo20230810.model.R
import com.example.springboot3demo20230810.modules.app_config.model.AppManifestConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AppConfigController {

    @GetMapping("/app-config/manifest")
    fun get(): R<AppManifestConfig> {

        // TODO: 根据dev、prod环境区分
        val config = AppManifestConfig(
            stampBaseUrl = "http://192.168.10.81:3000/images/stamp",
            momoFontUrl = "http://192.168.10.81:3000/src/assets/Blueaka.otf"
        )

        return R.ok(config)
    }
}