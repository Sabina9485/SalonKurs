package com.example.salon.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecializationDao {
    @Query("SELECT * FROM specializations ORDER BY title") fun getAll(): Flow<List<Specialization>>
    @Query("SELECT * FROM specializations WHERE id = :id") suspend fun getById(id: Int): Specialization?
    @Insert suspend fun insert(s: Specialization): Long
    @Update suspend fun update(s: Specialization)
    @Delete suspend fun delete(s: Specialization)
}

@Dao
interface MasterDao {
    @Query("""SELECT m.*, s.title AS specTitle FROM masters m
        INNER JOIN specializations s ON s.id = m.idSpecialization ORDER BY m.lastName""")
    fun getAllWithSpec(): Flow<List<MasterWithSpec>>
    @Query("SELECT * FROM masters WHERE id = :id") suspend fun getById(id: Int): Master?
    @Insert suspend fun insert(m: Master): Long
    @Update suspend fun update(m: Master)
    @Delete suspend fun delete(m: Master)
}

@Dao
interface ServiceTypeDao {
    @Query("SELECT * FROM service_types ORDER BY title") fun getAll(): Flow<List<ServiceType>>
    @Query("SELECT * FROM service_types WHERE id = :id") suspend fun getById(id: Int): ServiceType?
    @Insert suspend fun insert(t: ServiceType): Long
    @Update suspend fun update(t: ServiceType)
    @Delete suspend fun delete(t: ServiceType)
}

@Dao
interface ServiceDao {
    @Query("""SELECT sv.*, t.title AS typeTitle FROM services sv
        INNER JOIN service_types t ON t.id = sv.idServiceType ORDER BY t.title, sv.title""")
    fun getAllWithType(): Flow<List<ServiceWithType>>
    @Query("SELECT * FROM services WHERE id = :id") suspend fun getById(id: Int): Service?
    @Insert suspend fun insert(s: Service): Long
    @Update suspend fun update(s: Service)
    @Delete suspend fun delete(s: Service)
}

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY lastName") fun getAll(): Flow<List<Client>>
    @Query("SELECT * FROM clients WHERE id = :id") suspend fun getById(id: Int): Client?
    @Insert suspend fun insert(c: Client): Long
    @Update suspend fun update(c: Client)
    @Delete suspend fun delete(c: Client)
}

@Dao
interface AppointmentDao {
    // JOIN: вместо ID — имя клиента, имя мастера и название услуги
    @Query("""
        SELECT a.id AS id, a.dateTime AS dateTime, a.status AS status,
               (c.lastName || ' ' || c.name) AS clientName,
               (m.lastName || ' ' || m.name) AS masterName,
               s.title AS serviceTitle, s.price AS price
        FROM appointments a
        INNER JOIN clients c ON c.id = a.idClient
        INNER JOIN masters m ON m.id = a.idMaster
        INNER JOIN services s ON s.id = a.idService
        ORDER BY a.dateTime
    """)
    fun getAllDetailed(): Flow<List<AppointmentDetails>>
    @Query("SELECT * FROM appointments WHERE id = :id") suspend fun getById(id: Int): Appointment?
    @Insert suspend fun insert(a: Appointment): Long
    @Update suspend fun update(a: Appointment)
    @Delete suspend fun delete(a: Appointment)
    @Query("DELETE FROM appointments WHERE id = :id") suspend fun deleteById(id: Int)
}
