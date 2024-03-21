package dev.kobalt.holdem.android.about

import android.os.Bundle
import android.view.View
import dev.kobalt.holdem.android.base.BaseFragment
import dev.kobalt.holdem.android.databinding.AboutBinding

class AboutFragment : BaseFragment<AboutBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.apply {
            backButton.apply { setOnClickListener { onBackPressed() } }
        }
    }

}