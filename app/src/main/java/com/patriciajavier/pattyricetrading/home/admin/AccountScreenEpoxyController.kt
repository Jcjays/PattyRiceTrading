package com.patriciajavier.pattyricetrading.home.admin

import com.airbnb.epoxy.EpoxyController
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.AccountCardButtonModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

class AccountScreenEpoxyController : EpoxyController(){

    var listOfUser : DataOrException<List<User>, Exception> = DataOrException()
    set(value) {
        field = value
        requestModelBuild()
    }

    override fun buildModels() {
            listOfUser.data?.forEach {
                AccountCardButtonModel(
                    name = it.firstName + " " + it.lastName,
                    isAdmin = it.isAdmin
                ).id(it.email).addTo(this)
            }
    }

    data class AccountCardButtonModel(
        val name: String,
        val isAdmin: Boolean
    ): ViewBindingKotlinModel<AccountCardButtonModelBinding>(R.layout.account_card_button_model){
        override fun AccountCardButtonModelBinding.bind() {
            root.setOnClickListener {
                //todo
            }
            accountTitle.text = name
            accountRole.text = if(isAdmin) "Admin" else "Shopkeeper"
        }
    }
}