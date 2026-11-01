package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Booking
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.IhwViewModel

@Composable
fun BookingConfirmationScreen(
    booking: Booking?,
    viewModel: IhwViewModel,
    onHomeClick: () -> Unit,
    onMyBookingsClick: () -> Unit
) {
    val context = LocalContext.current
    val b = booking ?: return

    BackHandler {
        onHomeClick()
    }

    Scaffold(
        containerColor = IhwCreamBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Large animated maroon circle with checkmark
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(IhwBookingMaroon),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Booking Confirmed",
                    color = IhwDarkMaroon,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Booking Code", b.bookingCode)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showToast("Booking ID copied to clipboard!")
                    }
                ) {
                    Text(
                        text = "Booking ID: ${b.bookingCode}",
                        color = IhwMutedText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy ID",
                        tint = IhwBookingMaroon,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Summary Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Summary",
                            color = IhwDarkMaroon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryItem("Service", "${b.serviceName} • ${b.method} (${b.color})")
                        SummaryItem("Studio", "${b.branchName}, Station Road, Sodepur")
                        SummaryItem("Date & Time", "${b.dateStr}, ${b.timeSlot} (${b.durationMinutes} min)")
                        SummaryItem("Customer", "${b.customerName} (${b.customerPhone})")
                        SummaryItem("Price", if (b.totalPrice == 0) "FREE" else "₹${b.totalPrice} (Pay at studio)")
                    }
                }
            }

            // Action Buttons matching reference screenshot
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Add to Calendar
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, "IHW Appointment: ${b.serviceName}")
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Indian Hair World, Sodepur")
                                .putExtra(CalendarContract.Events.DESCRIPTION, "Booking ID: ${b.bookingCode}")
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showToast("Calendar opened")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_calendar_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IhwNavyText.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = IhwNavyText, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Calendar", color = IhwNavyText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    // Share on WhatsApp
                    OutlinedButton(
                        onClick = {
                            val msg = "Hi IHW! I have confirmed booking ${b.bookingCode} for ${b.serviceName} at Sodepur studio on ${b.dateStr} at ${b.timeSlot}."
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918910097668?text=${Uri.encode(msg)}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_whatsapp_share_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IhwWhatsApp)
                    ) {
                        Text("Share on WhatsApp", color = IhwWhatsApp, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Get Directions
                    OutlinedButton(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=Indian+Hair+World+Sodepur+Kolkata")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_directions_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IhwNavyText.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Default.Directions, contentDescription = null, tint = IhwNavyText, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Get Directions", color = IhwNavyText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    // View My Bookings
                    OutlinedButton(
                        onClick = onMyBookingsClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_my_bookings_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IhwBookingMaroon)
                    ) {
                        Text("View My Bookings", color = IhwBookingMaroon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Back to Home
                    Button(
                        onClick = onHomeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("action_home_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IhwBookingMaroon,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back to Home", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // What to expect list
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "What to expect",
                            color = IhwDarkMaroon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val expectations = listOf(
                            "Arrive 10 minutes before your scheduled appointment",
                            "Bring a photo of your preferred hairstyle if you have one",
                            "Enjoy a hot beverage in our private sanitized cabin",
                            "Walk out in 60 minutes with full natural hairline confidence"
                        )
                        expectations.forEach { exp ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = IhwBookingMaroon, fontWeight = FontWeight.Bold)
                                Text(exp, color = IhwNavyText, fontSize = 12.sp, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, color = IhwMutedText, fontSize = 11.sp)
        Text(text = value, color = IhwNavyText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
