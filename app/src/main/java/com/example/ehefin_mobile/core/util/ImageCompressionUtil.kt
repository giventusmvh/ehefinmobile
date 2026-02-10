package com.example.ehefin_mobile.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageCompressionUtil {
    private const val MAX_FILE_SIZE = 1 * 1024 * 1024 // 1MB

    fun compressImage(context: Context, imageFile: File): File {
        // If file is already smaller than 1MB, return original
        if (imageFile.length() <= MAX_FILE_SIZE) {
            return imageFile
        }

        try {
            // Decode image file to Bitmap
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return imageFile
            
            var compressQuality = 100
            var streamLength: Int
            val stream = ByteArrayOutputStream()

            // First attempt with 100% quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, stream)
            streamLength = stream.size()

            // Loop to reduce quality if size is greater than 3MB
            while (streamLength > MAX_FILE_SIZE && compressQuality > 5) {
                stream.reset() // Clear the stream
                compressQuality -= 5
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, stream)
                streamLength = stream.size()
            }

            // Save compressed bitmap to a new file
            val compressedFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
            val fos = FileOutputStream(compressedFile)
            fos.write(stream.toByteArray())
            fos.flush()
            fos.close()

            android.util.Log.d("ImageCompression", "Compressed from ${imageFile.length()} to ${compressedFile.length()}")
            return compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            // Return original file if compression fails
            return imageFile
        }
    }
}