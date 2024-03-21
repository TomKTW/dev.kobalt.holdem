package dev.kobalt.holdem.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import dev.kobalt.holdem.android.base.BaseContext

open class LabelInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr), BaseContext {

    override fun requestContext(): Context = context.applicationContext

    init {

        isFocusable = true
        isFocusableInTouchMode = true
    }

    /*
    var fontSize: Int
        get() = sp(textSize.toInt())
        set(value) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, value.toFloat())
        }

    init {
        onInit(attrs, defStyleAttr)
    }

    private fun onInit(attrs: AttributeSet?, defStyleAttr: Int) {
        isFocusable = true
        isFocusableInTouchMode = true

        /*
        obtainStyledAttributes(
            set = attrs,
            attrs = R.styleable.LabelInputView,
            defStyleAttr = defStyleAttr
        ) {
            typeface = getResourceFont(
                it.getResourceId(
                    R.styleable.LabelInputView_baseFont,
                    R.font.calibri
                )
            )
        }
         */

        /*
        background = StateListDrawable().apply {
            addState(
                intArrayOf(android.R.attr.state_focused),
                BorderShapeDrawable(
                    shape = RectShape(),
                    strokeWidth = dp(4),
                    strokeColor = getResourceColor(R.color.black),
                    fillColor = getResourceColor(R.color.white)
                )
            )
            addState(
                intArrayOf(android.R.attr.state_pressed),
                BorderShapeDrawable(
                    shape = RectShape(),
                    strokeWidth = dp(4),
                    strokeColor = getResourceColor(R.color.gray),
                    fillColor = getResourceColor(R.color.white)
                )
            )
            addState(
                intArrayOf(-android.R.attr.state_enabled),
                BorderShapeDrawable(
                    shape = RectShape(),
                    strokeWidth = dp(4),
                    strokeColor = getResourceColor(R.color.grayLight),
                    fillColor = getResourceColor(R.color.white)
                )
            )
            addState(
                intArrayOf(),
                BorderShapeDrawable(
                    shape = RectShape(),
                    strokeWidth = dp(4),
                    strokeColor = getResourceColor(R.color.grayLight),
                    fillColor = getResourceColor(R.color.white)
                )
            )
        }
        */

    }*/

    /*
     * Possible workaround method overrides for crash that seems to occur on Samsung devices on long tap.
     *
     * References:
     * https://stackoverflow.com/questions/42926522/java-lang-nullpointerexception-with-nougat
     * https://stackoverflow.com/questions/52497289/app-crashes-when-long-clicking-on-text-view-hint
     *
     * Cause (WTF?):
     * "Attempt to invoke virtual method 'boolean android.widget.Editor$SelectionModifierCursorController.isDragAcceleratorActive()' on a null object reference"
     */

    override fun performLongClick(): Boolean {
        return try {
            super.performLongClick()
        } catch (e: NullPointerException) {
            e.printStackTrace()
            true
        }
    }

    override fun performLongClick(x: Float, y: Float): Boolean {
        return try {
            super.performLongClick(x, y)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return try {
            super.onTouchEvent(event)
        } catch (e: NullPointerException) {
            e.printStackTrace(); true
        }
    }

    override fun performClick(): Boolean {
        return try {
            super.performClick()
        } catch (e: NullPointerException) {
            e.printStackTrace()
            true
        }
    }

}

