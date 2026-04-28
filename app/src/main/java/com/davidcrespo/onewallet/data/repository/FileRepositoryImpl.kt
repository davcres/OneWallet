package com.davidcrespo.onewallet.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.repository.FileRepository
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter

class FileRepositoryImpl(
    private val context: Context,
    private val dispatcher: DispatcherProvider
) : FileRepository {

    override suspend fun saveToDownloads(fileName: String, content: String): Result<Unit> = withContext(dispatcher.io) {
        runCatching {
            val contentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("Failed to create MediaStore entry")

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(content)
                }
            } ?: throw Exception("Failed to open output stream")
        }
    }

    override suspend fun readFromUri(uriString: String): Result<String> = withContext(dispatcher.io) {
        runCatching {
            val uri = uriString.toUri()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: throw Exception("Failed to open input stream")
        }
    }
}
