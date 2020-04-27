package com.inensus.android.view.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.inensus.android.R
import com.inensus.android.util.SharedPreferencesWrapper
import kotlinx.android.synthetic.main.fragment_input_bottom_sheet.*

class InputBottomSheetFragment : BottomSheetDialogFragment() {

    override fun getTheme() = R.style.AppTheme_BottomSheet

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? =
            layoutInflater.inflate(R.layout.fragment_input_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInputView.hint = getString(R.string.hint_server_url)
        textInputView.text = SharedPreferencesWrapper.getInstance().baseUrl

        cancelButton.setOnClickListener {
            dismiss()
        }

        saveButton.setOnClickListener {
            SharedPreferencesWrapper.getInstance().baseUrl = textInputView.text
            Toast.makeText(context, getString(R.string.restart_app), Toast.LENGTH_LONG).show()
            dismiss()
        }

        textInputView.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveButton.performClick()
                true
            } else {
                false
            }
        })

        textInputView.requestFocus()
    }

    companion object {
        fun newInstance() = InputBottomSheetFragment()
    }
}
