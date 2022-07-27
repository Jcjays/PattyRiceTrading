package com.patriciajavier.pattyricetrading.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentRegistrationScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.User
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegistrationScreen : Fragment() {

    private var _binding: FragmentRegistrationScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : RegistrationLoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner){
            binding.loadingState.root.visibility = if(it) View.VISIBLE else View.GONE
        }

        //observe firebase
        viewModel.userMutableLiveData.observe(viewLifecycleOwner){
            if(it.exception != null){
                Toast.makeText(requireContext(),it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                return@observe
            }

            if(it.data != null){
                Toast.makeText(requireContext(), it.data!!.email.toString(), Toast.LENGTH_SHORT).show()
                //todo navigate to home fragment
            }
        }


        binding.createAccountButton.setOnClickListener {
            validateInput()
        }

        binding.loginShortcutButton.setOnClickListener {
            findNavController().navigate(R.id.loginScreen)
        }

    }

    private fun validateInput(){
        val firstName = binding.firstNameTextField.text.toString().trim()
        if(firstName.isEmpty()){
            binding.firstNameFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.firstNameFieldContainer.error = null
        binding.firstNameFieldContainer.isErrorEnabled = false

        //-------------------------------------------------------------------

        val lastName = binding.lastNameTextField.text.toString().trim()
        if(lastName.isEmpty()){
            binding.lastNameFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.lastNameFieldContainer.error = null
        binding.lastNameFieldContainer.isErrorEnabled = false

        //-------------------------------------------------------------------

        val phoneNumber = binding.phoneNumberTextField.text.toString().trim()
        val regexExpressionPhoneNumber : Pattern = Pattern.compile(Constant.PHONE_NUMBER_VALIDATION)
        val matcherPhoneNumber : Matcher = regexExpressionPhoneNumber.matcher(phoneNumber)

        if(phoneNumber.isEmpty()){
            binding.phoneNumberFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }

        if(!matcherPhoneNumber.matches()){
            binding.phoneNumberFieldContainer.error = Constant.TEXT_FIELD_ERROR_FORMAT_MSG
            return
        }
        //removing error
        binding.phoneNumberFieldContainer.error = null
        binding.phoneNumberFieldContainer.isErrorEnabled = false

        //-------------------------------------------------------------------

        val address = binding.addressTextField.text.toString().trim()
        if(address.isEmpty()){
            binding.addressFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.addressFieldContainer.error = null
        binding.addressFieldContainer.isErrorEnabled = false

        //-------------------------------------------------------------------

        val emailAddress = binding.emailTextField.text.toString().trim()
        val regexExpressionEmailAddress : Pattern = Pattern.compile(Constant.EMAIL_ADDRESS_VALIDATION)
        val matcherEmailAddress : Matcher = regexExpressionEmailAddress.matcher(emailAddress)

        if(emailAddress.isEmpty()){
            binding.emailFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        if(!matcherEmailAddress.matches()){
            binding.emailFieldContainer.error = Constant.TEXT_FIELD_ERROR_FORMAT_MSG
            return
        }
        //removing error
        binding.emailFieldContainer.error = null
        binding.emailFieldContainer.isErrorEnabled = false

        //-------------------------------------------------------------------

        val password = binding.passwordTextField.text.toString().trim()
        val regexExpressionPassword : Pattern = Pattern.compile(Constant.PASSWORD_VALIDATION)
        val matcherPassword : Matcher = regexExpressionPassword.matcher(password)
        if(password.isEmpty()){
            binding.passwordFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        if(!matcherPassword.matches()){
            binding.passwordFieldContainer.error = Constant.PASSWORD_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.passwordFieldContainer.error = null
        binding.passwordFieldContainer.isErrorEnabled = false

        //wrap data to user obj which will be used to upload on fire store
        val user = User(
            firstName = firstName,
            lastName = lastName,
            address = address,
            phoneNumber = phoneNumber,
            email = emailAddress,
            password = password
        )

        viewModel.createUserWithEmailPassword(user)
    }
}