package com.example.temptrack.languages

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun changeLanguageLocaleTo(lan: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lan)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

fun getLanguageLocale(): String {
    return AppCompatDelegate.getApplicationLocales().toLanguageTags()
}