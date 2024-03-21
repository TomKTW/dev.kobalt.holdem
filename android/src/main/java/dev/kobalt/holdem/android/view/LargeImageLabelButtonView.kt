package dev.kobalt.holdem.android.view

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.setPadding
import dev.kobalt.holdem.android.R
import dev.kobalt.holdem.android.base.BaseContext

open class LargeImageLabelButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : StackView(context, attrs, defStyleAttr), BaseContext {

    val iconImage = ImageView(context)
    val titleLabel = LabelView(context)

    override fun requestContext(): Context = context.applicationContext

    init {
        onInit(context, attrs, defStyleAttr)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onInit(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        background = LayerDrawable(
            arrayOf(
                getResourceColor(R.color.black_a50).toDrawable(),
                RippleDrawable(
                    getResourceColorState(R.color.black),
                    null,
                    ShapeDrawable(RectShape())
                )
            )
        )
        obtainStyledAttributes(attrs, R.styleable.LargeImageLabelButtonView, defStyleAttr) {
            iconImage.apply {
                setPadding(dp(8))
                setImageDrawable(it.getValueDrawable(R.styleable.LargeImageLabelButtonView_iconImage))
            }
            titleLabel.apply {
                gravity = Gravity.CENTER
                setPadding(dp(16))
                setTextColor(getResourceColor(R.color.white))
                text = it.getValueString(R.styleable.LargeImageLabelButtonView_titleLabel)
            }
        }
        addView(iconImage, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f))
        addView(titleLabel, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

}