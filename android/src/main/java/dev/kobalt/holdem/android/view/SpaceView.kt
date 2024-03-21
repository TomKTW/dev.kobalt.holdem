/*
 * dev.kobalt.holdem
 * Copyright (C) 2024 Tom.K
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.kobalt.holdem.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import dev.kobalt.holdem.android.base.BaseContext

class SpaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), BaseContext {

    override fun requestContext(): Context = context.applicationContext

    /**
     * Draw nothing.
     *
     * @param canvas an unused parameter.
     */
    //@SuppressLint("MissingSuperCall")
    /*   override fun draw(canvas: Canvas?) {
       }*/

    /**
     * Compare to: [View.getDefaultSize]
     * If mode is AT_MOST, return the child size instead of the parent size
     * (unless it is too big).
     */
    private fun getDefaultSize2(size: Int, measureSpec: Int): Int {
        var result = size
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED -> result = size
            MeasureSpec.AT_MOST -> result = Math.min(size, specSize)
            MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize2(suggestedMinimumWidth, widthMeasureSpec),
            getDefaultSize2(suggestedMinimumHeight, heightMeasureSpec)
        )
    }
}