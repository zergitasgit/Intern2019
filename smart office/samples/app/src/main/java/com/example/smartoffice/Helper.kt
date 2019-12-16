package com.example.smartoffice

import android.R.attr
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.smartoffice.`object`.Office
import com.pdftron.pdf.utils.Utils.getContentResolver
import java.io.File
import java.io.FilenameFilter


class Helper {
    companion object {
        private const val orderBy = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"

        fun getAllDocuments(context: Context):ArrayList<Office> {
            val arr = ArrayList<Office>();
            val contentResolver = context.contentResolver
            val uri: Uri = MediaStore.Files.getContentUri("external")

            val columns = arrayOf(
                MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
            )

            val extensions: MutableList<String> = ArrayList()
            extensions.add("pdf")
            extensions.add("csv")
            extensions.add("doc")
            extensions.add("docx")
            extensions.add("xls")
            extensions.add("xlsx")

            val mimes: MutableList<String> = ArrayList()
            for (ext in extensions) {
                mimes.add(MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext))
            }

            val cursor: Cursor?
            cursor = contentResolver!!.query(uri, columns, null, null, orderBy)
            if (cursor != null) {
                val mimeColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
                val pathColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                while (cursor.moveToNext()) {
                    val mimeType: String? = cursor.getString(mimeColumnIndex)
                    val filePath: String = cursor.getString(pathColumnIndex)
                    if (mimeType != null && mimes.contains(mimeType)) { // handle cursor
                        makeFile(cursor)
                        arr.add(makeFile(cursor))

                    } else { // need to check extension, because the Mime Type is null
                        val extension: String = getExtensionByPath(filePath)
                        if (extensions.contains(extension)) { // handle cursor
                            makeFile(cursor)
                            arr.add(makeFile(cursor))
                        }
                    }
                }
                cursor.close()
            }
            return arr
        }
        private fun makeFile(cursor: Cursor):Office{
            val mimeColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
            val pathColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val sizeColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
            val titleColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.TITLE)
            val nameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val fileId = cursor.getInt(pathColumnIndex)
            val fileSize = cursor.getString(sizeColumnIndex)
            val fileDisplayName = cursor.getString(nameColumnIndex)
            val fileTitle = cursor.getString(titleColumnIndex)
            val filePath = cursor.getString(pathColumnIndex)
            var mimeType = cursor.getString(mimeColumnIndex)
            var type = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            if (type == null) {
                type = getExtensionByPath(filePath)
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type)
            }
            val office = Office(filePath.substring(filePath.lastIndexOf("/")+1), getSizeAll(fileSize.toLong()),filePath,false)
            return office
        }
        private fun  getExtensionByPath(path:String):String{
            var result: String? = "%20"
            val i: Int = path.lastIndexOf('.')
            if (i > 0) {
                result = path.substring(i + 1)
            }
            return result!!
        }
        fun getFiles(dir: String): List<File>? {
            return getFiles(dir, null)
        }

        private fun getFiles(dir: String, matchRegex: String?): List<File>? {
            val file = File(dir)
            var files: Array<File?>? = null
            files = if (matchRegex != null) {
                val filter = FilenameFilter { dir, fileName -> fileName.matches(matchRegex.toRegex()) }
                file.listFiles(filter)
            } else {
                file.listFiles()
            }
            return if (files != null) listOf(*files) else null
        }
         fun getSize(file: File): String {
            var size = file.length() // Get size and convert bytes into Kb.
            var suffix = ""
            if (size >= 1024) {
                suffix = "KB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "MB";
                    size /= 1024;
                }
            }
            return size.toString() + suffix
        }

         fun createFolder() {
            val folder =
                File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "All Documents");

            if (!folder.exists()) {
                folder.mkdir();
            }

        }
        private fun getSizeAll(size:Long): String{
            var size = size
            var suffix = ""
            if (size >= 1024) {
                suffix = "KB"
                size /= 1024
                if (size >= 1024) {
                    suffix = "MB";
                    size /= 1024
                }
            }
            return size.toString() + suffix
        }
        fun convertToPx(dp: Int,context: Context): Int {
            // Get the screen's density scale
            val scale = context.resources.displayMetrics.density
            // Convert the dps to pixels, based on density scale
            return (dp * scale + 0.5f).toInt()
        }
    }

}