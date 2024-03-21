package dev.kobalt.holdem.android.home

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import dev.kobalt.holdem.android.about.AboutKey
import dev.kobalt.holdem.android.base.BaseFragment
import dev.kobalt.holdem.android.databinding.HomeBinding
import dev.kobalt.holdem.android.options.OptionsKey
import dev.kobalt.holdem.android.play.PlayKey

class HomeFragment : BaseFragment<HomeBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.apply {
            playButton.apply { setOnClickListener { backstack.goTo(PlayKey()) } }
            optionsButton.apply { setOnClickListener { backstack.goTo(OptionsKey()) } }
            aboutButton.apply { setOnClickListener { backstack.goTo(AboutKey()) } }
        }
    }

}


