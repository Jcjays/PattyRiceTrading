package com.patriciajavier.pattyricetrading.registration

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.databinding.FragmentResetPasswordScreenBinding
import com.patriciajavier.pattyricetrading.registration.arch.SharedViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class ResetPasswordScreen : Fragment() {

    private var _binding : FragmentResetPasswordScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel : SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetPasswordBtn.setOnClickListener {
            validateInput()
        }

    }

    private fun validateInput() {
        val emailAddress = binding.emailTextFieldForReset.text.toString().trim()
        val regexExpressionEmailAddress: Pattern =
            Pattern.compile(Constant.EMAIL_ADDRESS_VALIDATION)
        val matcherEmailAddress: Matcher = regexExpressionEmailAddress.matcher(emailAddress)

        if (emailAddress.isEmpty()) {
            binding.emailFieldContainerForReset.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        if (!matcherEmailAddress.matches()) {
            binding.emailFieldContainerForReset.error = Constant.TEXT_FIELD_ERROR_FORMAT_MSG
            return
        }
        //removing error
        binding.emailFieldContainerForReset.error = null
        binding.emailFieldContainerForReset.isErrorEnabled = false

        viewModel.resetPassword(emailAddress)

        binding.resetPasswordBtn.isEnabled = false
        Handler(Looper.myLooper()!!).postDelayed({
                binding.resetPasswordBtn.isEnabled = true
                              }, 20000 //Specific time in milliseconds
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =  null
    }
}