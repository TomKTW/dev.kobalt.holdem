package dev.kobalt.holdem.android.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import dev.kobalt.holdem.android.base.BaseContext

open class StackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr), BaseContext {

    override fun requestContext(): Context = context.applicationContext

}