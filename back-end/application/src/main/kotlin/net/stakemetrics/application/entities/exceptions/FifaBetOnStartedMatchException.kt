package net.stakemetrics.application.entities.exceptions

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaOddSnapshot
import java.util.*

class FifaBetOnStartedMatchException(fifaMatch: FifaMatch, oddSnapshot: FifaOddSnapshot) :
    RuntimeException("Match ${fifaMatch.integrationId} has already started at ${fifaMatch.time}, not betting on it. Odd snapshot: ${oddSnapshot.id}, now at ${Date()}")