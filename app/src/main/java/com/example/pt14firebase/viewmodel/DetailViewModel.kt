package com.example.pt14firebase.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pt14firebase.modeldata.Siswa
import com.example.pt14firebase.repositori.RepositorySiswa
import com.example.pt14firebase.view.route.DestinasiDetail
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface StatusUIDetail {
    data class Success(val satusiswa: Siswa) : StatusUIDetail
    object Error : StatusUIDetail
    object Loading : StatusUIDetail
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorySiswa: RepositorySiswa
) : ViewModel() {

    private val idSiswa: String =
        savedStateHandle[DestinasiDetail.itemIdArg]
            ?: throw IllegalArgumentException("idSiswa tidak ditemukan")

    var statusUIDetail: StatusUIDetail by mutableStateOf(StatusUIDetail.Loading)
        private set

    init {
        getSatuSiswa()
    }

    private fun getSatuSiswa() {
        viewModelScope.launch {
            statusUIDetail = StatusUIDetail.Loading
            statusUIDetail = try {
                val siswa = repositorySiswa.getSatuSiswa(idSiswa)
                    ?: throw IOException("Data siswa tidak ditemukan")

                StatusUIDetail.Success(satusiswa = siswa)
            } catch (e: Exception) {
                StatusUIDetail.Error
            }
        }
    }

    fun hapusSatuSiswa() {
        viewModelScope.launch {
            try {
                repositorySiswa.hapusSatuSiswa(idSiswa)
                println("Sukses Hapus Data: $idSiswa")
            } catch (e: Exception) {
                println("Gagal Hapus Data: ${e.message}")
            }
        }
    }
}