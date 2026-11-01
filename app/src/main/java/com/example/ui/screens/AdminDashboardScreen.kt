package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.AdminTab
import com.example.viewmodel.IhwViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: IhwViewModel,
    onLogout: () -> Unit
) {
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()
    val activeTab by viewModel.adminTab.collectAsState()

    BackHandler {
        onLogout()
    }

    if (!isAuthenticated) {
        AdminPinScreen(
            onVerify = { pin -> viewModel.verifyAdminPin(pin) },
            onCancel = onLogout
        )
    } else {
        Scaffold(
            topBar = {
                AdminTopBar(onLogout = onLogout)
            },
            bottomBar = {
                AdminBottomNav(
                    activeTab = activeTab,
                    onTabSelect = { viewModel.setAdminTab(it) }
                )
            },
            containerColor = Color(0xFFF8FAFC)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (activeTab) {
                    AdminTab.TODAY -> AdminTodayTab(viewModel = viewModel)
                    AdminTab.CALENDAR -> AdminCalendarTab(viewModel = viewModel)
                    AdminTab.BOOKINGS -> AdminBookingsTab(viewModel = viewModel)
                    AdminTab.SETTINGS -> AdminSettingsTab(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun AdminPinScreen(
    onVerify: (String) -> Boolean,
    onCancel: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IhwDarkMaroon),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(IhwCreamBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = IhwDarkMaroon,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Studio Admin Portal",
                    color = IhwDarkMaroon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Enter 4-digit PIN (Default: 1234)",
                    color = IhwMutedText,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                            pin = it
                            hasError = false
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("4-Digit PIN") },
                    isError = hasError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (hasError) {
                    Text(
                        text = "Invalid PIN. Please try again.",
                        color = Color(0xFFDC2626),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val ok = onVerify(pin)
                        if (!ok) hasError = true
                    },
                    enabled = pin.length == 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("admin_login_submit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Unlock Dashboard", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onCancel) {
                    Text("Return to App", color = IhwMutedText)
                }
            }
        }
    }
}

@Composable
private fun AdminTopBar(onLogout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = IhwDarkMaroon,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "IHW Studio Manager",
                    color = IhwGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Sodepur Branch Admin • 11 AM - 7 PM",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = onLogout, modifier = Modifier.testTag("admin_logout_btn")) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun AdminBottomNav(
    activeTab: AdminTab,
    onTabSelect: (AdminTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(AdminTab.TODAY, "Today", Icons.Default.Today),
            Triple(AdminTab.CALENDAR, "Calendar", Icons.Default.CalendarMonth),
            Triple(AdminTab.BOOKINGS, "Bookings", Icons.Default.ListAlt),
            Triple(AdminTab.SETTINGS, "Settings", Icons.Default.Settings)
        )

        items.forEach { (tab, label, icon) ->
            NavigationBarItem(
                selected = activeTab == tab,
                onClick = { onTabSelect(tab) },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp)) },
                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IhwDarkMaroon,
                    selectedTextColor = IhwDarkMaroon,
                    indicatorColor = IhwGold.copy(alpha = 0.35f),
                    unselectedIconColor = IhwMutedText,
                    unselectedTextColor = IhwMutedText
                )
            )
        }
    }
}

// ----------------- TAB 1: TODAY -----------------
@Composable
private fun AdminTodayTab(viewModel: IhwViewModel) {
    val siteConfig by viewModel.repository.siteConfig.collectAsState()
    val bookings by viewModel.repository.bookings.collectAsState()
    val timeSlots = viewModel.repository.getAvailableTimeSlots()

    var selectedChair by remember { mutableStateOf(CustomerType.MEN) }
    var rescheduleTargetBooking by remember { mutableStateOf<Booking?>(null) }
    var walkInSlot by remember { mutableStateOf<String?>(null) }

    val todayBookings = bookings.filter {
        it.dateStr.contains("Today") || it.dateStr.contains("Oct 26") || it.dateStr.contains("Oct 31") || it.dateStr.contains("Nov")
    }

    val chairCapacity = if (selectedChair == CustomerType.MEN) siteConfig.gentsChairCapacity else siteConfig.ladiesChairCapacity
    val chairBookings = todayBookings.filter { it.customerType == selectedChair }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard("Today Total", "${todayBookings.size}", IhwDarkMaroon, Modifier.weight(1f))
                AdminStatCard("Gents Booked", "${todayBookings.count { it.customerType == CustomerType.MEN }}", Color(0xFF2563EB), Modifier.weight(1f))
                AdminStatCard("Ladies Booked", "${todayBookings.count { it.customerType == CustomerType.WOMEN }}", Color(0xFF9333EA), Modifier.weight(1f))
                AdminStatCard("Revenue", "₹${todayBookings.sumOf { it.totalPrice }}", Color(0xFF16A34A), Modifier.weight(1.2f))
            }
        }

        // Side-by-Side Chair Tabs: Gents Chair vs Ladies Chair
        item {
            ChairCapacityHeader(
                selectedChair = selectedChair,
                onSelectChair = { selectedChair = it },
                gentsCapacity = siteConfig.gentsChairCapacity,
                ladiesCapacity = siteConfig.ladiesChairCapacity
            )
        }

        // 11:00 AM to 7:00 PM Timeline for selected chair
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${selectedChair.label} Timeline (11 AM – 7 PM)",
                    color = IhwDarkMaroon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Capacity: $chairCapacity chair${if (chairCapacity > 1) "s" else ""}",
                    color = IhwMutedText,
                    fontSize = 12.sp
                )
            }
        }

        items(timeSlots) { slot ->
            val slotBookings = chairBookings.filter { it.timeSlot == slot && it.status != BookingStatus.CANCELLED }
            val isFull = slotBookings.size >= chairCapacity

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                border = BorderStroke(1.dp, if (slotBookings.isNotEmpty()) IhwGold.copy(alpha = 0.5f) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = IhwDarkMaroon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = slot,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IhwNavyText
                            )
                        }

                        if (slotBookings.isEmpty()) {
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "FREE CHAIR",
                                    color = Color(0xFF166534),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(
                                color = if (isFull) Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${slotBookings.size}/$chairCapacity BOOKED",
                                    color = if (isFull) Color(0xFF991B1B) else Color(0xFF92400E),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (slotBookings.isEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Chair ready for appointments or walk-ins.",
                                color = IhwMutedText,
                                fontSize = 11.sp
                            )
                            OutlinedButton(
                                onClick = { walkInSlot = slot },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                            ) {
                                Text("+ Walk-in", fontSize = 11.sp, color = IhwDarkMaroon, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                        slotBookings.forEach { bk ->
                            BookingManagementCard(
                                booking = bk,
                                onConfirm = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.CONFIRMED) },
                                onComplete = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.COMPLETED) },
                                onNoShow = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.NO_SHOW) },
                                onCancel = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.CANCELLED) },
                                onReschedule = { rescheduleTargetBooking = bk }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // Reschedule Dialog
    rescheduleTargetBooking?.let { bk ->
        RescheduleDialog(
            booking = bk,
            viewModel = viewModel,
            onDismiss = { rescheduleTargetBooking = null },
            onRescheduled = {
                rescheduleTargetBooking = null
                viewModel.showToast("Booking rescheduled successfully.")
            }
        )
    }

    // Walk-in Dialog
    walkInSlot?.let { slot ->
        WalkInBookingDialog(
            defaultCategory = selectedChair,
            defaultTime = slot,
            viewModel = viewModel,
            onDismiss = { walkInSlot = null }
        )
    }
}

// ----------------- TAB 2: CALENDAR -----------------
@Composable
private fun AdminCalendarTab(viewModel: IhwViewModel) {
    val siteConfig by viewModel.repository.siteConfig.collectAsState()
    val bookings by viewModel.repository.bookings.collectAsState()
    val timeSlots = viewModel.repository.getAvailableTimeSlots()

    var selectedDate by remember { mutableStateOf("Today, Oct 26") }
    var selectedChair by remember { mutableStateOf(CustomerType.MEN) }
    var rescheduleTargetBooking by remember { mutableStateOf<Booking?>(null) }
    var walkInSlot by remember { mutableStateOf<String?>(null) }

    val dateOptions = listOf("Today, Oct 26", "Tomorrow, Oct 27", "Mon, Oct 28", "Tue, Oct 29", "Wed, Oct 30")

    val chairCapacity = if (selectedChair == CustomerType.MEN) siteConfig.gentsChairCapacity else siteConfig.ladiesChairCapacity
    val dateBookings = bookings.filter { it.dateStr.contains(selectedDate.split(",").last().trim()) || (selectedDate.startsWith("Today") && (it.dateStr.contains("Today") || it.dateStr.contains("Oct 26"))) }
    val chairBookings = dateBookings.filter { it.customerType == selectedChair }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Appointment Calendar",
                color = IhwDarkMaroon,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Schedule organized by Gents & Ladies chairs across dates",
                color = IhwMutedText,
                fontSize = 12.sp
            )
        }

        // Date selection chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dateOptions.forEach { d ->
                    val isSel = selectedDate == d
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedDate = d },
                        color = if (isSel) IhwDarkMaroon else Color.White,
                        border = BorderStroke(1.dp, if (isSel) IhwDarkMaroon else Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = d,
                            color = if (isSel) Color.White else IhwNavyText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Side-by-side chair toggle
        item {
            ChairCapacityHeader(
                selectedChair = selectedChair,
                onSelectChair = { selectedChair = it },
                gentsCapacity = siteConfig.gentsChairCapacity,
                ladiesCapacity = siteConfig.ladiesChairCapacity
            )
        }

        // Timeline for the day
        items(timeSlots) { slot ->
            val slotBookings = chairBookings.filter { it.timeSlot == slot && it.status != BookingStatus.CANCELLED }
            val isFull = slotBookings.size >= chairCapacity

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = slot, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = IhwNavyText)
                        if (slotBookings.isEmpty()) {
                            Text(text = "Available", color = Color(0xFF166534), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = "${slotBookings.size}/$chairCapacity Booked",
                                color = if (isFull) Color(0xFF991B1B) else Color(0xFF92400E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (slotBookings.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        slotBookings.forEach { bk ->
                            BookingManagementCard(
                                booking = bk,
                                onConfirm = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.CONFIRMED) },
                                onComplete = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.COMPLETED) },
                                onNoShow = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.NO_SHOW) },
                                onCancel = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.CANCELLED) },
                                onReschedule = { rescheduleTargetBooking = bk }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { walkInSlot = slot }) {
                                Text("+ Book Slot", fontSize = 11.sp, color = IhwMaroon)
                            }
                        }
                    }
                }
            }
        }
    }

    rescheduleTargetBooking?.let { bk ->
        RescheduleDialog(
            booking = bk,
            viewModel = viewModel,
            onDismiss = { rescheduleTargetBooking = null },
            onRescheduled = {
                rescheduleTargetBooking = null
                viewModel.showToast("Booking rescheduled.")
            }
        )
    }

    walkInSlot?.let { slot ->
        WalkInBookingDialog(
            defaultCategory = selectedChair,
            defaultTime = slot,
            viewModel = viewModel,
            onDismiss = { walkInSlot = null }
        )
    }
}

// ----------------- TAB 3: BOOKINGS -----------------
@Composable
private fun AdminBookingsTab(viewModel: IhwViewModel) {
    val context = LocalContext.current
    val bookings by viewModel.repository.bookings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf<CustomerType?>(null) }
    var filterWorkType by remember { mutableStateOf<Boolean?>(null) }
    var filterStatus by remember { mutableStateOf<BookingStatus?>(null) }
    var showWalkInDialog by remember { mutableStateOf(false) }
    var showCsvExportDialog by remember { mutableStateOf(false) }
    var rescheduleTargetBooking by remember { mutableStateOf<Booking?>(null) }

    val filteredBookings = bookings.filter { bk ->
        val matchesSearch = searchQuery.isBlank() ||
                bk.customerName.contains(searchQuery, ignoreCase = true) ||
                bk.customerPhone.contains(searchQuery) ||
                bk.bookingCode.contains(searchQuery, ignoreCase = true) ||
                bk.serviceName.contains(searchQuery, ignoreCase = true)
        val matchesCategory = filterCategory == null || bk.customerType == filterCategory
        val matchesWorkType = filterWorkType == null || bk.isNewWork == filterWorkType
        val matchesStatus = filterStatus == null || bk.status == filterStatus

        matchesSearch && matchesCategory && matchesWorkType && matchesStatus
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "All Bookings (${filteredBookings.size})",
                        color = IhwDarkMaroon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Search, filter, walk-in registration & CSV export",
                        color = IhwMutedText,
                        fontSize = 11.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { showCsvExportDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp), tint = IhwDarkMaroon)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CSV", fontSize = 11.sp, color = IhwDarkMaroon, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showWalkInDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+ Walk-In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name, phone, code or service") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IhwMutedText) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_bookings_search_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Filter chips: Gents / Ladies / New Work / Servicing
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = filterCategory == null,
                    onClick = { filterCategory = null },
                    label = { Text("All Chairs", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterCategory == CustomerType.MEN,
                    onClick = { filterCategory = if (filterCategory == CustomerType.MEN) null else CustomerType.MEN },
                    label = { Text("Gents", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterCategory == CustomerType.WOMEN,
                    onClick = { filterCategory = if (filterCategory == CustomerType.WOMEN) null else CustomerType.WOMEN },
                    label = { Text("Ladies", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterWorkType == true,
                    onClick = { filterWorkType = if (filterWorkType == true) null else true },
                    label = { Text("New Work", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterWorkType == false,
                    onClick = { filterWorkType = if (filterWorkType == false) null else false },
                    label = { Text("Servicing", fontSize = 11.sp) }
                )
            }
        }

        if (filteredBookings.isEmpty()) {
            item {
                Text(
                    text = "No bookings found matching current filters.",
                    color = IhwMutedText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(filteredBookings) { bk ->
                BookingManagementCard(
                    booking = bk,
                    onConfirm = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.CONFIRMED) },
                    onComplete = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.COMPLETED) },
                    onNoShow = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.NO_SHOW) },
                    onCancel = { viewModel.repository.updateBookingStatus(bk.id, BookingStatus.CANCELLED) },
                    onReschedule = { rescheduleTargetBooking = bk }
                )
            }
        }
    }

    // CSV Export Dialog
    if (showCsvExportDialog) {
        val csvText = buildString {
            appendLine("BookingID,Date,Time,Category,WorkType,Customer,Phone,Service,Price,Status")
            bookings.forEach { b ->
                val cat = if (b.customerType == CustomerType.MEN) "Gents" else "Ladies"
                val wt = if (b.isNewWork) "New Work" else "Servicing"
                appendLine("${b.bookingCode},\"${b.dateStr}\",\"${b.timeSlot}\",$cat,$wt,\"${b.customerName}\",\"${b.customerPhone}\",\"${b.serviceName}\",${b.totalPrice},${b.status.label}")
            }
        }

        AlertDialog(
            onDismissRequest = { showCsvExportDialog = false },
            title = { Text("CSV Bookings Summary", fontWeight = FontWeight.Bold, color = IhwDarkMaroon) },
            text = {
                Column {
                    Text(
                        text = "Total ${bookings.size} records ready to copy or share.",
                        fontSize = 12.sp,
                        color = IhwMutedText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = csvText,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("IHW Bookings CSV", csvText)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showToast("CSV summary copied to clipboard!")
                        showCsvExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon)
                ) {
                    Text("Copy CSV")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCsvExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Walk-In Dialog
    if (showWalkInDialog) {
        WalkInBookingDialog(
            defaultCategory = CustomerType.MEN,
            defaultTime = "12:00 PM",
            viewModel = viewModel,
            onDismiss = { showWalkInDialog = false }
        )
    }

    rescheduleTargetBooking?.let { bk ->
        RescheduleDialog(
            booking = bk,
            viewModel = viewModel,
            onDismiss = { rescheduleTargetBooking = null },
            onRescheduled = {
                rescheduleTargetBooking = null
                viewModel.showToast("Booking rescheduled.")
            }
        )
    }
}

// ----------------- TAB 4: SETTINGS -----------------
@Composable
private fun AdminSettingsTab(viewModel: IhwViewModel) {
    val config by viewModel.repository.siteConfig.collectAsState()
    val services by viewModel.repository.services.collectAsState()

    var gentsCapacity by remember { mutableStateOf(config.gentsChairCapacity) }
    var ladiesCapacity by remember { mutableStateOf(config.ladiesChairCapacity) }
    var phone by remember { mutableStateOf(config.phone) }
    var hours by remember { mutableStateOf(config.workingHours) }
    var startingPrice by remember { mutableStateOf(config.startingPrice.toString()) }
    var festBannerEnabled by remember { mutableStateOf(config.festivalBannerEnabled) }
    var festText by remember { mutableStateOf(config.festivalBannerText) }
    var pin by remember { mutableStateOf(config.adminPin) }

    var editingService by remember { mutableStateOf<ServiceItem?>(null) }
    var newServicePrice by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Studio Configuration",
                color = IhwDarkMaroon,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Configure independent Gents & Ladies chair capacities, hours, services, and PIN",
                color = IhwMutedText,
                fontSize = 12.sp
            )
        }

        // Chair System Capacities (CRITICAL REQUIREMENT)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Chair Capacity Tracking",
                        fontWeight = FontWeight.Bold,
                        color = IhwNavyText,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Gents and Ladies capacities are tracked independently so both can be booked simultaneously at the same hour slot.",
                        color = IhwMutedText,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gents Chair Stepper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Gents Chair Capacity", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                            Text("Max simultaneous gents slots", fontSize = 10.sp, color = IhwMutedText)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (gentsCapacity > 1) gentsCapacity-- },
                                enabled = gentsCapacity > 1
                            ) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
                            }
                            Text(
                                text = "$gentsCapacity",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { if (gentsCapacity < 10) gentsCapacity++ }
                            ) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    // Ladies Chair Stepper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ladies Chair Capacity", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF701A75))
                            Text("Max simultaneous ladies slots", fontSize = 10.sp, color = IhwMutedText)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (ladiesCapacity > 1) ladiesCapacity-- },
                                enabled = ladiesCapacity > 1
                            ) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
                            }
                            Text(
                                text = "$ladiesCapacity",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { if (ladiesCapacity < 10) ladiesCapacity++ }
                            ) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }
        }

        // Timing & Contact
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Hours & Contact", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IhwNavyText)

                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Studio Working Hours") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = startingPrice,
                        onValueChange = { startingPrice = it },
                        label = { Text("Starting Patch Price (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Festival Banner
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Festival Banner", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IhwNavyText)
                        Switch(checked = festBannerEnabled, onCheckedChange = { festBannerEnabled = it })
                    }

                    if (festBannerEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = festText,
                            onValueChange = { festText = it },
                            label = { Text("Banner Message") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }
            }
        }

        // Services & Price Management
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Services & Prices (${services.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IhwNavyText)
                    Text("Tap to edit standard pricing", color = IhwMutedText, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    services.take(6).forEach { svc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editingService = svc
                                    newServicePrice = svc.price.toString()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(svc.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = IhwNavyText)
                                Text("${svc.customerType.label} • ${if (svc.isNewWork) "New Work" else "Servicing"}", fontSize = 10.sp, color = IhwMutedText)
                            }
                            Text(
                                text = if (svc.price == 0) "FREE" else "₹${svc.price}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = IhwDarkMaroon
                            )
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                    }
                }
            }
        }

        // Security / Admin PIN
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Admin PIN", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IhwNavyText)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
                        label = { Text("4-Digit Portal PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = config.copy(
                        gentsChairCapacity = gentsCapacity,
                        ladiesChairCapacity = ladiesCapacity,
                        phone = phone,
                        workingHours = hours,
                        startingPrice = startingPrice.toIntOrNull() ?: 5999,
                        festivalBannerEnabled = festBannerEnabled,
                        festivalBannerText = festText,
                        adminPin = pin.ifBlank { "1234" }
                    )
                    viewModel.repository.updateConfig(updated)
                    viewModel.showToast("Settings updated successfully!")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_save_settings_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Studio Configuration", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Edit service price dialog
    editingService?.let { svc ->
        AlertDialog(
            onDismissRequest = { editingService = null },
            title = { Text("Edit Price: ${svc.name}", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newServicePrice,
                    onValueChange = { newServicePrice = it },
                    label = { Text("Price in INR (₹)") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = newServicePrice.toIntOrNull() ?: svc.price
                        viewModel.repository.updateServicePrice(svc.id, p)
                        viewModel.showToast("Updated ${svc.name} price to ₹$p")
                        editingService = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingService = null }) { Text("Cancel") }
            }
        )
    }
}

// ----------------- REUSABLE ADMIN COMPONENTS -----------------

@Composable
private fun ChairCapacityHeader(
    selectedChair: CustomerType,
    onSelectChair: (CustomerType) -> Unit,
    gentsCapacity: Int,
    ladiesCapacity: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            // Gents Chair Tab
            val isGents = selectedChair == CustomerType.MEN
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isGents) IhwDarkMaroon else Color.Transparent)
                    .clickable { onSelectChair(CustomerType.MEN) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Gents Chair",
                        color = if (isGents) Color.White else IhwNavyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (isGents) IhwGold else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Cap: $gentsCapacity",
                            color = if (isGents) IhwDarkMaroon else IhwNavyText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Ladies Chair Tab
            val isLadies = selectedChair == CustomerType.WOMEN
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isLadies) IhwDarkMaroon else Color.Transparent)
                    .clickable { onSelectChair(CustomerType.WOMEN) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ladies Chair",
                        color = if (isLadies) Color.White else IhwNavyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (isLadies) IhwGold else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Cap: $ladiesCapacity",
                            color = if (isLadies) IhwDarkMaroon else IhwNavyText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingManagementCard(
    booking: Booking,
    onConfirm: () -> Unit,
    onComplete: () -> Unit,
    onNoShow: () -> Unit,
    onCancel: () -> Unit,
    onReschedule: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Tag, WorkType Tag, Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category Tag
                    Surface(
                        color = if (booking.customerType == CustomerType.MEN) Color(0xFFDBEAFE) else Color(0xFFFCE7F3),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = booking.customerType.label,
                            color = if (booking.customerType == CustomerType.MEN) Color(0xFF1E40AF) else Color(0xFF9D174D),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // WorkType Tag
                    Surface(
                        color = if (booking.isNewWork) IhwMaroon.copy(alpha = 0.12f) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (booking.isNewWork) "New Work" else "Servicing",
                            color = if (booking.isNewWork) IhwMaroon else Color(0xFF92400E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                StatusPill(booking.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Name & Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = booking.customerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = IhwNavyText
                )
                Text(
                    text = booking.bookingCode,
                    color = IhwMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "${booking.serviceName} • ₹${booking.totalPrice}",
                color = IhwDarkMaroon,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )

            Text(
                text = "${booking.dateStr} at ${booking.timeSlot} (${booking.durationMinutes} mins)",
                color = IhwMutedText,
                fontSize = 11.sp
            )

            if (booking.notes.isNotBlank()) {
                Text(
                    text = "Note: ${booking.notes}",
                    color = Color(0xFF92400E),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: 1-Tap Call, 1-Tap WA Reminder, Status Updates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Call
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.customerPhone.replace(" ", "")}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Call", fontSize = 11.sp, color = Color(0xFF2563EB))
                }

                // WhatsApp Reminder
                OutlinedButton(
                    onClick = {
                        val msg = "Hi ${booking.customerName}! Gentle reminder of your hair appointment at Indian Hair World Sodepur on ${booking.dateStr} at ${booking.timeSlot}. Let us know if you need assistance."
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${booking.customerPhone.replace("+", "").replace(" ", "")}?text=${Uri.encode(msg)}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("WA Alert", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }

                // Reschedule
                OutlinedButton(
                    onClick = onReschedule,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Resched", fontSize = 11.sp, color = IhwNavyText)
                }

                // Complete
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IhwDarkMaroon),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Done", fontSize = 11.sp)
                }
            }

            // Secondary row: No-show, Cancel, Confirm
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (booking.status != BookingStatus.CONFIRMED) {
                    TextButton(onClick = onConfirm, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)) {
                        Text("Confirm", fontSize = 10.sp, color = Color(0xFF16A34A))
                    }
                }
                TextButton(onClick = onNoShow, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)) {
                    Text("No-Show", fontSize = 10.sp, color = Color(0xFFD97706))
                }
                TextButton(onClick = onCancel, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)) {
                    Text("Cancel", fontSize = 10.sp, color = Color(0xFFDC2626))
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: BookingStatus) {
    val (bg, fg) = when (status) {
        BookingStatus.CONFIRMED -> Color(0xFFDCFCE7) to Color(0xFF166534)
        BookingStatus.COMPLETED -> Color(0xFFE0E7FF) to Color(0xFF3730A3)
        BookingStatus.PENDING -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        BookingStatus.NO_SHOW -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
        BookingStatus.CANCELLED -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = status.label,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, color = IhwMutedText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1)
        }
    }
}

// ----------------- WALK-IN REGISTRATION DIALOG -----------------
@Composable
private fun WalkInBookingDialog(
    defaultCategory: CustomerType,
    defaultTime: String,
    viewModel: IhwViewModel,
    onDismiss: () -> Unit
) {
    val services by viewModel.repository.services.collectAsState()
    val timeSlots = viewModel.repository.getAvailableTimeSlots()

    var step by remember { mutableStateOf(1) } // 1: Choose Gents / Ladies, 2: Details & Free Slot
    var category by remember { mutableStateOf(defaultCategory) }
    var isNewWork by remember { mutableStateOf(true) }
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("Today, Oct 26") }
    var selectedTime by remember { mutableStateOf(defaultTime) }

    val catServices = services.filter { it.customerType == category && it.isNewWork == isNewWork }
    var selectedService by remember { mutableStateOf(catServices.firstOrNull() ?: services.first()) }

    LaunchedEffect(category, isNewWork) {
        val s = services.filter { it.customerType == category && it.isNewWork == isNewWork }.firstOrNull()
        if (s != null) selectedService = s
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (step == 1) "Walk-In: Choose Category First" else "Walk-In: Client Details",
                fontWeight = FontWeight.Bold,
                color = IhwDarkMaroon,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (step == 1) {
                    Text(
                        text = "Indian Hair World separates Gents & Ladies queues and chairs. Please select the customer category:",
                        fontSize = 12.sp,
                        color = IhwNavyText
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { category = CustomerType.MEN }
                                .border(2.dp, if (category == CustomerType.MEN) IhwDarkMaroon else Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                            color = if (category == CustomerType.MEN) IhwCreamBg else Color.White
                        ) {
                            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Gents", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IhwDarkMaroon)
                                Text("Men's Hair Patch", fontSize = 10.sp, color = IhwMutedText)
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { category = CustomerType.WOMEN }
                                .border(2.dp, if (category == CustomerType.WOMEN) IhwDarkMaroon else Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                            color = if (category == CustomerType.WOMEN) IhwCreamBg else Color.White
                        ) {
                            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Ladies", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IhwDarkMaroon)
                                Text("Wigs & Toppers", fontSize = 10.sp, color = IhwMutedText)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Appointment Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = isNewWork,
                            onClick = { isNewWork = true },
                            label = { Text("New Work", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = !isNewWork,
                            onClick = { isNewWork = false },
                            label = { Text("Servicing Work", fontSize = 11.sp) }
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) clientPhone = it },
                        label = { Text("10-Digit Mobile Number *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Available Slots for ${category.label}:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        timeSlots.chunked(4).forEach { rowSlots ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                rowSlots.forEach { slot ->
                                    val isAvailable = viewModel.repository.isSlotAvailable(selectedDate, slot, category)
                                    val isSelected = selectedTime == slot
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable(enabled = isAvailable) { selectedTime = slot },
                                        color = when {
                                            !isAvailable -> Color(0xFFF1F5F9)
                                            isSelected -> IhwDarkMaroon
                                            else -> Color.White
                                        },
                                        border = BorderStroke(1.dp, if (isSelected) IhwDarkMaroon else Color(0xFFCBD5E1))
                                    ) {
                                        Text(
                                            text = slot.replace(" ", ""),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                !isAvailable -> Color(0xFF94A3B8)
                                                isSelected -> Color.White
                                                else -> IhwNavyText
                                            },
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (step == 1) {
                Button(
                    onClick = { step = 2 },
                    colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon)
                ) {
                    Text("Next: Details")
                }
            } else {
                Button(
                    onClick = {
                        if (clientName.isBlank() || clientPhone.length < 10) {
                            viewModel.showToast("Please enter valid name and 10-digit phone.")
                            return@Button
                        }
                        viewModel.repository.addWalkInBooking(
                            customerName = clientName,
                            customerPhone = clientPhone,
                            customerType = category,
                            isNewWork = isNewWork,
                            service = selectedService,
                            dateStr = selectedDate,
                            timeSlot = selectedTime,
                            notes = "Studio Walk-In Registration"
                        )
                        viewModel.showToast("Walk-in appointment booked for ${category.label} chair!")
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon)
                ) {
                    Text("Confirm Walk-in")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (step == 2) step = 1 else onDismiss()
            }) {
                Text(if (step == 2) "Back" else "Cancel")
            }
        }
    )
}

// ----------------- RESCHEDULE DIALOG -----------------
@Composable
private fun RescheduleDialog(
    booking: Booking,
    viewModel: IhwViewModel,
    onDismiss: () -> Unit,
    onRescheduled: () -> Unit
) {
    val timeSlots = viewModel.repository.getAvailableTimeSlots()
    val dateOptions = listOf("Today, Oct 26", "Tomorrow, Oct 27", "Mon, Oct 28", "Tue, Oct 29", "Wed, Oct 30")

    var selectedDate by remember { mutableStateOf(booking.dateStr.ifBlank { dateOptions.first() }) }
    var selectedTime by remember { mutableStateOf(booking.timeSlot) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reschedule Appointment", fontWeight = FontWeight.Bold, color = IhwDarkMaroon) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Client: ${booking.customerName} (${booking.customerType.label})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text("Pick New Date:", fontSize = 11.sp, color = IhwMutedText)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    dateOptions.take(3).forEach { d ->
                        val isSel = selectedDate.contains(d.split(",").last().trim())
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedDate = d },
                            color = if (isSel) IhwDarkMaroon else Color.White,
                            border = BorderStroke(1.dp, if (isSel) IhwDarkMaroon else Color(0xFFCBD5E1))
                        ) {
                            Text(
                                text = d.split(",").first(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else IhwNavyText,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Pick New Time (${booking.customerType.label} Chair):", fontSize = 11.sp, color = IhwMutedText)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    timeSlots.chunked(4).forEach { rowSlots ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowSlots.forEach { slot ->
                                val isAvailable = viewModel.repository.isSlotAvailable(selectedDate, slot, booking.customerType)
                                val isSelected = selectedTime == slot
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(enabled = isAvailable) { selectedTime = slot },
                                    color = when {
                                        !isAvailable -> Color(0xFFF1F5F9)
                                        isSelected -> IhwDarkMaroon
                                        else -> Color.White
                                    },
                                    border = BorderStroke(1.dp, if (isSelected) IhwDarkMaroon else Color(0xFFCBD5E1))
                                ) {
                                    Text(
                                        text = slot.replace(" ", ""),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            !isAvailable -> Color(0xFF94A3B8)
                                            isSelected -> Color.White
                                            else -> IhwNavyText
                                        },
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.repository.rescheduleBooking(booking.id, selectedDate, selectedTime)
                    onRescheduled()
                },
                colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon)
            ) {
                Text("Confirm Reschedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
