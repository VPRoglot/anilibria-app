package ru.radiationx.anilibria.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import ru.radiationx.anilibria.App
import ru.radiationx.anilibria.ui.activities.main.IntentActivity
import ru.radiationx.data.entity.domain.release.Release
import ru.radiationx.shared.ktx.android.centerCrop
import ru.radiationx.shared.ktx.android.createAvatar
import kotlin.math.min

object ShortcutHelper {

    fun addShortcut(data: Release) {
        ImageLoader.getInstance().loadImage(data.poster, object : SimpleImageLoadingListener() {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap) {
                val minSize = min(loadedImage.width, loadedImage.height)
                val desiredSize = Resources.getSystem().displayMetrics.density * 48
                val scaleFactor = minSize / desiredSize
                val bmp = loadedImage
                    .centerCrop(minSize, minSize, scaleFactor)
                    .createAvatar(isCircle = true)
                addShortcut(data, bmp)
            }
        })
    }

    fun addShortcut(data: Release, bitmap: Bitmap) = addShortcut(
        App.instance,
        data.code ?: "release_${data.id}",
        (data.title ?: data.titleEng).toString(),
        data.names.joinToString(" / ") { it },
        data.link.orEmpty(),
        bitmap
    )

    private fun addShortcut(
        context: Context,
        id: String,
        shortLabel: String,
        longLabel: String,
        url: String,
        bitmap: Bitmap
    ) {
        val shortcut = ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setIcon(IconCompat.createWithBitmap(bitmap))
            /*.setIntent(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
            ))*/
            .setIntent(Intent(App.instance.applicationContext, IntentActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            })
            .build()

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            val callbackIntent = ShortcutManagerCompat.createShortcutResultIntent(context, shortcut)

            val successCallback = PendingIntent.getBroadcast(context, 0, callbackIntent, 0)

            ShortcutManagerCompat.requestPinShortcut(
                context,
                shortcut,
                successCallback.intentSender
            )
        }
    }

}