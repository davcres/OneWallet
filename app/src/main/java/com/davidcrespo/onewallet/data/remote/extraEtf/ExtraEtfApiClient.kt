package com.davidcrespo.onewallet.data.remote.extraEtf

import com.davidcrespo.onewallet.data.remote.extraEtf.models.ExtraEtfResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ExtraEtfApiClient(private val client: HttpClient) {

    suspend fun getEtfPrice(isin: String): ExtraEtfResponse {
        return client.get(ExtraEtfApiConfig.GetEtf.PATH) {
            parameter(ExtraEtfApiConfig.GetEtf.ISIN, isin)
            parameter(ExtraEtfApiConfig.GetEtf.LOCALE, "es")
        }.body<ExtraEtfResponse>()
    }
}
