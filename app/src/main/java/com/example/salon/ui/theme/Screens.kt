@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.salon.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.salon.data.*

private val categoryIcons = listOf(
    Icons.Default.ContentCut, Icons.Default.Brush, Icons.Default.Spa, Icons.Default.Face, Icons.Default.Healing,
    Icons.Default.Favorite, Icons.Default.Star, Icons.Default.Palette
)

// ======================= ПРИВЕТСТВИЕ =======================
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color.White)) {
        Box(
            Modifier.weight(1.2f).fillMaxWidth().clip(RoundedCornerShape(bottomEnd = 56.dp)).background(HeaderGradient).statusBarsPadding(),
            Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(Color.White.copy(alpha = 0.08f), size.width * 0.45f, Offset(size.width * 0.95f, size.height * 0.1f))
                drawCircle(Color.White.copy(alpha = 0.12f), size.width * 0.25f, Offset(size.width * 0.05f, size.height * 0.95f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(120.dp).clip(CircleShape).background(Color.White), Alignment.Center) {
                    Icon(Icons.Default.Spa, null, tint = Plum, modifier = Modifier.size(64.dp))
                }
                Spacer(Modifier.height(22.dp))
                Text("Aura Studio", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Text("салон красоты и медицинский центр", color = Color.White.copy(alpha = 0.88f), fontSize = 14.sp)
            }
        }
        Column(
            Modifier.weight(0.8f).fillMaxWidth().navigationBarsPadding().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Text("Клиенты, мастера, услуги и записи в одном приложении", color = Muted, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            PrimaryButton("Начать работу", onStart)
        }
    }
}

// ======================= ГЛАВНАЯ =======================
@Composable
fun HomeScreen(vm: MainViewModel, nav: NavController) {
    val appts by vm.appointments.collectAsState()
    val clients by vm.clients.collectAsState()
    val masters by vm.masters.collectAsState()
    val services by vm.services.collectAsState()
    val specs by vm.specializations.collectAsState()
    val types by vm.serviceTypes.collectAsState()
    val todayStart = remember { dayStart(System.currentTimeMillis()) }
    val upcoming = remember(appts) { appts.filter { it.status == "Ожидание" && it.dateTime >= todayStart }.take(4) }
    val todayCount = remember(appts) { appts.count { dayStart(it.dateTime) == todayStart && it.status != "Отменено" } }
    val income = remember(appts) { appts.filter { it.status == "Выполнено" }.sumOf { it.price } }

    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 36.dp)).background(HeaderGradient)
                .statusBarsPadding().padding(20.dp, 12.dp, 20.dp, 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Spa, null, tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Aura Studio", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Box(Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).clickable { nav.navigate("appointments") }, Alignment.Center) {
                    Icon(Icons.Default.Notifications, "Записи", tint = Color.White)
                    if (todayCount > 0) Box(Modifier.align(Alignment.TopEnd).size(16.dp).clip(CircleShape).background(Coral), Alignment.Center) {
                        Text("$todayCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color.White).padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Красота и забота", color = Plum, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Сегодня записей: $todayCount", color = Muted, fontSize = 13.sp)
                    Text("Выручка: ${money(income)}", color = Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    SmallPill("Все записи") { nav.navigate("appointments") }
                }
                Box(Modifier.size(84.dp).clip(CircleShape).background(Blush), Alignment.Center) {
                    Icon(Icons.Default.Spa, null, tint = Plum, modifier = Modifier.size(44.dp))
                }
            }
        }

        LazyRow(contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 8.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            itemsIndexed(types, key = { _, t -> t.id }) { i, t -> CategoryCircle(categoryIcons[i % categoryIcons.size], t.title) { nav.navigate("service_types") } }
        }

        Row(Modifier.fillMaxWidth().padding(20.dp, 14.dp, 20.dp, 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Ближайшие записи", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Все", color = Plum, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { nav.navigate("appointments") })
        }
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (upcoming.isEmpty()) Text("Предстоящих записей нет", color = Muted)
            upcoming.forEach { a ->
                AppCard(onClick = { nav.navigate("appointment_edit/${a.id}") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconCircle(Icons.Default.Spa, 48.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(a.serviceTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${a.clientName}, мастер ${a.masterName}", color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${fmtDay(a.dateTime)}, ${fmtTime(a.dateTime)}", color = Plum, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        SmallPill("Открыть") { nav.navigate("appointment_edit/${a.id}") }
                    }
                }
            }
        }

        Text("Разделы", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp, 22.dp, 20.dp, 10.dp))
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("Мастера", masters.size, Icons.Default.ContentCut, Modifier.weight(1f)) { nav.navigate("masters") }
            StatTile("Услуги", services.size, Icons.Default.Spa, Modifier.weight(1f)) { nav.navigate("services") }
        }
        Row(Modifier.padding(20.dp, 12.dp, 20.dp, 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("Типы услуг", types.size, Icons.Default.Category, Modifier.weight(1f)) { nav.navigate("service_types") }
            StatTile("Специализации", specs.size, Icons.Default.Badge, Modifier.weight(1f)) { nav.navigate("specializations") }
        }
    }
}

@Composable
private fun StatTile(title: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) =
    AppCard(modifier, onClick = onClick) {
        IconCircle(icon, 40.dp)
        Spacer(Modifier.height(10.dp))
        Text("$count", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(title, color = Muted, fontSize = 13.sp)
    }

// ======================= ЗАПИСИ (как «история заказов») =======================
@Composable
fun AppointmentsScreen(vm: MainViewModel, nav: NavController) {
    val all by vm.appointments.collectAsState()
    var q by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    val list = remember(all, q, status) {
        all.filter { a ->
            (status == null || a.status == status) &&
                (q.isBlank() || "${a.clientName} ${a.masterName} ${a.serviceTitle}".contains(q.trim(), ignoreCase = true))
        }
    }
    AppScreen("Записи", "${all.size} в базе", { nav.popBackStack() }, "Новая запись" to { nav.navigate("appointment_edit/0") }) {
        SearchBar(q, { q = it }, "Клиент, мастер или услуга")
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(20.dp, 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterPill("Все", status == null) { status = null }
            Statuses.forEach { s -> FilterPill(s, status == s) { status = s } }
        }
        ListBody(list, { it.id }, if (all.isEmpty()) "Записей пока нет" else "Ничего не найдено") { a ->
            AppCard(Modifier.animateItem(), onClick = { nav.navigate("appointment_edit/${a.id}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconCircle(Icons.Default.Spa, 52.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(a.serviceTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Клиент: ${a.clientName}", color = Muted, fontSize = 12.sp)
                        Text("Мастер: ${a.masterName}", color = Muted, fontSize = 12.sp)
                        Text("${fmtDate(a.dateTime)}  |  ${fmtTime(a.dateTime)}", color = Plum, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        StatusPill(a.status)
                        Spacer(Modifier.height(8.dp))
                        Text(money(a.price), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton({ pending = { vm.deleteAppointment(a.id) } }, Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Удалить", tint = Coral) }
                }
            }
        }
    }
    pending?.let { action -> ConfirmDialog("Удалить запись?", { action(); pending = null }, { pending = null }) }
}

// ======================= КЛИЕНТЫ =======================
@Composable
fun ClientsScreen(vm: MainViewModel, nav: NavController) {
    val all by vm.clients.collectAsState()
    var q by remember { mutableStateOf("") }
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    val list = remember(all, q) {
        all.filter { q.isBlank() || "${it.lastName} ${it.name} ${it.middleName} ${it.phoneNumber}".contains(q.trim(), ignoreCase = true) }
    }
    AppScreen("Клиенты", "${all.size} в базе", { nav.popBackStack() }, "Новый клиент" to { nav.navigate("client_edit/0") }) {
        SearchBar(q, { q = it }, "Имя или телефон")
        ListBody(list, { it.id }, if (all.isEmpty()) "Клиентов пока нет" else "Ничего не найдено") { c ->
            AppCard(Modifier.animateItem(), onClick = { nav.navigate("client_edit/${c.id}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar("${c.lastName} ${c.name}", 54.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${c.lastName} ${c.name} ${c.middleName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${c.gender}, ${ageOf(c.dateOfBirth)} лет", color = Muted, fontSize = 12.sp)
                        Text(c.phoneNumber, color = Plum, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        if (c.allergies.isNotBlank() && c.allergies != "Нет") {
                            Spacer(Modifier.height(6.dp))
                            TagPill("Аллергия: ${c.allergies}", Amber)
                        }
                    }
                    IconButton({ pending = { vm.deleteClient(c) } }) { Icon(Icons.Default.Delete, "Удалить", tint = Coral) }
                }
            }
        }
    }
    pending?.let { action -> ConfirmDialog("Удалить клиента? Его записи на приём тоже будут удалены.", { action(); pending = null }, { pending = null }) }
}

// ======================= МАСТЕРА =======================
@Composable
fun MastersScreen(vm: MainViewModel, nav: NavController) {
    val all by vm.masters.collectAsState()
    var q by remember { mutableStateOf("") }
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    val list = remember(all, q) {
        all.filter { m -> q.isBlank() || "${m.master.lastName} ${m.master.name} ${m.specTitle}".contains(q.trim(), ignoreCase = true) }
    }
    AppScreen("Мастера", "${all.size} в штате", { nav.popBackStack() }, "Новый мастер" to { nav.navigate("master_edit/0") }) {
        SearchBar(q, { q = it }, "Имя или специализация")
        ListBody(list, { it.master.id }, if (all.isEmpty()) "Мастеров пока нет" else "Ничего не найдено") { item ->
            val m = item.master
            AppCard(Modifier.animateItem(), onClick = { nav.navigate("master_edit/${m.id}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar("${m.lastName} ${m.name}", 54.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${m.lastName} ${m.name} ${m.middleName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(item.specTitle, color = Plum, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Стаж ${m.experience} лет, с ${fmtDate(m.dateOfEmployment)}", color = Muted, fontSize = 12.sp)
                        Text(m.phoneNumber, color = Muted, fontSize = 12.sp)
                    }
                    IconButton({ pending = { vm.deleteMaster(m) } }) { Icon(Icons.Default.Delete, "Удалить", tint = Coral) }
                }
            }
        }
    }
    pending?.let { action -> ConfirmDialog("Удалить мастера? Его записи на приём тоже будут удалены.", { action(); pending = null }, { pending = null }) }
}

// ======================= УСЛУГИ =======================
@Composable
fun ServicesScreen(vm: MainViewModel, nav: NavController) {
    val all by vm.services.collectAsState()
    var q by remember { mutableStateOf("") }
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    val list = remember(all, q) { all.filter { s -> q.isBlank() || "${s.service.title} ${s.typeTitle}".contains(q.trim(), ignoreCase = true) } }
    AppScreen("Услуги", "${all.size} в прайсе", { nav.popBackStack() }, "Новая услуга" to { nav.navigate("service_edit/0") }) {
        SearchBar(q, { q = it }, "Название или тип услуги")
        ListBody(list, { it.service.id }, if (all.isEmpty()) "Услуг пока нет" else "Ничего не найдено") { item ->
            val s = item.service
            AppCard(Modifier.animateItem(), onClick = { nav.navigate("service_edit/${s.id}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconCircle(categoryIcons[s.idServiceType % categoryIcons.size], 52.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        TagPill(item.typeTitle)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("от", color = Muted, fontSize = 10.sp)
                        Text(money(s.price), color = Plum, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        IconButton({ pending = { vm.deleteService(s) } }, Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Удалить", tint = Coral) }
                    }
                }
            }
        }
    }
    pending?.let { action -> ConfirmDialog("Удалить услугу? Записи на неё тоже будут удалены.", { action(); pending = null }, { pending = null }) }
}

// ======================= СПЕЦИАЛИЗАЦИИ =======================
@Composable
fun SpecializationsScreen(vm: MainViewModel, nav: NavController) {
    val all by vm.specializations.collectAsState()
    val masters by vm.masters.collectAsState()
    var q by remember { mutableStateOf("") }
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    val list = remember(all, q) { all.filter { q.isBlank() || it.title.contains(q.trim(), ignoreCase = true) } }
    AppScreen("Специализации", "${all.size} в справочнике", { nav.popBackStack() }, "Новая специализация" to { nav.navigate("specialization_edit/0") }) {
        SearchBar(q, { q = it }, "Название специализации")
        ListBody(list, { it.id }, if (all.isEmpty()) "Специализаций пока нет" else "Ничего не найдено") { s ->
            val count = masters.count { it.master.idSpecialization == s.id }
            AppCard(Modifier.animateItem(), onClick = { nav.navigate("specialization_edit/${s.id}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconCircle(Icons.Default.Badge, 50.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Оклад ${money(s.salary)}", color = Plum, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        TagPill("Мастеров: $count", Violet)
                    }
                    IconButton({ pending = { vm.deleteSpecialization(s) } }) { Icon(Icons.Default.Delete, "Удалить", tint = Coral) }
                }
            }
        }
    }
    pending?.let { action ->
        ConfirmDialog("Удалить специализацию? Все мастера с ней и их записи на приём тоже будут удалены.", { action(); pending = null }, { pending = null })
    }
}

// ======================= ТИПЫ УСЛУГ =======================
@Composable
fun ServiceTypesScreen(vm: MainViewModel, nav: NavController) {
    val all by vm.serviceTypes.collectAsState()
    val services by vm.services.collectAsState()
    var q by remember { mutableStateOf("") }
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    val list = remember(all, q) { all.filter { q.isBlank() || it.title.contains(q.trim(), ignoreCase = true) } }
    AppScreen("Типы услуг", "${all.size} в справочнике", { nav.popBackStack() }, "Новый тип" to { nav.navigate("service_type_edit/0") }) {
        SearchBar(q, { q = it }, "Название типа услуг")
        ListBody(list, { it.id }, if (all.isEmpty()) "Типов пока нет" else "Ничего не найдено") { type ->
            val count = services.count { it.service.idServiceType == type.id }
            AppCard(Modifier.animateItem(), onClick = { nav.navigate("service_type_edit/${type.id}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconCircle(categoryIcons[type.id % categoryIcons.size], 50.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(type.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        TagPill("Услуг: $count", Violet)
                    }
                    IconButton({ pending = { vm.deleteServiceType(type) } }) { Icon(Icons.Default.Delete, "Удалить", tint = Coral) }
                }
            }
        }
    }
    pending?.let { action ->
        ConfirmDialog("Удалить тип услуг? Все услуги этого типа и записи на них тоже будут удалены.", { action(); pending = null }, { pending = null })
    }
}
