package net.stakemetrics.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan("net.stakemetrics")
@EntityScan("net.stakemetrics.persistence.models")
@EnableJpaRepositories("net.stakemetrics.persistence.jpa")
class BackEndApplication

fun main(args: Array<String>) {
    runApplication<BackEndApplication>(*args)
}
