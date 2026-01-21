package com.davidcrespo.onewallet.data.remote.quefondos

import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.core.extensions.round
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

class QueFondosApiClient(private val client: HttpClient) {

    suspend fun getFundPrice(isin: String, type: InvestmentType): InvestmentDto? {
        runCatching {
            val html = client.get {
                /*header("User-Agent", "Mozilla/5.0 (Android) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36")
                header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,* / *;q=0.8")
                header("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")
                header("Connection", "keep-alive")
                header("Upgrade-Insecure-Requests", "1")
                header("Referer", "https://es.investing.com/")
                header("Sec-Fetch-Dest", "document")
                header("Sec-Fetch-Mode", "navigate")
                header("Sec-Fetch-Site", "same-origin")*/
                parameter(QueFondosApiConfig.GetAsset.ISIN, isin)
            }.bodyAsText()
            return parseQueFondosHtmlFund(isin, type, html)
        }.getOrElse {
            return null
        }
    }

    private fun parseQueFondosHtmlFund(
        isin: String,
        type: InvestmentType,
        html: String
    ): InvestmentDto {

        // 1) Nombre
        val name = extractH2InsideInforme(html)
            ?.replace(Regex("""\s+"""), " ")
            ?.trim()
            ?: throw IllegalStateException("No se encontró el nombre del fondo")

        // 2) Valor liquidativo
        val priceAndCurrencyText = extractFloatRight(
            html,
            "Valor\\s+liquidativo:"
        )?.split(" ") ?: throw IllegalStateException("No se encontró el valor liquidativo")

        val value = priceAndCurrencyText.firstOrNull()?.normalizeDouble() ?: 0.0

        // 3) Variación 1 día (%)
        val diffPercentText = extractPercent(html, "1 d(?:&iacute;|í)a:")

        val diff = diffPercentText
            ?.replace("%", "")
            ?.normalizeDouble()
            ?.let { value * it / 100.0 }
            ?.round(2)

        // 4) Divisa
        val currencyText = priceAndCurrencyText.lastOrNull()

        val currency = if (!currencyText.isNullOrEmpty())
            Currency.valueOf(currencyText)
        else
            Currency.EUR

        return InvestmentDto(
            symbol = isin,
            name = name,
            price = value,
            previousPrice = if (diff != null) value - diff else 0.0,
            currency = currency,
            type = type,
            quantity = 0.0,
            year = 0,
            month = 0
        )
    }

    private fun extractH2InsideInforme(html: String): String? =
        Regex(
            """<div[^>]*class="[^"]*informe[^"]*"[^>]*>[\s\S]*?<h2[^>]*>(.*?)</h2>""",
            RegexOption.IGNORE_CASE
        ).find(html)?.groupValues?.get(1)

    private fun extractFloatRight(html: String, labelRegex: String): String? =
        Regex(
            """<span[^>]*class="[^"]*floatleft[^"]*"[^>]*>\s*$labelRegex\s*</span>\s*<span[^>]*class="[^"]*floatright[^"]*"[^>]*>\s*([^<]+)""",
            RegexOption.IGNORE_CASE
        ).find(html)?.groupValues?.get(1)

    private fun extractPercent(html: String, label: String): String? {
        return Regex(
            pattern = """<span[^>]*class="[^"]*floatleft[^"]*"[^>]*>.*?$label\s*</span>\s*<span[^>]*class="[^"]*floatright[^"]*"[^>]*>.*?([\d.,]+)%""",
            options = setOf(
                RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL
            )
        ).find(html)?.groupValues?.get(1)
    }
}
