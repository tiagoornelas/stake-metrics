package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.entities.enums.FeatureTypes
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import net.stakemetrics.application.entities.exceptions.NotAllowedException
import net.stakemetrics.application.service.ISubscriptionService
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.service.FifaStrategyService
import org.springframework.stereotype.Service

@Service
class FifaStrategyResourceValidator(
    private val subscriptionService: ISubscriptionService,
    private val fifaStrategyService: FifaStrategyService
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
        val count = fifaStrategyService.countByUser(user)
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
        val count = fifaStrategyService.countByUserAndStatus(user, FifaStrategyStatus.ACTIVE)
        val userFeatures = subscriptionService.listUserFeatures(user)

        val maxActiveStrategies = userFeatures[FeatureTypes.ACTIVE_STRATEGY.identifier]
            ?: throw NotAllowedException(errorMessage)

        if (count >= maxActiveStrategies) {
            throw NotAllowedException(errorMessage)
        }
    }

    private fun checkIfUserCanPaperBet(user: User) {
        val errorMessage = "User has reached the maximum number of paper bet strategies"
        val count = fifaStrategyService.countByUserAndStatus(user, FifaStrategyStatus.PAPER_BET)
        val userFeatures = subscriptionService.listUserFeatures(user)

        val maxActiveStrategies = userFeatures[FeatureTypes.TEST_STRATEGY.identifier]
            ?: throw NotAllowedException(errorMessage)

        if (count >= maxActiveStrategies) {
            throw NotAllowedException(errorMessage)
        }
    }
}