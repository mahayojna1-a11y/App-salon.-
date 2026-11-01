package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerType
import com.example.model.TransformationCase
import com.example.ui.theme.*
import com.example.viewmodel.IhwViewModel

@Composable
fun TransformationsGalleryScreen(
    viewModel: IhwViewModel,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf<CustomerType?>(null) }
    val cases = viewModel.repository.getTransformations()

    val filtered = if (selectedFilter == null) cases else cases.filter { it.customerType == selectedFilter }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
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
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("gallery_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hair Transformations Gallery",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = IhwCreamBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(null to "All", CustomerType.MEN to "Men's Systems", CustomerType.WOMEN to "Women's Toppers").forEach { (type, label) ->
                        val isSel = selectedFilter == type
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedFilter = type },
                            color = if (isSel) IhwBookingMaroon else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) IhwBookingMaroon else Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) Color.White else IhwNavyText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            items(filtered) { item ->
                TransformationGalleryCard(
                    item = item,
                    onBook = {
                        viewModel.startBooking()
                    }
                )
            }
        }
    }
}

@Composable
private fun TransformationGalleryCard(
    item: TransformationCase,
    onBook: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = IhwMaroonTint,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${item.customerType.label} • ${item.duration}",
                        color = IhwDarkMaroon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.method,
                        color = IhwSuccessGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                color = IhwDarkMaroon,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Before / After visual representation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Before box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("BEFORE", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.beforeDescription, color = IhwNavyText, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }

                // After box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, IhwSuccessGreen)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("AFTER (60 MINS)", color = IhwSuccessGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.afterDescription, color = IhwNavyText, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth().height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IhwBookingMaroon),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Book Free Consultation for this Look", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
