package com.davidcrespo.onewallet.core.models

import androidx.annotation.DrawableRes
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.core.R

@get:DrawableRes
val GlobalMarketRegion.flagRes: Int
    get() = when (this) {
        GlobalMarketRegion.ARGENTINA -> R.drawable.flag_ar
        GlobalMarketRegion.AUSTRIA -> R.drawable.flag_au
        GlobalMarketRegion.BELGIUM -> R.drawable.flag_be
        GlobalMarketRegion.BRAZIL -> R.drawable.flag_br
        GlobalMarketRegion.CANADA -> R.drawable.flag_ca
        GlobalMarketRegion.CHILE -> R.drawable.flag_ch
        GlobalMarketRegion.CHINA -> R.drawable.flag_cn
        GlobalMarketRegion.COLOMBIA -> R.drawable.flag_co
        GlobalMarketRegion.FRANCE -> R.drawable.flag_fr
        GlobalMarketRegion.GERMANY -> R.drawable.flag_de
        GlobalMarketRegion.GREECE -> R.drawable.flag_gr
        GlobalMarketRegion.HONG_KONG -> R.drawable.flag_hk
        GlobalMarketRegion.INDIA -> R.drawable.flag_in
        GlobalMarketRegion.ISRAEL -> R.drawable.flag_il
        GlobalMarketRegion.ITALY -> R.drawable.flag_it
        GlobalMarketRegion.JAPAN -> R.drawable.flag_jp
        GlobalMarketRegion.MEXICO -> R.drawable.flag_mx
        GlobalMarketRegion.NETHERLANDS -> R.drawable.flag_nl
        GlobalMarketRegion.PERU -> R.drawable.flag_pe
        GlobalMarketRegion.PORTUGAL -> R.drawable.flag_pt
        GlobalMarketRegion.SOUTH_AFRICA -> R.drawable.flag_za
        GlobalMarketRegion.SPAIN -> R.drawable.flag_es
        GlobalMarketRegion.SWITZERLAND -> R.drawable.flag_sw
        GlobalMarketRegion.UAE -> R.drawable.flag_uae
        GlobalMarketRegion.UNITED_KINGDOM -> R.drawable.flag_gb
        GlobalMarketRegion.UNITED_STATES -> R.drawable.flag_us
        GlobalMarketRegion.GLOBAL -> R.drawable.ic_globe
    }
