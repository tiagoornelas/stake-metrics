package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyStatus
import com.stakemetrics.backend.domain.exceptions.NotAllowedException
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRepositoryPort
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.subscription.enums.FeatureTypes
import com.stakemetrics.backend.plugins.subscription.service.SubscriptionService
import org.springframework.stereotype.Service

@Service
class FifaStrategyResourceValidator(
    private val fifaStrategyRepositoryPort: FifaStrategyRepositoryPort,
    private val subscriptionService: SubscriptionService
) {
    private val globalMaxStrategies = 30

    fun validate(dto: FifaDTO.FifaStrategyRequest) {
        if (dto.name.isBlank() || dto.marketSubTypes.isEmpty() || dto.leagues.isEmpty()) {
            throw IllegalArgumentException("Name, marketSubTypes and leagues cannot be blank")
        }

        if (dto.scopes.isEmpty()) {
            throw IllegalArgumentException("Scopes cannot be empty")
        }

        dto.scopes.forEach { scope ->
            val ruleTypesSet = mutableSetOf<FifaRuleTypes>()
            scope.rules.forEach { rule ->
                val ruleType = FifaRuleTypes.valueOf(rule.type.toString())

                if (ruleType in ruleTypesSet) {
                    throw IllegalArgumentException("Duplicate rule type found in scope")
                } else {
                    ruleTypesSet.add(ruleType)
                }

                val minValue = ruleType.minValue
                val maxValue = ruleType.maxValue

                if (rule.value < minValue || rule.value > maxValue) {
                    throw IllegalArgumentException("Rule value out of range")
                }
            }
        }
    }

    fun assureStrategyBelongsToUser(strategy: FifaStrategy, user: User) {
        if (strategy.user?.id != user.id) throw NotAllowedException("Strategy does not belong to user")
    }

    fun checkIfUserCanCreate(user: User) {
        val count = fifaStrategyRepositoryPort.countByUser(user.id)
        if (count >= globalMaxStrategies) throw NotAllowedException("User has reached the maximum number of strategies")
    }

    fun canUserChangeStatus(user: User, status: FifaStrategyStatus) {
        when (status) {
            FifaStrategyStatus.ACTIVE -> checkIfUserCanActivate(user)
            FifaStrategyStatus.PAPER_BET -> checkIfUserCanPaperBet(user)
            else -> {}
        }
    }

    private fun checkIfUserCanActivate(user: User) {
        val errorMessage = "User has reached the maximum number of active strategies"
        val count = fifaStrategyRepositoryPort.countByUserAndStatus(user.id, FifaStrategyStatus.ACTIVE)
        val userFeatures = subscriptionService.listUserFeatures(user)

        val maxActiveStrategies = userFeatures[FeatureTypes.ACTIVE_STRATEGY.identifier]
            ?: throw NotAllowedException(errorMessage)

        if (count >= maxActiveStrategies) {
            throw NotAllowedException(errorMessage)
        }
    }

    private fun checkIfUserCanPaperBet(user: User) {
        val errorMessage = "User has reached the maximum number of paper bet strategies"
        val count = fifaStrategyRepositoryPort.countByUserAndStatus(user.id, FifaStrategyStatus.PAPER_BET)
        val userFeatures = subscriptionService.listUserFeatures(user)

        val maxActiveStrategies = userFeatures[FeatureTypes.TEST_STRATEGY.identifier]
            ?: throw NotAllowedException(errorMessage)

        if (count >= maxActiveStrategies) {
            throw NotAllowedException(errorMessage)
        }
    }
}