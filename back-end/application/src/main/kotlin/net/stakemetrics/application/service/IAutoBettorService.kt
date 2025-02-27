package net.stakemetrics.application.service

import net.stakemetrics.application.entities.dtos.FifaBetDTO

interface IAutoBettorService {
    fun bet(payload: FifaBetDTO.AutoBetRequest)
}