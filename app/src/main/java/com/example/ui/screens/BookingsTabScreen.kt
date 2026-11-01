package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.ui.theme.*
import com.example.viewmodel.IhwViewModel

@Composable
fun BookingsTabScreen(
    viewModel: IhwViewModel,
    onBookNow: () -> Unit
) {
    val context = LocalContext.current
    val bookings by viewModel.repository.bookings.collectAsState()
    var searchPhone by remember { mutableStateOf(viewModel.repository.rememberedPhone) }
    var selectedSegment by remember { mutableStateOf(0) } // 0 = Upcoming, 1 = Past
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var bookingToReschedule by remember { mutableStateOf<Booking?>(null) }

    val myBookings = bookings.filter {
        searchPhone.length < 4 || it.customerPhone.contains(searchPhone.trim())
    }

    val upcomingBookings = myBookings.filter {
        it.status == BookingStatus.CONFIRMED || it.status == BookingStatus.PENDING
    }
    val pastBookings = myBookings.filter {
        it.status == BookingStatus.COMPLETED || it.status == BookingStatus.CANCELLED || it.status == BookingStatus.NO_SHOW
    }

    val displayList = if (selectedSegment == 0) upcomingBookings else pastBookings

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IhwCreamBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "My Appointments",
                color = IhwDarkMaroon,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track, reschedule or manage your studio visits",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        // Phone Lookup
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Phone Lookup",
                        color = IhwNavyText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = searchPhone,
                        onValueChange = {
                            searchPhone = it
                            viewModel.repository.rememberedPhone = it
                        },
                        label = { Text("Your 10-Digit Mobile Number") },
                        prefix = { Text("+91 ") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bookings_phone_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Two Segments: Upcoming and Past
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    listOf("Upcoming (${upcomingBookings.size})", "Past (${pastBookings.size})").forEachIndexed { idx, label ->
                        val isSel = selectedSegment == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) IhwMaroon else Color.Transparent)
                                .clickable { selectedSegment = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) Color.White else IhwNavyText,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        if (displayList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EventBusy,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (selectedSegment == 0) "No upcoming bookings" else "No past bookings",
                            color = IhwDarkMaroon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Walk in or book your consultation in advance.",
                            color = IhwMutedText,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onBookNow,
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("empty_bookings_book_now_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IhwMaroon,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            items(displayList) { bk ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = bk.bookingCode,
                                color = IhwDarkMaroon,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )

                            // Status Chip
                            val statusColor = when (bk.status) {
                                BookingStatus.CONFIRMED -> Color(0xFF10B981)
                                BookingStatus.PENDING -> Color(0xFFF59E0B)
                                BookingStatus.COMPLETED -> Color(0xFF3B82F6)
                                BookingStatus.CANCELLED -> Color(0xFFEF4444)
                                BookingStatus.NO_SHOW -> Color(0xFF6B7280)
                            }
                            Surface(
                                color = statusColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = bk.status.label,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = bk.serviceName,
                            color = IhwNavyText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Category & Work Type tags
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = IhwCreamBg,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IhwGold)
                            ) {
                                Text(
                                    text = "${bk.customerType.label} Chair",
                                    color = IhwDarkMaroon,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = if (bk.isNewWork) Color(0xFFFDF2F4) else Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (bk.isNewWork) "New Work" else "Servicing Work",
                                    color = if (bk.isNewWork) IhwMaroon else IhwWhatsApp,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Scheduled: ${bk.dateStr} at ${bk.timeSlot}",
                            color = IhwNavyText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )

                        Text(
                            text = "Studio: ${bk.branchName} • Price: ₹${bk.totalPrice} (Pay at studio)",
                            color = IhwMutedText,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Reschedule, Cancel, Get Directions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (bk.status == BookingStatus.CONFIRMED) {
                                OutlinedButton(
                                    onClick = { bookingToReschedule = bk },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reschedule", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { bookingToCancel = bk },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                                ) {
                                    Text("Cancel", fontSize = 11.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=Indian+Hair+World+Sodepur+Kolkata")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Directions", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Cancel Dialog
    bookingToCancel?.let { bk ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text("Cancel Appointment?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to cancel appointment ${bk.bookingCode} for ${bk.serviceName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.updateBookingStatus(bk.id, BookingStatus.CANCELLED)
                        viewModel.showToast("Appointment cancelled.")
                        bookingToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Cancel Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text("Keep Appointment")
                }
            }
        )
    }

    // Reschedule Dialog
    bookingToReschedule?.let { bk ->
        var newDate by remember { mutableStateOf("Tomorrow, 2:00 PM") }
        val slots = listOf("Tomorrow, 11:00 AM", "Tomorrow, 1:00 PM", "Tomorrow, 3:00 PM", "Tomorrow, 5:00 PM")

        AlertDialog(
            onDismissRequest = { bookingToReschedule = null },
            title = { Text("Reschedule Appointment", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Current: ${bk.dateStr} at ${bk.timeSlot}")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Choose new time slot:", fontSize = 12.sp, color = IhwMutedText)
                    Spacer(modifier = Modifier.height(6.dp))
                    slots.forEach { slot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { newDate = slot }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = newDate == slot, onClick = { newDate = slot })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(slot, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.rescheduleBooking(bk.id, newDate.substringBefore(","), newDate.substringAfter(","))
                        viewModel.showToast("Rescheduled to $newDate!")
                        bookingToReschedule = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToReschedule = null }) {
                    Text("Dismiss")
                }
            }
        )
    }
}
