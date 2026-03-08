package com.davidcrespo.onewallet.data.remote.investing

import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class InvestingApiClient(private val client: HttpClient) {

    suspend fun getFundPrice(isin: String): InvestmentDto? {
        runCatching {
            val html = client.get("${InvestingApiConfig.GetFunds.PATH}/${isin.lowercase()}") {
                /*header("User-Agent", "Mozilla/5.0 (Android) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36")
                header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,* / *;q=0.8")
                header("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")
                header("Connection", "keep-alive")
                header("Upgrade-Insecure-Requests", "1")
                header("Referer", "https://es.investing.com/")
                header("Sec-Fetch-Dest", "document")
                header("Sec-Fetch-Mode", "navigate")
                header("Sec-Fetch-Site", "same-origin")*/
            }.bodyAsText()

            return parseInvestingHtmlFund(isin, html)
        }.getOrElse {
            it.printStackTrace()
            return null
        }
    }

    private fun parseInvestingHtmlFund(isin: String, html: String): InvestmentDto {
        // 1) Nombre: <h1 ...>Fidelity ... (0P0001CJGV)</h1>
        val h1 = Regex("""<h1[^>]*>(.*?)</h1>""", RegexOption.IGNORE_CASE)
            .find(html)?.groupValues?.get(1)
            ?.replace(Regex("""\s+"""), " ")
            ?.trim()
            ?: throw IllegalStateException("No se encontró <h1> (nombre)")

        // Quitar el código final "(0P0001CJGV)" y quedarte solo con el nombre “humano”
        val name = h1.replace(Regex("""\s*\([^)]*\)\s*$"""), "").trim()

        // 2) Valor actual: pid-1210266-last" ...>10,940<
        // Capturamos pid y value para reutilizar el pid en diff
        val lastMatch = Regex(
            """pid-(\d+)-last"[^>]*>([^<]+)<""",
            RegexOption.IGNORE_CASE
        ).find(html) ?: throw IllegalStateException("No se encontró el valor actual (pid-*-last)")

        val pid = lastMatch.groupValues[1]
        val value = lastMatch.groupValues[2].trim().normalizeDouble()

        // 3) Diferencia día anterior: pid-1210266-pc" ... -0,038
        val diff = Regex(
            """pid-${pid}-pc"[^>]*>([^<]+)<""",
            RegexOption.IGNORE_CASE
        ).find(html)?.groupValues?.get(1)?.trim()?.normalizeDouble()

        // 4) Moneda: "Valores en <span class='bold'>EUR<"
        val currency = Regex(
            """Valores\s+en\s*<span[^>]*>\s*([A-Z]{3})\s*<""",
            setOf(RegexOption.IGNORE_CASE)
        ).find(html)?.groupValues?.get(1)

        return InvestmentDto(
            symbol = isin,
            name = name,
            price = value,
            previousPrice = if (diff != null) value - diff else 0.0,
            currency = if (!currency.isNullOrEmpty()) CurrencyDto(currency) else CurrencyDto(USD),
            type = InvestmentType.FUND,
            quantity = 0.0,
            year = 0,
            month = 0
        )
    }
}
