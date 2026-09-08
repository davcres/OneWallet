package com.davidcrespo.onewallet.domain.repository

interface FileRepository {
    suspend fun saveToDownloads(fileName: String, content: String): Result<Unit>
    suspend fun readFromUri(uriString: String): Result<String>
}
