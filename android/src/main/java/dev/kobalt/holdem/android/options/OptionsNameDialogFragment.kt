package dev.kobalt.holdem.android.options

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import dev.kobalt.holdem.android.base.BaseDialogFragment
import dev.kobalt.holdem.android.databinding.OptionsNameBinding

class OptionsNameDialogFragment : BaseDialogFragment<OptionsNameBinding>() {

    override fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)
        viewBinding?.apply {
            nameInput.setText(application.preferences.name)
            root.post {
                inputMethodManager.showSoftInput(nameInput, InputMethodManager.SHOW_IMPLICIT)
            }
            cancelButton.apply {
                setOnClickListener { dismiss() }
            }
            submitButton.apply {
                setOnClickListener {
                    application.preferences.name = nameInput.text?.toString()
                    dismiss()
                }
            }
        }
    }

}