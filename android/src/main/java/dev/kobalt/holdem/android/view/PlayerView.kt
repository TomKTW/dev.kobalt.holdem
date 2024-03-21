package dev.kobalt.holdem.android.view

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.caverock.androidsvg.SVG
import dev.kobalt.holdem.android.R
import dev.kobalt.holdem.android.base.BaseView
import dev.kobalt.holdem.android.databinding.ViewPlayerBinding
import dev.kobalt.holdem.android.state.HoldemCard
import dev.kobalt.holdem.android.state.StateEntity


class PlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseView<ViewPlayerBinding>(context, attrs, defStyleAttr) {

    private val backgroundRadius = dp(8).toFloat()
    private val backgroundDrawable = ShapeDrawable(
        RoundRectShape(
            floatArrayOf(
                backgroundRadius,
                backgroundRadius,
                backgroundRadius,
                backgroundRadius,
                backgroundRadius,
                backgroundRadius,
                backgroundRadius,
                backgroundRadius
            ), null, null
        )
    ).apply {
        paint.color = getResourceColor(R.color.clear)
    }

    init {
        background = backgroundDrawable
    }

    fun apply(entity: StateEntity.Player) {
        viewBinding.nameLabel.text = entity.name
        viewBinding.dealerTagImage.isVisible = entity.tags.contains("dealer")
        val firstCard = entity.hand.getOrNull(0)?.src?.let { HoldemCard.valueOf(it) }?.image
        viewBinding.firstCardImage.isInvisible = firstCard == null
        viewBinding.firstCardImage.setSVG(
            SVG.getFromAsset(
                context.assets,
                "${firstCard ?: "1B"}.svg"
            )
        )
        val secondCard = entity.hand.getOrNull(1)?.src?.let { HoldemCard.valueOf(it) }?.image
        viewBinding.secondCardImage.isInvisible = secondCard == null
        viewBinding.secondCardImage.setSVG(
            SVG.getFromAsset(
                context.assets,
                "${secondCard ?: "1B"}.svg"
            )
        )
        viewBinding.playerImage.isVisible =
            !viewBinding.firstCardImage.isVisible || !viewBinding.secondCardImage.isVisible
        viewBinding.valueLabel.text = entity.money?.toString()
        viewBinding.actionLabel.text = when {
            entity.action == "Idle" && (entity.betMoney == 0 || entity.betMoney == null) -> {
                ""
            }

            entity.betMoney == 0 || entity.betMoney == null -> {
                entity.action
            }

            else -> {
                "${entity.action} (${entity.betMoney})"
            }
        }
        viewBinding.actionLabel.isInvisible = !entity.action.orEmpty().isNotEmpty()
        when {
            entity.tags.contains("current") -> {
                backgroundDrawable.paint.color = getResourceColor(R.color.black_a50)
            }

            entity.tags.contains("winner") -> {
                backgroundDrawable.paint.color = getResourceColor(R.color.white_a50)
            }

            else -> {
                backgroundDrawable.paint.color = getResourceColor(R.color.clear)
            }
        }
    }

}