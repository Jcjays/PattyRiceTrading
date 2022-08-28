package com.patriciajavier.pattyricetrading.registration

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentRegistrationScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.User
import com.patriciajavier.pattyricetrading.registration.arch.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegistrationScreen : Fragment() {

    private var _binding: FragmentRegistrationScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentRegistrationScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe firebase
        viewModel.userMutableLiveData.observe(viewLifecycleOwner){
            //Observe loading state
            if(it.isLoading)
                binding.loadingState.root.isVisible = true

            if(it.exception != null){
                Toast.makeText(requireContext(),it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                return@observe
            }
            if(it.data != null){
                MyApp.userId = it.data!!.uid
                binding.loadingState.root.isVisible = false
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.checkAccessRight(it.data!!.uid)
                }
            }
        }

        //Determine whether the user is admin or shopkeeper
        viewModel.checkAccessRights.observe(viewLifecycleOwner){ accessRights ->
            if(accessRights != null){

                MyApp.accessRights = accessRights

                val label = if(accessRights) "Admin" else "Shopkeeper"
                val action = RegistrationScreenDirections.actionRegistrationScreenToAdminScreen(label)
                findNavController().navigate(action)
            }
            viewModel.clearCheckingForAccessRights()
        }

        //validating input from users
        binding.createAccountButton.setOnClickListener {
            validateInput()
        }

        //navigate to login screen
        binding.loginShortcutButton.setOnClickListener {
            findNavController().navigate(R.id.loginScreen)
        }

    }

    private fun validateInput(){
        val firstName = binding.firstNameTextField.text.toString().trim().replaceFirstChar { it.uppercase() }
        if(firstName.isEmpty()){
            binding.firstNameFieldContainer.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.firstNameFieldContainer.error = null
        binding.firstNameFieldContainer.isErrorEnabled = false

        //-------------------------------------------------------------------

        val lastName = binding.lastNameTextField.text.toString().trim().replaceFirstChar { it.uppercase() }
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

        //wrap the data to user obj which will be used to upload on fire store
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.checkAccessRights.removeObservers(viewLifecycleOwner)
        viewModel.userMutableLiveData.removeObservers(viewLifecycleOwner)
        _binding = null
    }
}