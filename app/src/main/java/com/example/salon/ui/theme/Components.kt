@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.salon.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// ---------- форматирование и даты ----------
private val ru = Locale("ru")
fun fmtDate(ms: Long): String = SimpleDateFormat("d MMM yyyy", ru).format(Date(ms))
fun fmtDay(ms: Long): String = SimpleDateFormat("EEE, d MMM", ru).format(Date(ms))
fun fmtTime(ms: Long): String = SimpleDateFormat("HH:mm", ru).format(Date(ms))
fun money(v: Double): String = String.format(ru, "%,.0f ₽", v)
fun initialsOf(fullName: String): String {
    val p = fullName.trim().split(" ")
    return "${p.getOrNull(0)?.firstOrNull() ?: ' '}${p.getOrNull(1)?.firstOrNull() ?: ' '}".uppercase()
}
fun dayStart(ms: Long): Long = Calendar.getInstance().apply {
    timeInMillis = ms; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis
/** DatePicker работает в UTC, а в БД даты хранятся по локальному времени. */
fun pickerToLocal(utcMs: Long): Long {
    val u = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMs }
    return Calendar.getInstance().apply { clear(); set(u.get(Calendar.YEAR), u.get(Calendar.MONTH), u.get(Calendar.DAY_OF_MONTH)) }.timeInMillis
}
fun localToPicker(ms: Long): Long {
    val l = Calendar.getInstance().apply { timeInMillis = ms }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { clear(); set(l.get(Calendar.YEAR), l.get(Calendar.MONTH), l.get(Calendar.DAY_OF_MONTH)) }.timeInMillis
}
fun ageOf(dobMs: Long): Int {
    val b = Calendar.getInstance().apply { timeInMillis = dobMs }
    val n = Calendar.getInstance()
    var a = n.get(Calendar.YEAR) - b.get(Calendar.YEAR)
    if (n.get(Calendar.DAY_OF_YEAR) < b.get(Calendar.DAY_OF_YEAR)) a--
    return a
}

val Statuses = listOf("Ожидание", "Выполнено", "Отменено")
fun statusColor(s: String) = when (s) { "Ожидание" -> Plum; "Выполнено" -> Green; "Отменено" -> Coral; else -> Muted }
fun statusBg(s: String) = when (s) { "Ожидание" -> Color(0xFFF3E0FA); "Выполнено" -> Color(0xFFDDF6D8); "Отменено" -> Color(0xFFFFE0E0); else -> Petal }

private val avatarColors = listOf(Plum, Magenta, Orchid, Violet, Amber, Green)
private fun avatarColor(seed: String) = avatarColors[abs(seed.hashCode()) % avatarColors.size]

// ---------- каркас экрана: градиентная шапка с заголовком по центру ----------
@Composable
fun AppScreen(
    title: String, subtitle: String? = null, onBack: (() -> Unit)? = null,
    fab: Pair<String, () -> Unit>? = null, content: @Composable ColumnScope.() -> Unit
) = Box(Modifier.fillMaxSize().background(Color.White)) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 36.dp)).background(HeaderGradient)
                .statusBarsPadding().padding(8.dp, 8.dp, 8.dp, 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.White) }
                else Spacer(Modifier.width(48.dp))
                Text(title.uppercase(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }
            if (subtitle != null) Text(subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Column(Modifier.weight(1f).fillMaxWidth(), content = content)
    }
    if (fab != null) Row(
        Modifier.align(Alignment.BottomEnd).padding(20.dp).shadow(10.dp, CircleShape, ambientColor = Magenta, spotColor = Magenta)
            .clip(CircleShape).background(PillGradient).clickable(onClick = fab.second).padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Add, null, tint = Color.White); Spacer(Modifier.width(8.dp))
        Text(fab.first, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BottomBar(current: String?, onSelect: (String) -> Unit) {
    val items = listOf(
        "home" to Icons.Default.Home, "appointments" to Icons.Default.CalendarMonth, "clients" to Icons.Default.Groups,
        "masters" to Icons.Default.ContentCut, "services" to Icons.Default.Spa
    )
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Row(
        Modifier.fillMaxWidth().shadow(18.dp, shape, ambientColor = Magenta, spotColor = Magenta).clip(shape)
            .background(Color.White).navigationBarsPadding().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (route, icon) ->
            val sel = route == current
            Column(Modifier.weight(1f).clickable { onSelect(route) }.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, route, tint = if (sel) Plum else Muted.copy(alpha = 0.6f), modifier = Modifier.size(26.dp))
                Box(Modifier.padding(top = 4.dp).size(5.dp).clip(CircleShape).background(if (sel) Plum else Color.Transparent))
            }
        }
    }
}

// ---------- карточки, чипы, кнопки ----------
@Composable
fun AppCard(
    modifier: Modifier = Modifier, color: Color = Petal, onClick: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(16.dp), content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier.fillMaxWidth().shadow(4.dp, shape, ambientColor = Magenta.copy(alpha = 0.25f), spotColor = Magenta.copy(alpha = 0.25f))
            .clip(shape).background(color).let { if (onClick != null) it.clickable(onClick = onClick) else it }.padding(padding),
        content = content
    )
}

@Composable
fun Avatar(fullName: String, size: Dp = 48.dp) {
    val c = avatarColor(fullName)
    Box(Modifier.size(size).clip(CircleShape).background(c.copy(alpha = 0.15f)), Alignment.Center) {
        Text(initialsOf(fullName), color = c, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.34f).sp)
    }
}

@Composable
fun IconCircle(icon: ImageVector, size: Dp = 46.dp, tint: Color = Plum, bg: Color = Blush) =
    Box(Modifier.size(size).clip(CircleShape).background(bg), Alignment.Center) { Icon(icon, null, tint = tint, modifier = Modifier.size(size * 0.5f)) }

@Composable
fun StatusPill(status: String) =
    Text(status, color = statusColor(status), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusBg(status)).padding(horizontal = 12.dp, vertical = 4.dp))

@Composable
fun TagPill(text: String, color: Color = Plum) =
    Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.12f)).padding(horizontal = 10.dp, vertical = 4.dp))

@Composable
fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) = Text(
    text, color = if (selected) Color.White else Plum, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    modifier = Modifier.clip(CircleShape).background(if (selected) PillGradient else androidx.compose.ui.graphics.SolidColor(Petal))
        .clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 9.dp)
)

@Composable
fun SmallPill(label: String, onClick: () -> Unit) = Text(
    label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp,
    modifier = Modifier.clip(CircleShape).background(PillGradient).clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 8.dp)
)

@Composable
fun PrimaryButton(label: String, onClick: () -> Unit) = Box(
    Modifier.fillMaxWidth().padding(top = 12.dp).height(54.dp)
        .shadow(8.dp, CircleShape, ambientColor = Orchid, spotColor = Orchid).clip(CircleShape)
        .background(PillGradient).clickable(onClick = onClick), Alignment.Center
) { Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

@Composable
fun SlotChip(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier.height(40.dp).clip(shape).border(1.dp, if (selected) Plum else Line, shape)
            .background(if (selected) Plum else Color.White).clickable(onClick = onClick), Alignment.Center
    ) { Text(text, color = if (selected) Color.White else Ink, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
}

@Composable
fun SectionLabel(text: String, error: String? = null) = Column(Modifier.padding(top = 14.dp, bottom = 8.dp)) {
    Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    if (error != null) Text(error, color = Coral, fontSize = 12.sp)
}

@Composable
fun PersonCard(name: String, sub: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val c = avatarColor(name)
    Column(Modifier.width(92.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(84.dp).clip(shape).background(c.copy(alpha = 0.16f)).let { if (selected) it.border(2.dp, Plum, shape) else it },
            Alignment.Center
        ) { Text(initialsOf(name), color = c, fontWeight = FontWeight.Bold, fontSize = 26.sp) }
        Spacer(Modifier.height(6.dp))
        Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(sub, color = Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ServicePickCard(title: String, price: Double, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(Modifier.width(104.dp)) {
        Column(
            Modifier.fillMaxWidth().clip(shape).background(if (selected) Petal else Color.White)
                .border(if (selected) 1.5.dp else 1.dp, if (selected) Plum else Line, shape).clickable(onClick = onClick).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconCircle(Icons.Default.Spa, 38.dp)
            Spacer(Modifier.height(6.dp))
            Text(title, fontSize = 11.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.height(30.dp))
            Text("от ${money(price)}", color = Muted, fontSize = 10.sp)
        }
        if (selected) Box(Modifier.align(Alignment.TopEnd).padding(6.dp).size(18.dp).clip(CircleShape).background(Plum), Alignment.Center) {
            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
fun CategoryCircle(icon: ImageVector, label: String, onClick: () -> Unit) =
    Column(Modifier.width(74.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(64.dp).shadow(6.dp, CircleShape, ambientColor = Magenta.copy(alpha = 0.3f), spotColor = Magenta.copy(alpha = 0.3f))
            .clip(CircleShape).background(Color.White), Alignment.Center) { Icon(icon, null, tint = Plum, modifier = Modifier.size(30.dp)) }
        Spacer(Modifier.height(6.dp))
        Text(label, color = Plum, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }

// ---------- поля ввода ----------
@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Plum, unfocusedBorderColor = Line,
    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
    focusedLabelColor = Plum, unfocusedLabelColor = Muted, cursorColor = Plum,
    errorBorderColor = Coral, errorLabelColor = Coral, errorSupportingTextColor = Coral, errorContainerColor = Color.White
)

@Composable
fun SearchBar(value: String, onChange: (String) -> Unit, hint: String) = OutlinedTextField(
    value, onChange, placeholder = { Text(hint, color = Muted) }, singleLine = true,
    leadingIcon = { Icon(Icons.Default.Search, null, tint = Plum) },
    trailingIcon = { if (value.isNotEmpty()) IconButton({ onChange("") }) { Icon(Icons.Default.Close, "Очистить") } },
    shape = CircleShape, colors = fieldColors(), modifier = Modifier.fillMaxWidth().padding(20.dp, 14.dp, 20.dp, 4.dp)
)

@Composable
fun Field(value: String, onChange: (String) -> Unit, label: String, error: String?, keyboard: KeyboardType = KeyboardType.Text) =
    OutlinedTextField(
        value, onChange, label = { Text(label) }, isError = error != null, singleLine = true,
        supportingText = { error?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        shape = RoundedCornerShape(16.dp), colors = fieldColors(), modifier = Modifier.fillMaxWidth()
    )

@Composable
fun Dropdown(label: String, options: List<Pair<Int, String>>, selectedId: Int?, error: String?, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }) {
        OutlinedTextField(
            value = options.firstOrNull { it.first == selectedId }?.second ?: "", onValueChange = {}, readOnly = true,
            label = { Text(label) }, isError = error != null, supportingText = { error?.let { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(16.dp), colors = fieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded, { expanded = false }) {
            options.forEach { DropdownMenuItem({ Text(it.second) }, { onSelect(it.first); expanded = false }) }
        }
    }
}

@Composable
fun DateField(label: String, millis: Long?, error: String?, onPick: (Long) -> Unit) {
    var show by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = millis?.let { fmtDate(it) } ?: "", onValueChange = {}, readOnly = true, label = { Text(label) },
            isError = error != null, supportingText = { error?.let { Text(it) } },
            trailingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Plum) },
            shape = RoundedCornerShape(16.dp), colors = fieldColors(), modifier = Modifier.fillMaxWidth()
        )
        Box(Modifier.matchParentSize().clickable { show = true })
    }
    if (show) {
        val st = rememberDatePickerState(initialSelectedDateMillis = millis?.let(::localToPicker))
        DatePickerDialog(
            onDismissRequest = { show = false },
            confirmButton = { TextButton({ st.selectedDateMillis?.let { onPick(pickerToLocal(it)) }; show = false }) { Text("Готово") } },
            dismissButton = { TextButton({ show = false }) { Text("Отмена") } }
        ) { DatePicker(st) }
    }
}

@Composable
fun ConfirmDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    onDismissRequest = onDismiss, shape = RoundedCornerShape(24.dp), containerColor = Color.White,
    title = { Text("Удаление", fontWeight = FontWeight.Bold) }, text = { Text(text, color = Muted) },
    confirmButton = { TextButton(onConfirm) { Text("Удалить", color = Coral, fontWeight = FontWeight.Bold) } },
    dismissButton = { TextButton(onDismiss) { Text("Отмена") } }
)

@Composable
fun <T> ListBody(data: List<T>, key: (T) -> Any, empty: String, row: @Composable LazyItemScope.(T) -> Unit) {
    if (data.isEmpty()) Box(Modifier.fillMaxSize().padding(32.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(empty, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("Измените запрос или добавьте запись кнопкой внизу", color = Muted, fontSize = 14.sp)
        }
    } else LazyColumn(
        contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 100.dp), verticalArrangement = Arrangement.spacedBy(14.dp)
    ) { items(data, key = key) { row(this, it) } }
}
