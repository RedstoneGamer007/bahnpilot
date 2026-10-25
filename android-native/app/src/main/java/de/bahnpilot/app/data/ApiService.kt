package de.bahnpilot.app.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BahnpilotApi {
    @GET("locations")
    suspend fun searchLocations(
        @Query("query") query: String,
        @Query("results") results: Int = 6,
        @Query("language") language: String = "de"
    ): List<Location>

    @GET("journeys")
    suspend fun getJourneys(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("departure") departure: String? = null,
        @Query("results") results: Int = 5,
        @Query("transfers") transfers: Int = -1,
        @Query("language") language: String = "de",
        @Query("stopovers") stopovers: Boolean = true,
        @Query("remarks") remarks: Boolean = true
    ): JourneysResponse

    @GET("journeys/{refreshToken}")
    suspend fun refreshJourney(
        @Path(value = "refreshToken", encoded = true) refreshToken: String
    ): Map<String, Any> // contains "journey"

    @GET("stops/{id}/departures")
    suspend fun getDepartures(
        @Path("id") id: String,
        @Query("duration") duration: Int = 60,
        @Query("results") results: Int = 14,
        @Query("language") language: String = "de"
    ): List<Departure>

    @GET("stops/{id}/departures")
    suspend fun getDeparturesWrapped(
        @Path("id") id: String,
        @Query("duration") duration: Int = 60,
        @Query("results") results: Int = 14,
        @Query("language") language: String = "de"
    ): DeparturesResponse

}
