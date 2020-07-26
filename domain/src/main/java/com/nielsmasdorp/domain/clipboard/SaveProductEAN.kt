package com.nielsmasdorp.domain.clipboard

class SaveProductEAN(private val clipboard: Clipboard) {

    fun invoke(ean: String) {
        clipboard.saveClip(ean)
    }
}