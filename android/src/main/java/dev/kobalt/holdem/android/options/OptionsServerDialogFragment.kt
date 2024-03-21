package dev.kobalt.holdem.android.options

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import dev.kobalt.holdem.android.base.BaseDialogFragment
import dev.kobalt.holdem.android.databinding.OptionsServerBinding

class OptionsServerDialogFragment : BaseDialogFragment<OptionsServerBinding>() {

    override fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)
        viewBinding?.apply {
            serverInput.setText(application.preferences.server)
            root.post {
                inputMethodManager.showSoftInput(serverInput, InputMethodManager.SHOW_IMPLICIT)
            }
            cancelButton.apply {
                setOnClickListener { dismiss() }
            }
            submitButton.apply {
                setOnClickListener {
                    application.preferences.server = serverInput.text?.toString()
                    dismiss()
                }
            }
        }
    }

}