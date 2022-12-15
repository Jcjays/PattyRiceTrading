package com.patriciajavier.pattyricetrading.firestore.models.account.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.firebase.ui.auth.data.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentAccountProfileScreenBinding
import com.patriciajavier.pattyricetrading.databinding.FragmentEditProfileScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.EditableUserInfo
import java.util.regex.Matcher
import java.util.regex.Pattern


class EditProfileScreen : Fragment() {


    private var _binding: FragmentEditProfileScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : AccountProfileScreenViewModel by activityViewModels()
    private val args : EditProfileScreenArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.let {
            viewModel.getUserInfo(it.userId)
        }

        viewModel.userInfo.observe(viewLifecycleOwner){
            if(it.exception != null){
                Toast.makeText(requireContext(), it.exception!!.message, Toast.LENGTH_SHORT).show()
                return@observe
            }

            if(it.data != null){
                binding.newFirstNameEditProfile.setText(it.data!!.firstName)
                binding.newLastNameEditProfile.setText(it.data!!.lastName)
                binding.newAddressEditProfile.setText(it.data!!.address)
                binding.newPhoneNumberEditProfile.setText(it.data!!.phoneNumber)
            }
        }


        binding.cancelEditProfile.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel this operation?")
                .setMessage("All the new information cannot be save.")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    findNavController().navigateUp()
                }
                .show()
        }

        binding.saveEditProfile.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to edit this user with the provided new information?")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    validateInput()
                    findNavController().navigateUp()
                }
                .show()
        }
    }

    private fun validateInput() {

        val firstName = binding.newFirstNameEditProfile.text.toString().trim().replaceFirstChar { it.uppercase() }
        if(firstName.isEmpty()){
            binding.firstNameEditProfileTextField.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.firstNameEditProfileTextField.error = null
        binding.firstNameEditProfileTextField.isErrorEnabled = false

        //-------------------------------------------------------------------

        val lastName = binding.newLastNameEditProfile.text.toString().trim().replaceFirstChar { it.uppercase() }
        if(lastName.isEmpty()){
            binding.lastNameEditProfileTextField.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.lastNameEditProfileTextField.error = null
        binding.lastNameEditProfileTextField.isErrorEnabled = false

        //-------------------------------------------------------------------

        val phoneNumber = binding.newPhoneNumberEditProfile.text.toString().trim()
        val regexExpressionPhoneNumber : Pattern = Pattern.compile(Constant.PHONE_NUMBER_VALIDATION)
        val matcherPhoneNumber : Matcher = regexExpressionPhoneNumber.matcher(phoneNumber)

        if(phoneNumber.isEmpty()){
            binding.phoneNumberEditProfileTextField.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        if(!matcherPhoneNumber.matches()){
            binding.phoneNumberEditProfileTextField.error = Constant.TEXT_FIELD_ERROR_FORMAT_MSG
            return
        }
        //removing error
        binding.phoneNumberEditProfileTextField.error = null
        binding.phoneNumberEditProfileTextField.isErrorEnabled = false

        //-------------------------------------------------------------------

        val address = binding.newAddressEditProfile.text.toString().trim()
        if(address.isEmpty()){
            binding.addressEditProfileTextField.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.addressEditProfileTextField.error = null
        binding.addressEditProfileTextField.isErrorEnabled = false

        //-------------------------------------------------------------------

        val newUserInfo = EditableUserInfo(
            uId = args.userId,
            firstName = firstName,
            lastName = lastName,
            address = address,
            phoneNumber = phoneNumber
        )

        viewModel.updateUserProfile(newUserInfo)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}