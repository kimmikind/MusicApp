package com.example.musicapp.data.local

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalTRepository(private val context: Context) {

    /**
     * Получает все локальные треки из хранилища устройства.
     */
    suspend fun getAllTracks(): List<LocalTrack> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<LocalTrack>()
        val contentResolver: ContentResolver = context.contentResolver

        // URI для доступа к аудиофайлам на устройстве
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        // Поля, которые мы хотим получить для каждого трека
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA // Путь к файлу
        )

        // Условие для фильтрации только музыкальных файлов
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        // Сортировка по названию трека
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        var cursor: Cursor? = null
        try {
            // Запрос к ContentResolver
            cursor = contentResolver.query(
                uri,
                projection,
                selection,
                null,
                sortOrder
            )

            // Обработка результата
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn)
                    val artist = it.getString(artistColumn)
                    val filePath = it.getString(dataColumn)
                    // Получаем обложку трека
                    val coverBitmap = getTrackCover(filePath)
                    val coverPath = if (coverBitmap != null) {
                        // Сохраняем обложку в кэш или временное хранилище и возвращаем путь
                        saveCoverToCache(coverBitmap, id.toString())
                    } else {
                        null
                    }


                    // Создаем объект LocalTrack и добавляем его в список
                    tracks.add(
                        LocalTrack(
                            id = id,
                            title = title,
                            artist = artist,
                            coverPath = coverPath, // Обложка не доступна через MediaStore
                            filePath = filePath
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return@withContext tracks
    }

    /**
     * Ищет треки по запросу.
     */
    suspend fun searchTracks(query: String): List<LocalTrack> = withContext(Dispatchers.IO) {
        val allTracks = getAllTracks()
        return@withContext allTracks.filter { track ->
            track.title.contains(query, ignoreCase = true) ||
                    track.artist.contains(query, ignoreCase = true)
        }
    }

    fun getTrackCover(filePath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val art = retriever.embeddedPicture
            if (art != null) {
                BitmapFactory.decodeByteArray(art, 0, art.size)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    private fun saveCoverToCache(bitmap: Bitmap, fileName: String): String {
        // Сохраняем обложку в кэш и возвращаем путь к файлу
        val file = File(context.cacheDir, "$fileName.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return file.absolutePath
    }
}