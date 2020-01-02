package com.reader.pdfreader.helper

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.WindowManager
import com.pdftron.demo.app.SimpleReaderActivity
import com.pdftron.pdf.config.PDFViewCtrlConfig
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.utils.Utils
import java.io.File
import java.io.FilenameFilter
import java.text.SimpleDateFormat

class Helper {
    companion object {
        fun getFiles(dir: String): List<File>? {
            return getFiles(
                dir,
                null
            )
        }

        private fun getFiles(dir: String, matchRegex: String?): List<File>? {
            val file = File(dir)
            var files: Array<File?>? = null
            files = if (matchRegex != null) {
                val filter =
                    FilenameFilter { dir, fileName -> fileName.matches(matchRegex.toRegex()) }
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

        fun getPreviousPath(pathFile: String): String {
            val path: String = pathFile
            val lastIndexOf = pathFile.lastIndexOf(File.separator)
            if (lastIndexOf < 0) {

                return pathFile
            }
            return path.substring(0, lastIndexOf)
        }

        fun openSimpleReaderActivity(context: Context, path: String) {
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
            SimpleReaderActivity.openDocument(context, Uri.parse(path), config)

//            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//            intent.data = Uri.fromFile(File(File(path).name.e))
//            sendBroadcast(intent)

        }


         fun getModifile(file: File): String {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            return sdf.format(file.lastModified())
        }
    }
}