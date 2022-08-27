package com.patriciajavier.pattyricetrading.registration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentLoginScreenBinding
import com.patriciajavier.pattyricetrading.registration.arch.SharedViewModel
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginScreen : Fragment() {

    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe firebase
        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            //loading state
            binding.loadingState.root.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            if (it.exception != null) {
                Toast.makeText(requireContext(), it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                return@observe
            }

            if(it.data != null) {
                //assign a global access to currentUserId
                MyApp.userId = it.data!!.uid
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.checkAccessRight(it.data!!.uid)
                }
            }
        }

        //Determine whether the user is admin or shopkeeper
        viewModel.checkAccessRights.observe(viewLifecycleOwner) { accessRights ->
            if(accessRights != null){

                //assign a global access to currentUserId
                MyApp.accessRights = accessRights

                val label = if(accessRights) "Admin" else "Shopkeeper"
                val action = LoginScreenDirections.actionLoginScreenToAdminScreen(label)
                findNavController().navigate(action)
            }
            viewModel.clearCheckingForAccessRights()
        }

        binding.resetShortcutButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreen_to_resetPasswordScreen)
        }

        binding.loginButton.setOnClickListener {
            validateInput()
        }

        binding.registerShortcutButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreen_to_registrationScreen)
        }

    }

    private fun validateInput() {

        val emailAddress = binding.emailTextField.text.toString().trim()
        val regexExpressionEmailAddress: Pattern =
            Pattern.compile(Constant.EMAIL_ADDRESS_VALIDATION)
        val matcherEmailAddress: Matcher = regexExpressionEmailAddress.matcher(emailAddress)

        if (emailAddress.isEmpty()) {
            binding.emailFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        if (!matcherEmailAddress.matches()) {
            binding.emailFieldContainer.error = Constant.TEXT_FIELD_ERROR_FORMAT_MSG
            return
        }
        //removing error
        binding.emailFieldContainer.error = null
        binding.emailFieldContainer.isErrorEnabled = false


        val password = binding.passwordTextField.text.toString().trim()
        val regexExpressionPassword: Pattern = Pattern.compile(Constant.PASSWORD_VALIDATION)
        val matcherPassword: Matcher = regexExpressionPassword.matcher(password)
        if (password.isEmpty()) {
            binding.passwordFilledContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        if (!matcherPassword.matches()) {
            binding.passwordFilledContainer.error = Constant.PASSWORD_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.passwordFilledContainer.error = null
        binding.passwordFilledContainer.isErrorEnabled = false

        viewModel.loginWithEmailPassword(emailAddress, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}