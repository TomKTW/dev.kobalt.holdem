package dev.kobalt.holdem.android.about

import dev.kobalt.holdem.android.base.BaseKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class AboutKey(private val placeholder: String = "") : BaseKey() {
    override fun instantiateFragment() = AboutFragment()
}