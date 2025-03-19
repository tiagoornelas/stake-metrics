package net.stakemetrics.integration.tippy.dto
import com.google.gson.annotations.SerializedName
import java.util.Date

import java.util.UUID


class TippyDTO {

    data class TippyChannelResponse(
        val id: UUID,
        val name: String,
        @SerializedName("is_admin")
        val isAdmin: Boolean,
    )

    data class IntegrationResponse(
        val channel: TippyChannelResponse,
        @SerializedName("expires_at")
        val expiresAt: Date
    )

    data class AutoBetRequestWithIntegrationInfo(
        val integrationId: String,
        val selectionId: UUID
    )

    data class AutoBetRequest(
        @SerializedName("selection_id")
        val selectionId: UUID
    )

    data class Response(
        @SerializedName("bookmaker_id")
        val bookmakerId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        val events: List<EventResponse>,
        val id: UUID,
        val name: String,
        val `object`: String,
        @SerializedName("page_id")
        val pageId: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )

    data class EventResponse(
        val `object`: String,
        val id: UUID,
        @SerializedName("fixture_id")
        val fixtureId: String,
        val name: String,
        val home: String,
        val away: String,
        @SerializedName("event_date")
        val eventDate: String,
        @SerializedName("is_live")
        val isLive: Boolean,
        @SerializedName("is_active")
        val isActive: Boolean,
        @SerializedName("sport_type")
        val sportType: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        val score: String,
        @SerializedName("bookmaker_id")
        val bookmakerId: Int,
        @SerializedName("live_period")
        val livePeriod: Int,
        @SerializedName("live_time")
        val liveTime: String,
        val markets: List<Market>
    )

    data class Market(
        val `object`: String,
        @SerializedName("bookmaker_id")
        val bookmakerId: Int,
        val name: String,
        val selections: List<Selection>
    )

    data class Selection(
        val `object`: String,
        val id: UUID,
        @SerializedName("fixture_id")
        val fixtureId: String,
        @SerializedName("participant_id")
        val participantId: String,
        val odds: Double,
        @SerializedName("odds_fraction")
        val oddsFraction: String,
        val header: String,
        val name: String,
        val metadata: String,
        @SerializedName("is_live")
        val isLive: Boolean,
        @SerializedName("is_suspended")
        val isSuspended: Boolean,
        @SerializedName("is_active")
        val isActive: Boolean,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("market_name")
        val marketName: String,
        @SerializedName("event_id")
        val eventId: String,
        @SerializedName("bookmaker_id")
        val bookmakerId: Int
    )
}
