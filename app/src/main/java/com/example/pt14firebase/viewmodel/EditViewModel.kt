package com.example.pt14firebase.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pt14firebase.modeldata.DetailSiswa
import com.example.pt14firebase.modeldata.UIStateSiswa
import com.example.pt14firebase.modeldata.toDataSiswa
import com.example.pt14firebase.modeldata.toUiStateSiswa
import com.example.pt14firebase.repositori.RepositorySiswa
import com.example.pt14firebase.view.route.DestinasiDetail
import kotlinx.coroutines.launch

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorySiswa: RepositorySiswa
) : ViewModel() {

    var uiStateSiswa by mutableStateOf(UIStateSiswa())
        private set

    private val idSiswa: String =
        savedStateHandle[DestinasiDetail.itemIdArg]
            ?: error("idSiswa tidak ditemukan")

    init {
        viewModelScope.launch {
            repositorySiswa.getSatuSiswa(idSiswa)?.let { siswa ->
                uiStateSiswa = siswa.toUiStateSiswa(isEntryValid = true)
            }
        }
    }

    fun updateUiState(detailSiswa: DetailSiswa) {
        uiStateSiswa = UIStateSiswa(
            detailSiswa = detailSiswa,
            isEntryValid = validasiInput(detailSiswa)
        )
    }

    private fun validasiInput(
        detailSiswa: DetailSiswa = uiStateSiswa.detailSiswa
    ): Boolean {
        return detailSiswa.nama.isNotBlank() &&
                detailSiswa.alamat.isNotBlank() &&
                detailSiswa.telpon.isNotBlank()
    }

    fun editSatuSiswa(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            if (validasiInput()) {
                try {
                    repositorySiswa.editSatuSiswa(
                        idSiswa,
                        uiStateSiswa.detailSiswa.copy(
                            id = idSiswa
                        ).toDataSiswa()
                    )
                    onSuccess()
                } catch (e: Exception) {
                    println("Update Error: ${e.message}")
                }
            }
        }
    }
}
