package dev.kobalt.holdem.android.play

import dev.kobalt.holdem.android.base.BaseKey
import kotlinx.parcelize.Parcelize


@Parcelize
data class PlayKey(private val placeholder: String = "") : BaseKey() {
    override fun instantiateFragment() = PlayFragment()
}