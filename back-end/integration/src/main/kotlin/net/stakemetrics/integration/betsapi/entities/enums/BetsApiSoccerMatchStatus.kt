import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes

enum class BetsApiSoccerMatchStatus {
    NOT_STARTED,
    IN_PLAY,
    TO_BE_FIXED,
    ENDED,
    POSTPONED,
    CANCELLED,
    WALKOVER,
    INTERRUPTED,
    ABANDONED,
    RETIRED,
    SUSPENDED,
    DECIDED_BY_FA,
    REMOVED;

    fun toFifaMatchStatusType(): FifaMatchStatusTypes {
        return when (this) {
            NOT_STARTED -> FifaMatchStatusTypes.NOT_STARTED
            IN_PLAY -> FifaMatchStatusTypes.IN_PLAY
            TO_BE_FIXED -> FifaMatchStatusTypes.TO_BE_FIXED
            ENDED -> FifaMatchStatusTypes.ENDED
            POSTPONED -> FifaMatchStatusTypes.POSTPONED
            CANCELLED -> FifaMatchStatusTypes.CANCELLED
            WALKOVER -> FifaMatchStatusTypes.WALKOVER
            INTERRUPTED -> FifaMatchStatusTypes.INTERRUPTED
            ABANDONED -> FifaMatchStatusTypes.ABANDONED
            RETIRED -> FifaMatchStatusTypes.RETIRED
            SUSPENDED -> FifaMatchStatusTypes.SUSPENDED
            DECIDED_BY_FA -> FifaMatchStatusTypes.DECIDED_BY_FA
            REMOVED -> FifaMatchStatusTypes.REMOVED
        }
    }
}