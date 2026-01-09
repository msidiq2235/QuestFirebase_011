package com.example.pt14firebase.repositori

import com.example.pt14firebase.modeldata.Siswa
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface RepositorySiswa {
    suspend fun getDataSiswa(): List<Siswa>
    suspend fun postDataSiswa(siswa: Siswa)
    suspend fun getSatuSiswa(id: String): Siswa?
    suspend fun editSatuSiswa(id: String, siswa: Siswa)
    suspend fun hapusSatuSiswa(id: String)
}

class FirebaseRepositorySiswa : RepositorySiswa {

    private val collection =
        FirebaseFirestore.getInstance().collection("siswa")

    override suspend fun getDataSiswa(): List<Siswa> {
        return try {
            collection.get().await().documents.map { doc ->
                Siswa(
                    id = doc.id, // 🔥 ID ASLI FIRESTORE
                    nama = doc.getString("nama") ?: "",
                    alamat = doc.getString("alamat") ?: "",
                    telpon = doc.getString("telpon") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun postDataSiswa(siswa: Siswa) {
        val docRef =
            if (siswa.id.isBlank()) collection.document()
            else collection.document(siswa.id)

        docRef.set(
            mapOf(
                "nama" to siswa.nama,
                "alamat" to siswa.alamat,
                "telpon" to siswa.telpon
            )
        ).await()
    }

    override suspend fun getSatuSiswa(id: String): Siswa? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                Siswa(
                    id = doc.id,
                    nama = doc.getString("nama") ?: "",
                    alamat = doc.getString("alamat") ?: "",
                    telpon = doc.getString("telpon") ?: ""
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun editSatuSiswa(id: String, siswa: Siswa) {
        collection.document(id).update(
            mapOf(
                "nama" to siswa.nama,
                "alamat" to siswa.alamat,
                "telpon" to siswa.telpon
            )
        ).await()
    }

    override suspend fun hapusSatuSiswa(id: String) {
        collection.document(id).delete().await()
    }
}
