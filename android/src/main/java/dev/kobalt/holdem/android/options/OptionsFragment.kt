package dev.kobalt.holdem.android.options

import android.os.Bundle
import android.view.View
import dev.kobalt.holdem.android.base.BaseFragment
import dev.kobalt.holdem.android.databinding.OptionsBinding

class OptionsFragment : BaseFragment<OptionsBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.apply {
            backButton.apply { setOnClickListener { onBackPressed() } }
            editNameButton.apply {
                setOnClickListener {
                    OptionsNameDialogFragment().show(
                        childFragmentManager,
                        "OptionsName"
                    )
                }
            }
            editServerButton.apply {
                setOnClickListener {
                    OptionsServerDialogFragment().show(
                        childFragmentManager,
                        "OptionsServer"
                    )
                }
            }
        }
    }

}