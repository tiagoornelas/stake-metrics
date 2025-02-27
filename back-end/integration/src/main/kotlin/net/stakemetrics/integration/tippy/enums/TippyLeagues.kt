package net.stakemetrics.integration.tippy.enums

import net.stakemetrics.application.entities.enums.FifaDefaultLeagues
import java.util.UUID

enum class TippyLeagues(val applicationType: FifaDefaultLeagues, val integrationId: UUID) {
    BATTLE(FifaDefaultLeagues.BATTLE, UUID.fromString("01897ee3-1ad9-3a9b-e184-0d5ec4a2b800")),
    H2H(FifaDefaultLeagues.H2H, UUID.fromString("01901053-153f-eaad-201d-107cc3d56d3b")),
    GT(FifaDefaultLeagues.GT, UUID.fromString("01897ee3-2a8a-7705-dcaa-a276352815c9")),
    VOLTA(FifaDefaultLeagues.VOLTA, UUID.fromString("0194c0fb-11d5-25fa-1be4-570ebdd35567"))
}