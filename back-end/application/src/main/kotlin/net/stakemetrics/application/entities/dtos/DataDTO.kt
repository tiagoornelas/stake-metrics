package net.stakemetrics.application.entities.dtos

import net.stakemetrics.application.entities.FifaOddSnapshot

class DataDTO {

    data class OddSnapshotResponse(
        val oddSnapshots: List<FifaOddSnapshot>
    )

}