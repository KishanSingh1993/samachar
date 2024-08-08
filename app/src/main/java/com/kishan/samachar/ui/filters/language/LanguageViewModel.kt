package com.kishan.samachar.ui.filters.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kishan.samachar.data.model.Language
import com.kishan.samachar.data.repository.MainRepository
import com.kishan.samachar.ui.base.UiState
import com.kishan.samachar.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _languageList = MutableStateFlow<UiState<List<Language>>>(UiState.Loading)

    val languageList: StateFlow<UiState<List<Language>>> = _languageList

    init {
        getAllLanguages()
    }

    private fun getAllLanguages() {
        viewModelScope.launch(dispatcherProvider.main) {
            repository.getAllLanguages()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _languageList.value = UiState.Error(e.message.toString())
                }
                .collect {
                    _languageList.value = UiState.Success(it)
                }
        }
    }

}