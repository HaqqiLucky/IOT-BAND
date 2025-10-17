package com.example.smartbandiot.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PreferencesViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun weightUpdate(weight: Double){
        _user.value = _user.value?.copy(weight = weight)
    }
    fun updateHeight(height: Double) {
        _user.value = _user.value?.copy(height = height)
    }
    fun updateGoal(goal: String) {
        _user.value = _user.value?.copy(goal = goal)
    }
}