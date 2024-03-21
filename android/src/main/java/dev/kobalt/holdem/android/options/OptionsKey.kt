package dev.kobalt.holdem.android.options

import dev.kobalt.holdem.android.base.BaseKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class OptionsKey(private val placeholder: String = "") : BaseKey() {
    override fun instantiateFragment() = OptionsFragment()
}