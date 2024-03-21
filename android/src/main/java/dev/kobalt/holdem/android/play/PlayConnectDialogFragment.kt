package dev.kobalt.holdem.android.play

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import dev.kobalt.holdem.android.base.BaseDialogFragment
import dev.kobalt.holdem.android.databinding.PlayConnectBinding

class PlayConnectDialogFragment : BaseDialogFragment<PlayConnectBinding>() {

    private val playFragment get() = parentFragment as? PlayFragment

    override fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            playFragment?.viewModel?.connectedFlow?.collect {
                viewBinding?.apply {
                    cancelButton.isEnabled = it != PlayConnectState.Connecting
                    connectButton.isEnabled = it != PlayConnectState.Connecting
                    urlInput.isEnabled = it != PlayConnectState.Connecting
                    nameInput.alpha = if (it != PlayConnectState.Connecting) 1.0f else 0.5f
                    cancelButton.alpha = if (it != PlayConnectState.Connecting) 1.0f else 0.5f
                    connectButton.alpha = if (it != PlayConnectState.Connecting) 1.0f else 0.5f
                    urlInput.alpha = if (it != PlayConnectState.Connecting) 1.0f else 0.5f
                    nameInput.alpha = if (it != PlayConnectState.Connecting) 1.0f else 0.5f
                    if (it == PlayConnectState.Connected) {
                        nameInput.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                            playFragment?.viewModel?.changeNickname(it)
                        }
                        dismiss()
                    }
                }
            }
        }
        viewBinding?.apply {
            root.post {
                inputMethodManager.showSoftInput(urlInput, InputMethodManager.SHOW_IMPLICIT)
            }
            urlInput.apply {
                setText(application.preferences.server)
            }
            nameInput.apply {
                setText(application.preferences.name)
            }
            cancelButton.apply {
                setOnClickListener { dismiss() }
            }
            connectButton.apply {
                setOnClickListener {
                    nameInput.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                        playFragment?.viewModel?.connect(urlInput.text?.toString().orEmpty())
                    } ?: run {
                        showToast("Please enter your nickname.")
                    }
                }
            }
        }
    }

}