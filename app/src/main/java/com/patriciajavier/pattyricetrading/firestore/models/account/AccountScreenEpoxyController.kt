package com.patriciajavier.pattyricetrading.firestore.models.account

import com.airbnb.epoxy.EpoxyController
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.AccountCardButtonModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

class AccountScreenEpoxyController(
    private val onClick: (String) -> Unit
) : EpoxyController(){

    var listOfUser : DataOrException<List<User>, Exception> = DataOrException()
    set(value) {
        field = value
        requestModelBuild()
    }

    override fun buildModels() {
            listOfUser.data?.sortedBy{it.firstName}?.forEach{
                AccountCardButtonModel(
                    data = it,
                    onClick
                ).id(it.email).addTo(this)
            }
    }

    data class AccountCardButtonModel(
        val data: User,
        val onClick: (String) -> Unit
    ): ViewBindingKotlinModel<AccountCardButtonModelBinding>(R.layout.account_card_button_model){
        override fun AccountCardButtonModelBinding.bind() {
            root.setOnClickListener {
                onClick(data.uId)
            }

            accountTitle.text = data.firstName + " " + data.lastName
            accountRole.text = if(data.isAdmin) "Admin" else "Shopkeeper"
        }
    }
}