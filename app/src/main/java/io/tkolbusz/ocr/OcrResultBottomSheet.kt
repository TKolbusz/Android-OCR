package io.tkolbusz.ocr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.tkolbusz.ocr.databinding.BottomsheetOcrResultBinding

class OcrResultBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetOcrResultBinding
    private lateinit var resultText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            resultText = it.getString(ARG_RESULT_TEXT, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetOcrResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resultTextView.text = resultText

        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun copyToClipboard() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("OCR Result", resultText)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), R.string.text_copied, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val OCR_BOTTOM_SHEET_TAG = "OcrResultBottomSheet"
        private const val ARG_RESULT_TEXT = "result_text"

        fun newInstance(resultText: String): OcrResultBottomSheet {
            return OcrResultBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_RESULT_TEXT, resultText)
                }
            }
        }
    }
}
