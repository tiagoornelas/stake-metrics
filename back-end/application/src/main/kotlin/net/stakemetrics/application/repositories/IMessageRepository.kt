package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.Message

interface IMessageRepository {
    fun save(message: Message)
}