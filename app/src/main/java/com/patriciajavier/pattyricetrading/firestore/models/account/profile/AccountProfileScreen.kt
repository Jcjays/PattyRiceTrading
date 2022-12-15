package com.patriciajavier.pattyricetrading.firestore.models.account.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentAccountProfileScreenBinding

class AccountProfileScreen : Fragment() {


    private var _binding: FragmentAccountProfileScreenBinding? = null
    private val binding get() = _binding!!

    private val args : AccountProfileScreenArgs by navArgs()
    private val viewModel : AccountProfileScreenViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountProfileScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.let {
            viewModel.getUserInfo(it.userId)
        }


        viewModel.userInfo.observe(viewLifecycleOwner){
            binding.loadingState.root.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            if(it.exception != null){
                Toast.makeText(requireContext(), it.exception!!.message, Toast.LENGTH_SHORT).show()
                return@observe
            }

            if(it.data != null){

                if(it.data!!.isAdmin)
                    binding.statusUserProfile.isGone = true
                else
                    binding.statusUserProfile.isVisible = true

                binding.roleAccountProfile.text = if(it.data!!.isAdmin) "Administrator" else "Shopkeeper"
                binding.statusUserProfile.text = if(it.data!!.isActive) "Set Inactive" else "Set Active"
                binding.nameAccountProfile.text = it.data!!.firstName + " " + it.data!!.lastName
                binding.addressAccountProfile.text = it.data!!.address
                binding.emailAccountProfile.text = it.data!!.email
                binding.phoneNumberAccountProfile.text = it.data!!.phoneNumber
            }
        }

        binding.editUserProfile.setOnClickListener {
            val action = AccountProfileScreenDirections.actionAccountProfileScreenToEditProfileScreen(args.userId)
            findNavController().navigate(action)
        }

        //setting the status of shopkeepers
        binding.statusUserProfile.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to set this user as active/inactive? If a user is set as inactive he/she cannot perform any operation in their app.")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    viewModel.setStatus(args.userId)
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .show()
        }

        //set role of shopkeeper
        binding.editUserRole.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to change this user's role? If a role is changed, the user will have new functions to use.")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    viewModel.setRole(args.userId)
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}