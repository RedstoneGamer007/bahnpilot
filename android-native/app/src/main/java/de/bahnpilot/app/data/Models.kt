package de.bahnpilot.app.data

import com.google.gson.annotations.SerializedName

// transport.rest / v6.db.transport.rest models (FPTF)
data class Location(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String? = null
)

data class JourneysResponse(
    @SerializedName("journeys") val journeys: List<Journey>,
    @SerializedName("earlierRef") val earlierRef: String? = null,
    @SerializedName("laterRef") val laterRef: String? = null
)

data class Journey(
    @SerializedName("type") val type: String = "journey",
    @SerializedName("legs") val legs: List<Leg>,
    @SerializedName("refreshToken") val refreshToken: String? = null,
    @SerializedName("price") val price: Price? = null
)

data class Leg(
    @SerializedName("origin") val origin: Stop? = null,
    @SerializedName("destination") val destination: Stop? = null,
    @SerializedName("departure") val departure: String? = null,
    @SerializedName("plannedDeparture") val plannedDeparture: String? = null,
    @SerializedName("arrival") val arrival: String? = null,
    @SerializedName("plannedArrival") val plannedArrival: String? = null,
    @SerializedName("departureDelay") val departureDelay: Int? = null,
    @SerializedName("arrivalDelay") val arrivalDelay: Int? = null,
    @SerializedName("departurePlatform") val departurePlatform: String? = null,
    @SerializedName("plannedDeparturePlatform") val plannedDeparturePlatform: String? = null,
    @SerializedName("arrivalPlatform") val arrivalPlatform: String? = null,
    @SerializedName("plannedArrivalPlatform") val plannedArrivalPlatform: String? = null,
    @SerializedName("line") val line: Line? = null,
    @SerializedName("direction") val direction: String? = null,
    @SerializedName("walking") val walking: Boolean? = false,
    @SerializedName("distance") val distance: Int? = null,
    @SerializedName("cancelled") val cancelled: Boolean? = false,
    @SerializedName("stopovers") val stopovers: List<Stopover>? = null,
    @SerializedName("remarks") val remarks: List<Remark>? = null,
    @SerializedName("tripId") val tripId: String? = null
)

data class Stop(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("type") val type: String? = null
)

data class Line(
    @SerializedName("name") val name: String? = null,
    @SerializedName("product") val product: String? = null,
    @SerializedName("productName") val productName: String? = null,
    @SerializedName("mode") val mode: String? = null
)

data class Stopover(
    @SerializedName("stop") val stop: Stop? = null,
    @SerializedName("arrival") val arrival: String? = null,
    @SerializedName("plannedArrival") val plannedArrival: String? = null,
    @SerializedName("arrivalDelay") val arrivalDelay: Int? = null,
    @SerializedName("departure") val departure: String? = null,
    @SerializedName("plannedDeparture") val plannedDeparture: String? = null,
    @SerializedName("departureDelay") val departureDelay: Int? = null,
    @SerializedName("cancelled") val cancelled: Boolean? = null
)

data class Remark(
    @SerializedName("text") val text: String? = null,
    @SerializedName("type") val type: String? = null
)

data class Price(
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("currency") val currency: String? = null
)

// Departures
data class Departure(
    @SerializedName("tripId") val tripId: String? = null,
    @SerializedName("when") val whenTime: String? = null,
    @SerializedName("plannedWhen") val plannedWhen: String? = null,
    @SerializedName("delay") val delay: Int? = null,
    @SerializedName("platform") val platform: String? = null,
    @SerializedName("plannedPlatform") val plannedPlatform: String? = null,
    @SerializedName("line") val line: Line? = null,
    @SerializedName("direction") val direction: String? = null,
    @SerializedName("destination") val destination: Stop? = null,
    @SerializedName("provenance") val provenance: String? = null,
    @SerializedName("cancelled") val cancelled: Boolean? = false,
    @SerializedName("remarks") val remarks: List<Remark>? = null
)

data class DeparturesResponse(
    @SerializedName("departures") val departures: List<Departure>? = null
)
