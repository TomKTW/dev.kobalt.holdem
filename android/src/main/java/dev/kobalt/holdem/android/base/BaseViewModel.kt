package dev.kobalt.holdem.android.base

import androidx.lifecycle.ViewModel
import dev.kobalt.holdem.android.main.MainApplication

open class BaseViewModel : ViewModel() {

    val application get() = MainApplication.Native.instance

}