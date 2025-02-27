package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.User

interface IAutoBettorRepository {
    fun findByUser(user: User): AutoBettor?
    fun save(autoBettor: AutoBettor)
    fun deleteByUser(user: User)
}