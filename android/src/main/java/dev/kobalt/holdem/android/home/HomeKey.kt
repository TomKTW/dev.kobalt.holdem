package dev.kobalt.holdem.android.home

import dev.kobalt.holdem.android.base.BaseKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeKey(private val placeholder: String = "") : BaseKey() {
    override fun instantiateFragment() = HomeFragment()
}