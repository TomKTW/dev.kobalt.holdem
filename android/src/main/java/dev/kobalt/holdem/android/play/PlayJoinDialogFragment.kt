package dev.kobalt.holdem.android.play

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import dev.kobalt.holdem.android.base.BaseDialogFragment
import dev.kobalt.holdem.android.databinding.PlayJoinBinding

class PlayJoinDialogFragment : BaseDialogFragment<PlayJoinBinding>() {

    private val playFragment get() = parentFragment as? PlayFragment

    override fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)
        viewBinding?.apply {
            root.post {
                inputMethodManager.showSoftInput(idInput, InputMethodManager.SHOW_IMPLICIT)
            }
            cancelButton.apply {
                setOnClickListener { dismiss() }
            }
            submitButton.apply {
                setOnClickListener {
                    idInput.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                        playFragment?.viewModel?.joinRoom(it)
                        dismiss()
                    }
                }
            }
        }
    }

}