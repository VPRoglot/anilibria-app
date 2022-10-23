package ru.radiationx.anilibria.utils

import android.app.DownloadManager
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Environment
import android.util.Log
import ru.radiationx.anilibria.App
import ru.radiationx.shared_app.common.MimeTypeUtil
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.text.DecimalFormat
import kotlin.math.pow

/**
 * Created by isanechek on 30.07.16.
 */

object Utils {

    fun readableFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()

        val number = size / 1024.0.pow(digitGroups.toDouble())
        val formattedNumber = DecimalFormat("#,##0.#").format(number)
        val unit = units[digitGroups]
        return "$formattedNumber $unit"
    }

    /* PLEASE CHECK STORAGE PERMISSION */
    fun systemDownloader(
        context: Context,
        url: String,
        fileName: String = getFileNameFromUrl(url)
    ) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        dm?.let {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            request.setMimeType(MimeTypeUtil.getType(fileName))
            request.setTitle(fileName)
            request.setDescription(fileName)
            it.enqueue(request)
        }
    }

    fun getFileNameFromUrl(url: String): String {
        var fileName = url
        try {
            fileName = URLDecoder.decode(url, "CP1251")
        } catch (e: UnsupportedEncodingException) {
            Timber.e(e)
        }

        val cut = fileName.lastIndexOf('/')
        if (cut != -1) {
            fileName = fileName.substring(cut + 1)
        }
        return fileName
    }

    @Suppress("UsePropertyAccessSyntax")
    fun copyToClipBoard(s: String) {
        val clipboard = App.instance.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", s)
        clipboard.setPrimaryClip(clip)
    }

    fun readFromClipboard(): String? {
        val clipboard = App.instance.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val description = clipboard.primaryClipDescription
            val data = clipboard.primaryClip
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return data.getItemAt(0).text.toString()
        }
        return null
    }

    fun shareText(text: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        App.instance.startActivity(
            Intent.createChooser(sendIntent, "Поделиться").addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun externalLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(FLAG_ACTIVITY_NEW_TASK)
        App.instance.startActivity(
            Intent.createChooser(intent, "Открыть в").addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun longLog(msg: String) {
        val maxLogSize = 1000
        for (i in 0..msg.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > msg.length) msg.length else end
            Log.v("LONG_LOG", msg.substring(start, end))
        }
    }
}
