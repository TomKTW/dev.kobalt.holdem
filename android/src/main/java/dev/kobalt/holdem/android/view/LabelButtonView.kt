package dev.kobalt.holdem.android.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import dev.kobalt.holdem.android.base.BaseContext

open class LabelButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(
    context, attrs, defStyleAttr
), BaseContext {

    override fun requestContext(): Context = context.applicationContext

    init {
        onInit(attrs, defStyleAttr)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onInit(attrs: AttributeSet?, defStyleAttr: Int) {
    }

}