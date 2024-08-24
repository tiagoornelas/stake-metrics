package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.workers.tipsters.factory.FifaBetCloser
import org.springframework.stereotype.Service

@Service
class FifaGoalLineBetCloser(private val fifaBetRepository: IFifaBetRepository) : FifaBetCloser {
    override fun closeBet(fifaBet: FifaBet) {
        val candidate = fifaBet.line
        val match = fifaBet.match ?: return
        val handicap = fifaBet.handicap ?: return
        val totalGoals = match.totalGoalsAtFullTime?.toDouble() ?: return

        val updatedFifaBet = fifaBet.copy()

        when (candidate) {
            FifaMarketBetCandidates.OVER -> {
                when {
                    totalGoals > handicap -> {
                        if (totalGoals == handicap + 0.25) {
                            updatedFifaBet.status = BetStatusTypes.HALF_WON
                            updatedFifaBet.profit = (updatedFifaBet.odds - 1.0) / 2
                        } else {
                            updatedFifaBet.status = BetStatusTypes.WON
                            updatedFifaBet.profit = updatedFifaBet.odds - 1.0
                        }
                    }

                    totalGoals < handicap -> {
                        if (totalGoals == handicap - 0.25) {
                            updatedFifaBet.status = BetStatusTypes.HALF_LOST
                            updatedFifaBet.profit = -0.5
                        } else {
                            updatedFifaBet.status = BetStatusTypes.LOST
                            updatedFifaBet.profit = -1.0
                        }
                    }

                    totalGoals == handicap -> {
                        updatedFifaBet.status = BetStatusTypes.VOID
                        updatedFifaBet.profit = 0.0
                    }

                }
            }

            FifaMarketBetCandidates.UNDER -> {
                when {
                    totalGoals < handicap -> {
                        if (totalGoals == handicap - 0.25) {
                            updatedFifaBet.status = BetStatusTypes.HALF_WON
                            updatedFifaBet.profit = (updatedFifaBet.odds - 1.0) / 2
                        } else {
                            updatedFifaBet.status = BetStatusTypes.WON
                            updatedFifaBet.profit = updatedFifaBet.odds - 1.0
                        }
                    }

                    totalGoals > handicap -> {
                        if (totalGoals == handicap + 0.25) {
                            updatedFifaBet.status = BetStatusTypes.HALF_LOST
                            updatedFifaBet.profit = -0.5
                        } else {
                            updatedFifaBet.status = BetStatusTypes.LOST
                            updatedFifaBet.profit = -1.0
                        }
                    }

                    totalGoals == handicap -> {
                        updatedFifaBet.status = BetStatusTypes.VOID
                        updatedFifaBet.profit = 0.0
                    }

                }
            }

            else -> return
        }

        fifaBetRepository.save(updatedFifaBet)
    }

}