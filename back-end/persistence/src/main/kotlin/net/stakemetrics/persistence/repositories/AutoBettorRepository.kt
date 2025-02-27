package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.repositories.IAutoBettorRepository
import net.stakemetrics.persistence.jpa.AutoBettorJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class AutoBettorRepository(private val autoBettorJpaRepository: AutoBettorJpaRepository) : IAutoBettorRepository {

    override fun findByUser(user: User): AutoBettor? {
        return autoBettorJpaRepository.findByUserId(user.id)?.toDomain()
    }

    override fun save(autoBettor: AutoBettor) {
        autoBettorJpaRepository.save(autoBettor.toModel())
    }

    override fun deleteByUser(user: User) {
        autoBettorJpaRepository.deleteByUserId(user.id)
    }

}