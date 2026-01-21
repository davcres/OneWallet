package com.davidcrespo.onewallet.data.remote.etf.justEtf

import com.davidcrespo.onewallet.data.remote.etf.justEtf.models.JustEtfDetailResponse
import com.davidcrespo.onewallet.data.remote.etf.justEtf.models.JustEtfResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class JustEtfApiClient(private val client: HttpClient) {

    suspend fun getEtfDetail(isin: String, currency: String): JustEtfDetailResponse {
        return client.get(JustEtfApiConfig.GetEtfDetail.PATH) {
            parameter(JustEtfApiConfig.GetEtfDetail.ISIN, isin)
            parameter(JustEtfApiConfig.GetEtfDetail.CURRENCY, currency)
            parameter(JustEtfApiConfig.GetEtfDetail.LOCALE, "es")
        }.body<JustEtfDetailResponse>()
    }

    suspend fun getEtfPrice(isin: String, currency: String): JustEtfResponse {
        return client.get("${JustEtfApiConfig.GetEtf.PATH_PREFIX}/$isin/${JustEtfApiConfig.GetEtf.PATH_SUFIX}") {
            parameter(JustEtfApiConfig.GetEtf.CURRENCY, currency)
            parameter(JustEtfApiConfig.GetEtf.LOCALE, "es")
        }.body<JustEtfResponse>()
    }
}
