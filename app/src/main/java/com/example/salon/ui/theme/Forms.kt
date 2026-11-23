@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.salon.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.salon.data.*

private const val REQ = "Обязательное поле"
private val Genders = listOf("Женский", "Мужской")
private val TimeSlots = (9..20).flatMap { h -> listOf(0, 30).map { m -> "%02d:%02d".format(h, m) } }
private fun String.phoneOk() = count { it.isDigit() } >= 10
private fun String.toMoney() = replace(',', '.').toDoubleOrNull()?.takeIf { it >= 0 }

@Composable
private fun FormScreen(title: String, nav: NavController, content: @Composable ColumnScope.() -> Unit) =
    AppScreen(title, onBack = { nav.popBackStack() }) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().navigationBarsPadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp), content = content
        )
    }

// ======================= КЛИЕНТ =======================
@Composable
fun ClientFormScreen(vm: MainViewModel, nav: NavController, id: Int) {
    var last by remember { mutableStateOf("") }; var name by remember { mutableStateOf("") }
    var middle by remember { mutableStateOf("") }; var gender by remember { mutableStateOf<Int?>(null) }
    var phone by remember { mutableStateOf("") }; var dob by remember { mutableStateOf<Long?>(null) }
    var allergies by remember { mutableStateOf("") }; var tried by remember { mutableStateOf(false) }
    LaunchedEffect(id) {
        if (id != 0) vm.getClient(id)?.let {
            last = it.lastName; name = it.name; middle = it.middleName; gender = Genders.indexOf(it.gender).takeIf { i -> i >= 0 }
            phone = it.phoneNumber; dob = it.dateOfBirth; allergies = it.allergies
        }
    }
    FormScreen(if (id == 0) "Новый клиент" else "Клиент", nav) {
        Field(last, { last = it }, "Фамилия", if (tried && last.isBlank()) REQ else null)
        Field(name, { name = it }, "Имя", if (tried && name.isBlank()) REQ else null)
        Field(middle, { middle = it }, "Отчество", null)
        Dropdown("Пол", Genders.mapIndexed { i, g -> i to g }, gender, if (tried && gender == null) "Выберите пол" else null) { gender = it }
        Field(phone, { phone = it }, "Телефон", if (tried && !phone.phoneOk()) "Минимум 10 цифр" else null, KeyboardType.Phone)
        DateField("Дата рождения", dob, if (tried && dob == null) REQ else null) { dob = it }
        Field(allergies, { allergies = it }, "Аллергии (если нет — оставьте пустым)", null)
        PrimaryButton("Сохранить") {
            tried = true
            if (last.isNotBlank() && name.isNotBlank() && gender != null && phone.phoneOk() && dob != null) {
                vm.saveClient(Client(id, last.trim(), name.trim(), middle.trim(), Genders[gender!!], phone.trim(), dob!!, allergies.trim().ifBlank { "Нет" }))
                nav.popBackStack()
            }
        }
    }
}

// ======================= МАСТЕР =======================
@Composable
fun MasterFormScreen(vm: MainViewModel, nav: NavController, id: Int) {
    val specs by vm.specializations.collectAsState()
    var last by remember { mutableStateOf("") }; var name by remember { mutableStateOf("") }
    var middle by remember { mutableStateOf("") }; var phone by remember { mutableStateOf("") }
    var hired by remember { mutableStateOf<Long?>(null) }; var exp by remember { mutableStateOf("") }
    var specId by remember { mutableStateOf<Int?>(null) }; var posId by remember { mutableStateOf("1") }
    var tried by remember { mutableStateOf(false) }
    LaunchedEffect(id) {
        if (id != 0) vm.getMaster(id)?.let {
            last = it.lastName; name = it.name; middle = it.middleName; phone = it.phoneNumber; hired = it.dateOfEmployment
            exp = it.experience.toString(); specId = it.idSpecialization; posId = it.idPosition.toString()
        }
    }
    val expVal = exp.toIntOrNull()?.takeIf { it in 0..60 }
    val posVal = posId.toIntOrNull()?.takeIf { it >= 0 }
    FormScreen(if (id == 0) "Новый мастер" else "Мастер", nav) {
        Field(last, { last = it }, "Фамилия", if (tried && last.isBlank()) REQ else null)
        Field(name, { name = it }, "Имя", if (tried && name.isBlank()) REQ else null)
        Field(middle, { middle = it }, "Отчество", null)
        Field(phone, { phone = it }, "Телефон", if (tried && !phone.phoneOk()) "Минимум 10 цифр" else null, KeyboardType.Phone)
        DateField("Дата приёма на работу", hired, if (tried && hired == null) REQ else null) { hired = it }
        Field(exp, { exp = it }, "Стаж (лет)", if (tried && expVal == null) "Целое число 0–60" else null, KeyboardType.Number)
        Dropdown("Специализация", specs.map { it.id to it.title }, specId, if (tried && specId == null) "Выберите специализацию" else null) { specId = it }
        Field(posId, { posId = it }, "ID должности", if (tried && posVal == null) "Целое число ≥ 0" else null, KeyboardType.Number)
        PrimaryButton("Сохранить") {
            tried = true
            if (last.isNotBlank() && name.isNotBlank() && phone.phoneOk() && hired != null && expVal != null && specId != null && posVal != null) {
                vm.saveMaster(Master(id, last.trim(), name.trim(), middle.trim(), phone.trim(), hired!!, expVal, specId!!, posVal))
                nav.popBackStack()
            }
        }
    }
}

// ======================= УСЛУГА =======================
@Composable
fun ServiceFormScreen(vm: MainViewModel, nav: NavController, id: Int) {
    val types by vm.serviceTypes.collectAsState()
    var typeId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }; var price by remember { mutableStateOf("") }
    var tried by remember { mutableStateOf(false) }
    LaunchedEffect(id) {
        if (id != 0) vm.getService(id)?.let { typeId = it.idServiceType; title = it.title; price = it.price.toString() }
    }
    val priceVal = price.toMoney()
    FormScreen(if (id == 0) "Новая услуга" else "Услуга", nav) {
        Dropdown("Тип услуги", types.map { it.id to it.title }, typeId, if (tried && typeId == null) "Выберите тип" else null) { typeId = it }
        Field(title, { title = it }, "Название", if (tried && title.isBlank()) REQ else null)
        Field(price, { price = it }, "Цена, ₽", if (tried && priceVal == null) "Введите число ≥ 0" else null, KeyboardType.Decimal)
        PrimaryButton("Сохранить") {
            tried = true
            if (typeId != null && title.isNotBlank() && priceVal != null) {
                vm.saveService(Service(id, typeId!!, title.trim(), priceVal))
                nav.popBackStack()
            }
        }
    }
}

// ======================= ЗАПИСЬ =======================
@Composable
fun AppointmentFormScreen(vm: MainViewModel, nav: NavController, id: Int) {
    val clients by vm.clients.collectAsState()
    val masters by vm.masters.collectAsState()
    val services by vm.services.collectAsState()
    var clientId by remember { mutableStateOf<Int?>(null) }
    var masterId by remember { mutableStateOf<Int?>(null) }
    var serviceId by remember { mutableStateOf<Int?>(null) }
    var day by remember { mutableStateOf<Long?>(null) }
    var slot by remember { mutableStateOf<Int?>(null) }
    var status by remember { mutableStateOf<Int?>(0) }
    var tried by remember { mutableStateOf(false) }
    LaunchedEffect(id) {
        if (id != 0) vm.getAppointment(id)?.let {
            clientId = it.idClient; masterId = it.idMaster; serviceId = it.idService
            day = dayStart(it.dateTime); slot = TimeSlots.indexOf(fmtTime(it.dateTime)).takeIf { i -> i >= 0 }
            status = Statuses.indexOf(it.status).coerceAtLeast(0)
        }
    }
    FormScreen(if (id == 0) "Новая запись" else "Запись", nav) {
        Dropdown("Клиент", clients.map { it.id to "${it.lastName} ${it.name}" }, clientId, if (tried && clientId == null) "Выберите клиента" else null) { clientId = it }
        SectionLabel("Выберите мастера", if (tried && masterId == null) "Выберите мастера" else null)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(masters, key = { it.master.id }) { m ->
                PersonCard("${m.master.name} ${m.master.lastName.take(1)}.", m.specTitle, masterId == m.master.id) { masterId = m.master.id }
            }
        }
        SectionLabel("Выберите услугу", if (tried && serviceId == null) "Выберите услугу" else null)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(services, key = { it.service.id }) { s ->
                ServicePickCard(s.service.title, s.service.price, serviceId == s.service.id) { serviceId = s.service.id }
            }
        }
        DateField("Дата приёма", day, if (tried && day == null) REQ else null) { day = it }
        SectionLabel("Доступное время", if (tried && slot == null) "Выберите время" else null)
        TimeSlots.chunked(3).forEachIndexed { r, row ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEachIndexed { c, t -> SlotChip(t, slot == r * 3 + c, Modifier.weight(1f)) { slot = r * 3 + c } }
            }
        }
        Spacer(Modifier.height(6.dp))
        Dropdown("Статус", Statuses.mapIndexed { i, s -> i to s }, status, null) { status = it }
        PrimaryButton("Сохранить") {
            tried = true
            if (clientId != null && masterId != null && serviceId != null && day != null && slot != null) {
                val (h, m) = TimeSlots[slot!!].split(":").map { it.toInt() }
                val whenMs = day!! + h * 3_600_000L + m * 60_000L
                vm.saveAppointment(Appointment(id, clientId!!, masterId!!, serviceId!!, whenMs, Statuses[status ?: 0]))
                nav.popBackStack()
            }
        }
    }
}

// ======================= СПЕЦИАЛИЗАЦИЯ =======================
@Composable
fun SpecializationFormScreen(vm: MainViewModel, nav: NavController, id: Int) {
    var title by remember { mutableStateOf("") }; var salary by remember { mutableStateOf("") }
    var tried by remember { mutableStateOf(false) }
    LaunchedEffect(id) { if (id != 0) vm.getSpecialization(id)?.let { title = it.title; salary = it.salary.toString() } }
    val salaryVal = salary.toMoney()
    FormScreen(if (id == 0) "Новая специализация" else "Специализация", nav) {
        Field(title, { title = it }, "Название", if (tried && title.isBlank()) REQ else null)
        Field(salary, { salary = it }, "Оклад, ₽", if (tried && salaryVal == null) "Введите число ≥ 0" else null, KeyboardType.Decimal)
        PrimaryButton("Сохранить") {
            tried = true
            if (title.isNotBlank() && salaryVal != null) {
                vm.saveSpecialization(Specialization(id, title.trim(), salaryVal))
                nav.popBackStack()
            }
        }
    }
}

// ======================= ТИП УСЛУГ =======================
@Composable
fun ServiceTypeFormScreen(vm: MainViewModel, nav: NavController, id: Int) {
    var title by remember { mutableStateOf("") }
    var tried by remember { mutableStateOf(false) }
    LaunchedEffect(id) { if (id != 0) vm.getServiceType(id)?.let { title = it.title } }
    FormScreen(if (id == 0) "Новый тип услуг" else "Тип услуг", nav) {
        Field(title, { title = it }, "Название", if (tried && title.isBlank()) REQ else null)
        PrimaryButton("Сохранить") {
            tried = true
            if (title.isNotBlank()) {
                vm.saveServiceType(ServiceType(id, title.trim()))
                nav.popBackStack()
            }
        }
    }
}
