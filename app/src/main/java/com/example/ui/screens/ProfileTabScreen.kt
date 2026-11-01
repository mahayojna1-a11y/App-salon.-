package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.*
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
import com.example.model.Language
import com.example.model.MainTab
import com.example.ui.theme.*
import com.example.viewmodel.IhwViewModel

@Composable
fun ProfileTabScreen(
    viewModel: IhwViewModel,
    onAdminClick: () -> Unit
) {
    val context = LocalContext.current
    val siteConfig by viewModel.repository.siteConfig.collectAsState()
    val profile by viewModel.repository.userProfile.collectAsState()
    val currentLang by viewModel.repository.currentLanguage.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IhwCreamBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Profile & Studio Info",
                color = IhwDarkMaroon,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Avatar & User Info Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Initials avatar
                        val initials = profile.name.split(" ").filter { it.isNotEmpty() }
                            .map { it.first().uppercase() }.take(2).joinToString("")
                            .ifEmpty { "RM" }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(IhwMaroon),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                color = IhwGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = profile.name,
                                color = IhwNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+91 ${profile.phone}",
                                color = IhwMutedText,
                                fontSize = 12.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Edit", fontSize = 11.sp)
                    }
                }
            }
        }

        // Shortcut to My Bookings
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.setMainTab(MainTab.BOOKINGS) },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = IhwMaroon)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("My Bookings", color = IhwNavyText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Text("→", color = IhwMutedText, fontSize = 16.sp)
                }
            }
        }

        // Contact Studio Card: Call and WhatsApp buttons
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
                        text = "Contact Studio",
                        color = IhwDarkMaroon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${siteConfig.phone.replace(" ", "")}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IhwGold,
                                contentColor = IhwDarkMaroon
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Call Studio", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://wa.me/${siteConfig.whatsAppNumber}?text=${Uri.encode("Hi IHW! I have an enquiry.")}")
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IhwWhatsApp,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Address & Hours Card + Get Directions
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
                        text = "Studio Location & Hours",
                        color = IhwDarkMaroon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = siteConfig.address,
                        color = IhwNavyText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Hours: 11:00 AM – 7:00 PM (Open 7 Days)",
                        color = IhwNavyText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=Indian+Hair+World+Sodepur+Kolkata")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Get Directions", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Language Switch English / Bangla (ONLY HERE)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Language / ভাষা",
                            color = IhwNavyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (currentLang == Language.ENGLISH) "English" else "বাংলা",
                            color = IhwMutedText,
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Language.values().forEach { lang ->
                            val isSel = currentLang == lang
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.repository.setLanguage(lang) },
                                color = if (isSel) IhwMaroon else IhwCreamBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) IhwMaroon else Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = lang.displayName,
                                    color = if (isSel) Color.White else IhwNavyText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // About & Policies
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About Indian Hair World",
                        color = IhwDarkMaroon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "IHW is a premier non-surgical hair replacement brand featured on Shark Tank India. Our Sodepur studio offers 100% human hair patches, wigs, toppers and maintenance in sanitized private cabins.",
                        color = IhwNavyText.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Terms of Service", color = IhwMutedText, fontSize = 11.sp)
                        Text("Privacy Policy", color = IhwMutedText, fontSize = 11.sp)
                    }
                }
            }
        }

        // Small Admin Entry at the bottom opening PIN screen
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Studio Admin Login",
                    color = IhwMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onAdminClick() }
                        .testTag("profile_admin_login_link")
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        var editName by remember { mutableStateOf(profile.name) }
        var editPhone by remember { mutableStateOf(profile.phone) }
        var editEmail by remember { mutableStateOf(profile.email) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) editPhone = it },
                        label = { Text("Mobile Number") },
                        prefix = { Text("+91 ") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email (Optional)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.saveUserProfile(editName, editPhone, editEmail)
                        viewModel.showToast("Profile updated!")
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IhwMaroon)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }
}
