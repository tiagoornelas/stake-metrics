package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import net.stakemetrics.application.entities.enums.SubscriptionStatus
import net.stakemetrics.application.repositories.IFifaStrategyRepository
import net.stakemetrics.application.repositories.IUserRepository
import net.stakemetrics.application.service.ISubscriptionService
import net.stakemetrics.application.utils.Logger
import org.springframework.stereotype.Service

@Service
class UserSubscriptionChecker(
    private val userRepository: IUserRepository,
    private val subscriptionService: ISubscriptionService,
    private val fifaStrategyRepository: IFifaStrategyRepository,
    private val logger: Logger
) {

    fun check() {
        val users = userRepository.findAll()
        users.forEach { user ->
            val status = subscriptionService.checkUserSubscriptionStatus(user)

            if (status == SubscriptionStatus.INACTIVE) {
                val activeStrategies = fifaStrategyRepository.findActiveByUser(user)
                activeStrategies.forEach { strategy ->
                    logger.log("Strategy ${strategy.id} is active and user ${user.id} is inactive. Deactivating strategy.")
                    strategy.status = FifaStrategyStatus.INACTIVE
                    fifaStrategyRepository.save(strategy)
                }
            }
        }
    }

}