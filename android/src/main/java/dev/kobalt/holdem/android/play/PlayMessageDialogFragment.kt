package dev.kobalt.holdem.android.play

import android.app.Dialog
import android.os.Bundle
import dev.kobalt.holdem.android.base.BaseDialogFragment
import dev.kobalt.holdem.android.databinding.PlayMessageBinding

class PlayMessageDialogFragment : BaseDialogFragment<PlayMessageBinding>() {

    override fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)
        viewBinding?.apply {
            messageLabel.text = requireArguments().getString("message")
            closeButton.apply { setOnClickListener { dismiss() } }
        }
    }

}