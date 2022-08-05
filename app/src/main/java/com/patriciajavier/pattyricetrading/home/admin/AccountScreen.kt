package com.patriciajavier.pattyricetrading.home.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.patriciajavier.pattyricetrading.databinding.FragmentAccountScreenBinding
import com.patriciajavier.pattyricetrading.databinding.FragmentAdminScreenBinding
import com.patriciajavier.pattyricetrading.home.admin.arch.AccountScreenViewModel

class AccountScreen : Fragment() {

    private val TAG = "AccountScreen:"

    private var _binding: FragmentAccountScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountScreenViewModel by activityViewModels()
    private val epoxyController = AccountScreenEpoxyController()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fetch all the users from fire store
        viewModel.getAllUsers()

        viewModel.listOfUser.observe(viewLifecycleOwner){
            binding.loadingState.root.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            epoxyController.listOfUser = it
        }

        binding.epoxyRecyclerView.setController(epoxyController)
    }
}