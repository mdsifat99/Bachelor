package com.sifat.bachelor.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.sifat.bachelor.ViewState
import com.sifat.bachelor.api.model.LoginResponse
import com.sifat.bachelor.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LoginViewModel(private val repository: AppRepository): ViewModel() {

    private val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    fun userLogin(): LiveData<LoginResponse> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<LoginResponse>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.userLogin()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body != null) {
                            responseBody.value = response.body
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }

        return responseBody
    }

}