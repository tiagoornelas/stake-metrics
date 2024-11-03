package net.stakemetrics.application.entities.exceptions

import net.stakemetrics.application.entities.FifaMatch

class FifaBetOnStartedMatchException(fifaMatch: FifaMatch) :
    RuntimeException("Match ${fifaMatch.integrationId} has already started at ${fifaMatch.time}, not betting on it. Status: ${fifaMatch.status}")