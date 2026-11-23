package com.example.salon.data

import androidx.room.*

@Entity(tableName = "specializations")
data class Specialization(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val salary: Double
)

@Entity(
    tableName = "masters",
    foreignKeys = [ForeignKey(Specialization::class, ["id"], ["idSpecialization"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("idSpecialization")]
)
data class Master(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lastName: String,
    val name: String,
    val middleName: String,
    val phoneNumber: String,
    val dateOfEmployment: Long,       // epoch millis
    val experience: Int,
    val idSpecialization: Int,
    val idPosition: Int               // справочника Positions на схеме нет, поле хранится «для полноты»
)

@Entity(tableName = "service_types")
data class ServiceType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String
)

@Entity(
    tableName = "services",
    foreignKeys = [ForeignKey(ServiceType::class, ["id"], ["idServiceType"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("idServiceType")]
)
data class Service(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idServiceType: Int,
    val title: String,
    val price: Double
)

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lastName: String,
    val name: String,
    val middleName: String,
    val gender: String,
    val phoneNumber: String,
    val dateOfBirth: Long,
    val allergies: String
)

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(Client::class, ["id"], ["idClient"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Master::class, ["id"], ["idMaster"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Service::class, ["id"], ["idService"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("idClient"), Index("idMaster"), Index("idService")]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idClient: Int,
    val idMaster: Int,
    val idService: Int,
    val dateTime: Long,               // дата и время приёма, epoch millis
    val status: String                // Ожидание / Выполнено / Отменено
)

// ---- результаты JOIN-запросов ----
data class AppointmentDetails(
    val id: Int,
    val dateTime: Long,
    val status: String,
    val clientName: String,
    val masterName: String,
    val serviceTitle: String,
    val price: Double
)

data class MasterWithSpec(@Embedded val master: Master, val specTitle: String)
data class ServiceWithType(@Embedded val service: Service, val typeTitle: String)
