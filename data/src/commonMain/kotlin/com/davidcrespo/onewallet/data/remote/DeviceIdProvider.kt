package com.davidcrespo.onewallet.data.remote

fun interface DeviceIdProvider {
    fun getDeviceId(): String
}
