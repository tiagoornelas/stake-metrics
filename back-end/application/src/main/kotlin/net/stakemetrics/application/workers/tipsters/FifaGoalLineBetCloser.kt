package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.workers.tipsters.factory.FifaBetCloser
import org.springframework.stereotype.Service

@Service
class FifaGoalLineBetCloser(private val fifaBetRepository: IFifaBetRepository) : FifaBetCloser {
    override fun closeBet(fifaBet: FifaBet): FifaBet {
        val candidate = fifaBet.line
        val match = fifaBet.match!!
        val handicap = fifaBet.handicap
        val totalGoals = match.totalGoalsAtFullTime?.toDouble()

        when (candidate) {
            FifaMarketBetCandidates.OVER -> {
                when {
                    totalGoals!! > handicap!! -> {
                        if (totalGoals == handicap + 0.25) {
                            fifaBet.status = BetStatusTypes.HALF_WON
                            fifaBet.profit = (fifaBet.odds - 1.0) / 2
                        } else {
                            fifaBet.status = BetStatusTypes.WON
                            fifaBet.profit = fifaBet.odds - 1.0
                        }
                    }

                    totalGoals < handicap -> {
                        if (totalGoals == handicap - 0.25) {
                            fifaBet.status = BetStatusTypes.HALF_LOST
                            fifaBet.profit = -0.5
                        } else {
                            fifaBet.status = BetStatusTypes.LOST
                            fifaBet.profit = -1.0
                        }
                    }

                    totalGoals == handicap -> {
                        fifaBet.status = BetStatusTypes.VOID
                        fifaBet.profit = 0.0
                    }

                }
            }

            FifaMarketBetCandidates.UNDER -> {
                when {
                    totalGoals!! < handicap!! -> {
                        if (totalGoals == handicap - 0.25) {
                            fifaBet.status = BetStatusTypes.HALF_WON
                            fifaBet.profit = (fifaBet.odds - 1.0) / 2
                        } else {
                            fifaBet.status = BetStatusTypes.WON
                            fifaBet.profit = fifaBet.odds - 1.0
                        }
                    }

                    totalGoals > handicap -> {
                        if (totalGoals == handicap + 0.25) {
                            fifaBet.status = BetStatusTypes.HALF_LOST
                            fifaBet.profit = -0.5
                        } else {
                            fifaBet.status = BetStatusTypes.LOST
                            fifaBet.profit = -1.0
                        }
                    }

                    totalGoals == handicap -> {
                        fifaBet.status = BetStatusTypes.VOID
                        fifaBet.profit = 0.0
                    }

                }
            }

            else -> throw IllegalArgumentException("Invalid candidate for goal line bet: $candidate")
        }

        fifaBetRepository.save(fifaBet)
        return fifaBet
    }

}