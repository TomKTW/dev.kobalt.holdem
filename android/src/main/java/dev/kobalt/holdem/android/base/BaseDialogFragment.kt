package dev.kobalt.holdem.android.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseDialogFragment<V : ViewBinding> : DialogFragment(), BaseContext {

    override fun requestContext(): Context = requireContext().applicationContext

    private var binding: V? = null
    val viewBinding: V? get() = binding

    private val viewBindingClass =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
    private val method = viewBindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )

    @Suppress("UNCHECKED_CAST")
    protected open fun createBindingInstance(inflater: LayoutInflater, container: ViewGroup?): V {
        return method.invoke(null, inflater, container, false) as V
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val builder =
            AlertDialog.Builder(
                requireContext(),
                com.google.android.material.R.style.Theme_MaterialComponents_Dialog_Bridge
            )
        builder.setView(createBindingInstance(layoutInflater, null).also { binding = it }.root)
        return builder.create().apply {
            setCanceledOnTouchOutside(false)
            onDialogCreated(this, savedInstanceState)
        }
    }

    open fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    val viewLifecycleScope get() = viewLifecycleOwner.lifecycleScope

}