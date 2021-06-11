package com.eogresources.screentransitions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor() : ViewModel() {
    var count by mutableStateOf(0)

    fun updateCount() {
        Log.v("FormVM", "Update Count")
        count++
    }

    init {
        Log.v("FormVM", "Init")
        count++
    }
}