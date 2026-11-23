package com.example.salon.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Calendar

@Database(
    entities = [Specialization::class, Master::class, ServiceType::class, Service::class, Client::class, Appointment::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun specializationDao(): SpecializationDao
    abstract fun masterDao(): MasterDao
    abstract fun serviceTypeDao(): ServiceTypeDao
    abstract fun serviceDao(): ServiceDao
    abstract fun clientDao(): ClientDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "salon.db")
                .addCallback(object : Callback() {
                    // Вызывается один раз, при создании файла БД: заполняем тестовыми данными
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        seed(db)
                    }
                })
                .build().also { INSTANCE = it }
        }
    }
}

private fun date(y: Int, m: Int, d: Int): Long =
    Calendar.getInstance().apply { clear(); set(y, m - 1, d) }.timeInMillis

private fun seed(db: SupportSQLiteDatabase) {
    val day = 24L * 60 * 60 * 1000
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val specs = listOf(
        "Парикмахер-стилист" to 60000.0, "Мастер маникюра" to 55000.0, "Косметолог" to 75000.0,
        "Бровист" to 50000.0, "Массажист" to 65000.0, "Визажист" to 58000.0, "Трихолог" to 80000.0,
        "Мастер эпиляции" to 52000.0, "Тату-мастер" to 70000.0, "Дерматолог-косметолог" to 95000.0
    )
    specs.forEachIndexed { i, s ->
        db.execSQL("INSERT INTO specializations(id, title, salary) VALUES (?, ?, ?)", arrayOf(i + 1, s.first, s.second))
    }

    // фамилия, имя, отчество, телефон, дата приёма на работу, стаж
    val masters = listOf(
        listOf("Иванова", "Анна", "Сергеевна", "+79001000001", date(2019, 3, 15), 7),
        listOf("Петрова", "Мария", "Игоревна", "+79001000002", date(2020, 6, 1), 5),
        listOf("Смирнова", "Елена", "Викторовна", "+79001000003", date(2016, 9, 12), 10),
        listOf("Кузнецова", "Ольга", "Андреевна", "+79001000004", date(2021, 2, 20), 4),
        listOf("Соколов", "Дмитрий", "Павлович", "+79001000005", date(2018, 11, 5), 8),
        listOf("Новикова", "Татьяна", "Олеговна", "+79001000006", date(2022, 1, 17), 3),
        listOf("Морозова", "Ирина", "Николаевна", "+79001000007", date(2015, 5, 25), 11),
        listOf("Волкова", "Светлана", "Дмитриевна", "+79001000008", date(2020, 10, 9), 5),
        listOf("Лебедев", "Артём", "Михайлович", "+79001000009", date(2017, 7, 30), 9),
        listOf("Козлова", "Наталья", "Александровна", "+79001000010", date(2014, 4, 14), 12)
    )
    masters.forEachIndexed { i, m ->
        db.execSQL(
            "INSERT INTO masters(id, lastName, name, middleName, phoneNumber, dateOfEmployment, experience, idSpecialization, idPosition) VALUES (?,?,?,?,?,?,?,?,?)",
            arrayOf(i + 1, m[0], m[1], m[2], m[3], m[4], m[5], i + 1, (i % 3) + 1)
        )
    }

    val types = listOf(
        "Стрижки и укладки", "Окрашивание", "Маникюр", "Педикюр", "Уход за лицом",
        "Брови и ресницы", "Массаж", "Эпиляция", "Макияж", "Инъекционная косметология"
    )
    types.forEachIndexed { i, t -> db.execSQL("INSERT INTO service_types(id, title) VALUES (?, ?)", arrayOf(i + 1, t)) }

    val services = listOf(
        "Женская стрижка" to 2500.0, "Окрашивание в один тон" to 6000.0, "Маникюр с покрытием" to 2200.0,
        "Педикюр аппаратный" to 3000.0, "Чистка лица ультразвуком" to 3500.0, "Ламинирование бровей" to 2000.0,
        "Классический массаж спины" to 2800.0, "Лазерная эпиляция ног" to 4500.0,
        "Вечерний макияж" to 3200.0, "Биоревитализация" to 9000.0
    )
    services.forEachIndexed { i, s ->
        db.execSQL("INSERT INTO services(id, idServiceType, title, price) VALUES (?, ?, ?, ?)", arrayOf(i + 1, i + 1, s.first, s.second))
    }

    // фамилия, имя, отчество, пол, телефон, дата рождения, аллергии
    val clients = listOf(
        listOf("Орлова", "Дарья", "Алексеевна", "Женский", "+79102000001", date(1992, 5, 14), "Нет"),
        listOf("Фёдоров", "Максим", "Игоревич", "Мужской", "+79102000002", date(1988, 11, 3), "Пыльца"),
        listOf("Белова", "Анастасия", "Павловна", "Женский", "+79102000003", date(1995, 2, 21), "Латекс"),
        listOf("Григорьева", "Полина", "Сергеевна", "Женский", "+79102000004", date(1999, 8, 30), "Нет"),
        listOf("Тихонов", "Роман", "Олегович", "Мужской", "+79102000005", date(1985, 12, 9), "Нет"),
        listOf("Захарова", "Виктория", "Андреевна", "Женский", "+79102000006", date(1990, 7, 17), "Пенициллин"),
        listOf("Крылов", "Антон", "Дмитриевич", "Мужской", "+79102000007", date(1993, 3, 28), "Нет"),
        listOf("Мельникова", "Алина", "Романовна", "Женский", "+79102000008", date(2001, 9, 12), "Никель"),
        listOf("Данилова", "Екатерина", "Викторовна", "Женский", "+79102000009", date(1987, 1, 25), "Нет"),
        listOf("Ларионова", "Юлия", "Михайловна", "Женский", "+79102000010", date(1996, 6, 6), "Цитрусовые")
    )
    clients.forEachIndexed { i, c ->
        db.execSQL(
            "INSERT INTO clients(id, lastName, name, middleName, gender, phoneNumber, dateOfBirth, allergies) VALUES (?,?,?,?,?,?,?,?)",
            arrayOf(i + 1, c[0], c[1], c[2], c[3], c[4], c[5], c[6])
        )
    }

    // клиент, мастер, услуга (мастер i оказывает услугу i), смещение в днях, час, минуты, статус
    val appts = listOf(
        listOf(1, 1, 1, 0, 10, 0, "Ожидание"), listOf(2, 2, 2, 0, 12, 30, "Ожидание"),
        listOf(3, 3, 3, 1, 11, 0, "Ожидание"), listOf(4, 4, 4, 1, 15, 30, "Ожидание"),
        listOf(5, 5, 5, 2, 10, 30, "Ожидание"), listOf(6, 6, 6, -1, 14, 0, "Выполнено"),
        listOf(7, 7, 7, -1, 16, 0, "Выполнено"), listOf(8, 8, 8, -2, 13, 0, "Выполнено"),
        listOf(9, 9, 9, -3, 17, 30, "Отменено"), listOf(10, 10, 10, 3, 18, 0, "Ожидание")
    )
    appts.forEachIndexed { i, a ->
        val whenMs = today + (a[3] as Int) * day + (a[4] as Int) * 3_600_000L + (a[5] as Int) * 60_000L
        db.execSQL(
            "INSERT INTO appointments(id, idClient, idMaster, idService, dateTime, status) VALUES (?,?,?,?,?,?)",
            arrayOf(i + 1, a[0], a[1], a[2], whenMs, a[6])
        )
    }
}
