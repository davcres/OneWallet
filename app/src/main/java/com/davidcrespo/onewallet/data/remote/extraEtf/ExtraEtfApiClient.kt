package com.davidcrespo.onewallet.data.remote.extraEtf

import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class ExtraEtfApiClient(private val client: HttpClient) {

    suspend fun getEtfPrice(isin: String): InvestmentDto? {
        runCatching {
            val html = client.get("${ExtraEtfApiConfig.GetEtf.PATH}/$isin") {
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
            return parseExtraEtfHtmlEtf(isin, html)
        }.getOrElse {
            return null
        }
    }

    private fun parseExtraEtfHtmlEtf(isin: String, html: String): InvestmentDto {
        // 1) Nombre
        val name = Regex(
            """<div[^>]*class=["'][^"']*investment-name-wrapper[^"']*["'][^>]*>[\s\S]*?<h1[^>]*>(.*?)</h1>""",
            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        ).find(html)?.groupValues?.get(1)
            ?.replace(Regex("""\s+"""), " ")
            ?.trim()
            ?: throw IllegalStateException("No se encontró el nombre (investment-name-wrapper h1)")

        // 2) Precio (ej: "113,12&nbsp;€")
        val priceTextFull = Regex(
            """class=["'][^"']*real-time-course-wrapper[^"']*["'][\s\S]*?<span[^>]*class=["'][^"']*ng-star-inserted[^"']*["'][^>]*>\s*([^<]+?)\s*</span>""",
            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        ).find(html)?.groupValues?.get(1)
            ?.replace(Regex("""\s+"""), " ")
            ?.trim()
            ?: throw IllegalStateException("No se encontró el texto del precio")

        // priceTextFull = "113,17&nbsp;€" (o "113,17 €" según venga)
        val cleaned = priceTextFull.replace("&nbsp;", " ").trim()
        val parts = cleaned.split(Regex("""\s+"""))

        val priceText = parts.firstOrNull() ?: throw IllegalStateException("Precio vacío: '$cleaned'")
        val symbol = parts.getOrNull(1) ?: throw IllegalStateException("Símbolo moneda no encontrado: '$cleaned'")

        val price = priceText.normalizeDouble()

        val currency = if (symbol.contains("$", ignoreCase = true) || symbol.contains("US", ignoreCase = true)) {
            Currency.USD
        } else {
            Currency.EUR
        }

        // 3) Delta diario (importe): "+0,41 €" / "-1,45 €"
        val diff = Regex(
            """tp-change-label[^>]*>[\s\S]*?([+-]\s*[0-9\.,]+)\s*(?:&nbsp;|\s)*[€$£][\s\S]*?</span>""",
            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        ).find(html)?.groupValues?.get(1)
            ?.replace(" ", "")
            ?.trim()
            ?.normalizeDouble()

        val previousPrice = if (diff != null) price - diff else 0.0

        return InvestmentDto(
            symbol = isin,
            name = name,
            price = price,
            previousPrice = previousPrice,
            currency = currency,
            type = InvestmentType.ETF,
            quantity = 0.0,
            year = 0,
            month = 0
        )
    }
}
