package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerType
import com.example.model.ServiceCategory
import com.example.model.ServiceItem
import com.example.ui.components.CustomerAudienceSegmentedToggle
import com.example.ui.components.ServiceImageCard
import com.example.ui.theme.*
import com.example.viewmodel.IhwViewModel

@Composable
fun ServicesTabScreen(
    viewModel: IhwViewModel,
    onBookService: (ServiceItem) -> Unit
) {
    val services by viewModel.repository.services.collectAsState()
    val audience by viewModel.customerAudience.collectAsState()
    var selectedCategory by remember { mutableStateOf(ServiceCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = services.filter { srv ->
        srv.customerType == audience &&
        (selectedCategory == ServiceCategory.ALL || srv.category == selectedCategory) &&
        (searchQuery.isBlank() || srv.name.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IhwCreamBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Services",
                color = IhwDarkMaroon,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choose your customized hair system or servicing",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        // Gents / Ladies pill toggle
        item {
            CustomerAudienceSegmentedToggle(
                selectedType = audience,
                onSelect = { viewModel.setCustomerAudience(it) }
            )
        }

        // Filter chips: All, Patch, Wig, Topper, Extensions, Servicing, Consultation
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ServiceCategory.values()) { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedCategory = cat },
                        color = if (isSel) IhwMaroon else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) IhwMaroon else Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = cat.label,
                            color = if (isSel) Color.White else IhwNavyText,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Search box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search services...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IhwMutedText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("services_tab_search"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        // Banner: "First time? Book a FREE Consultation"
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "First time? Book a FREE Consultation for expert scalp analysis.",
                        color = Color(0xFF92400E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 2-column grid of image cards
        val list = filtered
        for (i in 0 until list.size step 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ServiceImageCard(
                        service = list[i],
                        onBook = { onBookService(list[i]) },
                        modifier = Modifier.weight(1f)
                    )

                    if (i + 1 < list.size) {
                        ServiceImageCard(
                            service = list[i + 1],
                            onBook = { onBookService(list[i + 1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
