package com.medisupply.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/api/hello")
    fun hello(): Map<String, String> {
        return mapOf("message" to "¡Hola desde el backend de Medisupply! 🚀")
    }
}
