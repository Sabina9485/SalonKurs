package com.example.salon.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.salon.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AppRepository(AppDatabase.get(app))
    private fun <T> Flow<T>.state(init: T) = stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), init)

    val appointments = repo.detailedAppointments.state(emptyList())
    val clients = repo.allClients.state(emptyList())
    val masters = repo.mastersWithSpec.state(emptyList())
    val services = repo.servicesWithType.state(emptyList())
    val specializations = repo.specializations.state(emptyList())
    val serviceTypes = repo.serviceTypes.state(emptyList())

    suspend fun getClient(id: Int) = repo.getClient(id)
    suspend fun getMaster(id: Int) = repo.getMaster(id)
    suspend fun getService(id: Int) = repo.getService(id)
    suspend fun getAppointment(id: Int) = repo.getAppointment(id)
    suspend fun getSpecialization(id: Int) = repo.getSpecialization(id)
    suspend fun getServiceType(id: Int) = repo.getServiceType(id)

    fun saveClient(c: Client) = viewModelScope.launch { repo.saveClient(c) }
    fun deleteClient(c: Client) = viewModelScope.launch { repo.deleteClient(c) }
    fun saveMaster(m: Master) = viewModelScope.launch { repo.saveMaster(m) }
    fun deleteMaster(m: Master) = viewModelScope.launch { repo.deleteMaster(m) }
    fun saveService(s: Service) = viewModelScope.launch { repo.saveService(s) }
    fun deleteService(s: Service) = viewModelScope.launch { repo.deleteService(s) }
    fun saveAppointment(a: Appointment) = viewModelScope.launch { repo.saveAppointment(a) }
    fun deleteAppointment(id: Int) = viewModelScope.launch { repo.deleteAppointment(id) }
    fun saveSpecialization(s: Specialization) = viewModelScope.launch { repo.saveSpecialization(s) }
    fun deleteSpecialization(s: Specialization) = viewModelScope.launch { repo.deleteSpecialization(s) }
    fun saveServiceType(t: ServiceType) = viewModelScope.launch { repo.saveServiceType(t) }
    fun deleteServiceType(t: ServiceType) = viewModelScope.launch { repo.deleteServiceType(t) }
}
