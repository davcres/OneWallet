package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.repository.FileRepository
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.withContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.stringWithContentsOfURL
import platform.Foundation.writeToFile

class IosFileRepository(
    private val dispatcher: DispatcherProvider
) : FileRepository {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun saveToDownloads(fileName: String, content: String): Result<Unit> = withContext(dispatcher.io) {
        runCatching {
            val fileManager = NSFileManager.defaultManager
            val documentDirectory = fileManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null
            )?.path ?: error("Failed to get Documents directory")

            val filePath = "$documentDirectory/$fileName"
            val nsString = NSString.create(string = content)
            val success = nsString.writeToFile(
                path = filePath,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = null
            )
            if (!success) {
                error("Failed to write file to $filePath")
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun readFromUri(uriString: String): Result<String> = withContext(dispatcher.io) {
        runCatching {
            val url = NSURL.URLWithString(uriString)
            if (url != null) {
                NSString.stringWithContentsOfURL(url, NSUTF8StringEncoding, null)
                    ?: NSString.stringWithContentsOfFile(uriString, NSUTF8StringEncoding, null)
                    ?: error("Failed to read file from $uriString")
            } else {
                NSString.stringWithContentsOfFile(uriString, NSUTF8StringEncoding, null)
                    ?: error("Failed to read file from $uriString")
            }
        }
    }
}
