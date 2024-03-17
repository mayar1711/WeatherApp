package com.example.temptrack.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun changeLanguageLocaleTo(languageTag: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageTag)
    AppCompatDelegate.setApplicationLocales(appLocale)
}