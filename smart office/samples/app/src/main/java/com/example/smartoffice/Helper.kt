package com.example.smartoffice

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings.Global.getString
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import com.example.smartoffice.`object`.Office
import com.pdftron.demo.app.SimpleReaderActivity
import com.pdftron.pdf.config.PDFViewCtrlConfig
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.utils.Utils
import java.io.File
import java.io.FilenameFilter


class Helper {
    companion object {
        private const val orderBy = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"

        fun getAllDocuments(context: Context):ArrayList<Office> {

            val sharedPreferences = context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
            val path = sharedPreferences!!.getString("title", "hieu.pdf").substring(
                0,
                sharedPreferences!!.getString("title", "hieu.pdf").lastIndexOf(".")
            )
            val file =
                File(Environment.getExternalStorageDirectory().absolutePath + "/" + path + ".pdf")
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val  mediaScanIntent =  Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    val contentUri = Uri.fromFile(file.absoluteFile); //out is your file you saved/deleted/moved/copied
                    mediaScanIntent.setData(contentUri);
                    context.sendBroadcast(mediaScanIntent);
                } else {
                    context.sendBroadcast( Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
                }
//                updateContentProvider(Environment.getExternalStorageDirectory().absolutePath + "/" + path + ".pdf")

            }
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
            extensions.add("PDF")
            extensions.add("txt")
            extensions.add("doc")
            extensions.add("DOC")
            extensions.add("DOCX")
            extensions.add("docx")
            extensions.add("xls")
            extensions.add("xlsx")
            extensions.add("XLSX")
            extensions.add("ppt")
            extensions.add("pptx")
            extensions.add("PPTX")

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
        fun openSimpleReaderActivity(context: Context,path: String) {
            val tmBuilder = ToolManagerBuilder.from()
                .setUseDigitalSignature(false)
                .setAutoResizeFreeText(false)


            var cutoutMode = 0
            if (Utils.isPie()) {
                cutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            var builder = ViewerConfig.Builder()
            builder = builder
                .fullscreenModeEnabled(true)
                .multiTabEnabled(false)
                .documentEditingEnabled(true)
                .longPressQuickMenuEnabled(true)
                .showPageNumberIndicator(true)
                .showBottomNavBar(true)
                .showThumbnailView(true)
                .showBookmarksView(false)
                .showSearchView(true)
                .showShareOption(true)
                .showDocumentSettingsOption(true)
                .showAnnotationToolbarOption(true)
                .showOpenFileOption(true)
                .showOpenUrlOption(true)
                .showEditPagesOption(true)
                .showPrintOption(false)
                .showCloseTabOption(true)
                .showAnnotationsList(true)
                .showOutlineList(false)
                .showUserBookmarksList(true)
                .showCropOption(false)
                .showSaveCopyOption(false)
                .saveCopyExportPath(Environment.getExternalStorageDirectory().absolutePath)
                .pdfViewCtrlConfig(PDFViewCtrlConfig.getDefaultConfig(context))
                .toolManagerBuilder(tmBuilder)

            if (Utils.isPie()) {
                builder = builder.layoutInDisplayCutoutMode(cutoutMode)
            }
            val config = builder.build()
            SimpleReaderActivity.openDocument(context, Uri.parse(path),config)

//            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//            intent.data = Uri.fromFile(File(File(path).name.e))
//            sendBroadcast(intent)

        }
        fun rateAppClick(context: Context) {
            val uri = Uri.parse("market://details?id=" + context.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                    )
                )
            }

        }
        fun shareApp(context: Context){
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) { //e.toString();
            }
        }


    }




}