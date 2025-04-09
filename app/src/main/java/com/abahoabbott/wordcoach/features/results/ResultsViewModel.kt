package com.abahoabbott.wordcoach.features.results

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class ResultsViewModel @Inject constructor(): ViewModel(){

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState



    fun loadResults(resultsUiState: ResultsUiState){
        _uiState.value = resultsUiState
    }


}