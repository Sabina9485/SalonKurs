package com.example.salon.data

class AppRepository(db: AppDatabase) {
    private val specs = db.specializationDao()
    private val masters = db.masterDao()
    private val types = db.serviceTypeDao()
    private val services = db.serviceDao()
    private val clients = db.clientDao()
    private val appointments = db.appointmentDao()

    val specializations = specs.getAll()
    val mastersWithSpec = masters.getAllWithSpec()
    val serviceTypes = types.getAll()
    val servicesWithType = services.getAllWithType()
    val allClients = clients.getAll()
    val detailedAppointments = appointments.getAllDetailed()

    suspend fun getClient(id: Int) = clients.getById(id)
    suspend fun saveClient(c: Client) { if (c.id == 0) clients.insert(c) else clients.update(c) }
    suspend fun deleteClient(c: Client) = clients.delete(c)

    suspend fun getMaster(id: Int) = masters.getById(id)
    suspend fun saveMaster(m: Master) { if (m.id == 0) masters.insert(m) else masters.update(m) }
    suspend fun deleteMaster(m: Master) = masters.delete(m)

    suspend fun getService(id: Int) = services.getById(id)
    suspend fun saveService(s: Service) { if (s.id == 0) services.insert(s) else services.update(s) }
    suspend fun deleteService(s: Service) = services.delete(s)

    suspend fun getAppointment(id: Int) = appointments.getById(id)
    suspend fun saveAppointment(a: Appointment) { if (a.id == 0) appointments.insert(a) else appointments.update(a) }
    suspend fun deleteAppointment(id: Int) = appointments.deleteById(id)

    suspend fun getSpecialization(id: Int) = specs.getById(id)
    suspend fun saveSpecialization(s: Specialization) { if (s.id == 0) specs.insert(s) else specs.update(s) }
    suspend fun deleteSpecialization(s: Specialization) = specs.delete(s)

    suspend fun getServiceType(id: Int) = types.getById(id)
    suspend fun saveServiceType(t: ServiceType) { if (t.id == 0) types.insert(t) else types.update(t) }
    suspend fun deleteServiceType(t: ServiceType) = types.delete(t)
}
