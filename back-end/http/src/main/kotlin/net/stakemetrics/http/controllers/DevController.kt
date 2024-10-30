package net.stakemetrics.http.controllers

import net.stakemetrics.application.utils.KotlinScript
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dev")
class DevController(private val kotlinScript: KotlinScript) {

    @PostMapping("/kotlin-script")
    fun kotlinScript(): ResponseEntity<Unit> {
        kotlinScript.run()
        return ResponseEntity.status(HttpStatus.OK).build()
    }

}