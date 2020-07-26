package com.nielsmasdorp.lvbnotifier.data.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import com.nielsmasdorp.domain.clipboard.Clipboard

class AndroidClipboard(private val context: Context) : Clipboard {

    private val manager: ClipboardManager by lazy {
        context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun saveClip(clip: String) {
        manager.primaryClip = ClipData.newPlainText(CLIP_DESCRIPTION, clip)
    }

    companion object {
        private const val CLIP_DESCRIPTION = "Product EAN"
    }
}