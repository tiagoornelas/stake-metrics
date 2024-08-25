package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.FifaBet

interface FifaBetCloser {
    fun closeBet(fifaBet: FifaBet): FifaBet
}