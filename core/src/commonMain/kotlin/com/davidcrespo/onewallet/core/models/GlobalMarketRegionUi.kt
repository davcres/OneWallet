package com.davidcrespo.onewallet.core.models

import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.flag_ar
import com.davidcrespo.onewallet.core.generated.resources.flag_au
import com.davidcrespo.onewallet.core.generated.resources.flag_be
import com.davidcrespo.onewallet.core.generated.resources.flag_br
import com.davidcrespo.onewallet.core.generated.resources.flag_ca
import com.davidcrespo.onewallet.core.generated.resources.flag_ch
import com.davidcrespo.onewallet.core.generated.resources.flag_cn
import com.davidcrespo.onewallet.core.generated.resources.flag_co
import com.davidcrespo.onewallet.core.generated.resources.flag_de
import com.davidcrespo.onewallet.core.generated.resources.flag_es
import com.davidcrespo.onewallet.core.generated.resources.flag_fr
import com.davidcrespo.onewallet.core.generated.resources.flag_gb
import com.davidcrespo.onewallet.core.generated.resources.flag_gr
import com.davidcrespo.onewallet.core.generated.resources.flag_hk
import com.davidcrespo.onewallet.core.generated.resources.flag_il
import com.davidcrespo.onewallet.core.generated.resources.flag_in
import com.davidcrespo.onewallet.core.generated.resources.flag_it
import com.davidcrespo.onewallet.core.generated.resources.flag_jp
import com.davidcrespo.onewallet.core.generated.resources.flag_mx
import com.davidcrespo.onewallet.core.generated.resources.flag_nl
import com.davidcrespo.onewallet.core.generated.resources.flag_pe
import com.davidcrespo.onewallet.core.generated.resources.flag_pt
import com.davidcrespo.onewallet.core.generated.resources.flag_sw
import com.davidcrespo.onewallet.core.generated.resources.flag_uae
import com.davidcrespo.onewallet.core.generated.resources.flag_us
import com.davidcrespo.onewallet.core.generated.resources.flag_za
import com.davidcrespo.onewallet.core.generated.resources.ic_globe
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import org.jetbrains.compose.resources.DrawableResource

val GlobalMarketRegion.flagResource: DrawableResource
    get() = when (this) {
        GlobalMarketRegion.ARGENTINA -> Res.drawable.flag_ar
        GlobalMarketRegion.AUSTRIA -> Res.drawable.flag_au
        GlobalMarketRegion.BELGIUM -> Res.drawable.flag_be
        GlobalMarketRegion.BRAZIL -> Res.drawable.flag_br
        GlobalMarketRegion.CANADA -> Res.drawable.flag_ca
        GlobalMarketRegion.CHILE -> Res.drawable.flag_ch
        GlobalMarketRegion.CHINA -> Res.drawable.flag_cn
        GlobalMarketRegion.COLOMBIA -> Res.drawable.flag_co
        GlobalMarketRegion.FRANCE -> Res.drawable.flag_fr
        GlobalMarketRegion.GERMANY -> Res.drawable.flag_de
        GlobalMarketRegion.GREECE -> Res.drawable.flag_gr
        GlobalMarketRegion.HONG_KONG -> Res.drawable.flag_hk
        GlobalMarketRegion.INDIA -> Res.drawable.flag_in
        GlobalMarketRegion.ISRAEL -> Res.drawable.flag_il
        GlobalMarketRegion.ITALY -> Res.drawable.flag_it
        GlobalMarketRegion.JAPAN -> Res.drawable.flag_jp
        GlobalMarketRegion.MEXICO -> Res.drawable.flag_mx
        GlobalMarketRegion.NETHERLANDS -> Res.drawable.flag_nl
        GlobalMarketRegion.PERU -> Res.drawable.flag_pe
        GlobalMarketRegion.PORTUGAL -> Res.drawable.flag_pt
        GlobalMarketRegion.SOUTH_AFRICA -> Res.drawable.flag_za
        GlobalMarketRegion.SPAIN -> Res.drawable.flag_es
        GlobalMarketRegion.SWITZERLAND -> Res.drawable.flag_sw
        GlobalMarketRegion.UAE -> Res.drawable.flag_uae
        GlobalMarketRegion.UNITED_KINGDOM -> Res.drawable.flag_gb
        GlobalMarketRegion.UNITED_STATES -> Res.drawable.flag_us
        GlobalMarketRegion.GLOBAL -> Res.drawable.ic_globe
    }
