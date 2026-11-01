package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.getServiceDrawableRes
import com.example.ui.theme.*
import com.example.viewmodel.IhwViewModel

@Composable
fun BookingFlowScreen(
    viewModel: IhwViewModel,
    onClose: () -> Unit
) {
    val draft by viewModel.bookingDraft.collectAsState()
    val services by viewModel.repository.services.collectAsState()
    val branches by viewModel.repository.branches.collectAsState()
    val isSubmitting by viewModel.isBookingSubmitting.collectAsState()

    BackHandler {
        viewModel.prevBookingStep()
    }

    val stepTitles = listOf(
        "Choose Service",
        "Customise",
        "Choose Branch",
        "Select Date",
        "Pick a Time Slot",
        "Confirm Booking"
    )

    Scaffold(
        topBar = {
            BookingTopBar(
                currentStep = draft.currentStep,
                stepTitle = stepTitles[draft.currentStep - 1],
                onBack = { viewModel.prevBookingStep() },
                onClose = onClose
            )
        },
        bottomBar = {
            if (draft.currentStep <= 6) {
                BookingStickyBottomBar(
                    draft = draft,
                    isSubmitting = isSubmitting,
                    onContinue = {
                        if (draft.currentStep == 6) {
                            viewModel.submitBooking()
                        } else {
                            viewModel.nextBookingStep()
                        }
                    }
                )
            }
        },
        containerColor = IhwCreamBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (draft.currentStep) {
                1 -> Step1ChooseService(viewModel = viewModel, draft = draft, services = services)
                2 -> Step2Customise(viewModel = viewModel, draft = draft)
                3 -> Step3ChooseBranch(viewModel = viewModel, draft = draft, branches = branches)
                4 -> Step4ChooseDate(viewModel = viewModel, draft = draft)
                5 -> Step5PickTimeSlot(viewModel = viewModel, draft = draft)
                6 -> Step6DetailsAndConfirm(viewModel = viewModel, draft = draft)
            }
        }
    }
}

@Composable
private fun BookingTopBar(
    currentStep: Int,
    stepTitle: String,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("booking_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = IhwNavyText
                    )
                }

                Text(
                    text = "Book Appointment",
                    color = IhwDarkMaroon,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onClose, modifier = Modifier.testTag("booking_close_btn")) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = IhwNavyText
                    )
                }
            }

            // 6 Connected Dots Progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 1..6) {
                        val isDoneOrCurrent = i <= currentStep
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isDoneOrCurrent) IhwBookingMaroon else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (i < currentStep) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        if (i < 6) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(if (i < currentStep) IhwBookingMaroon else Color(0xFFE2E8F0))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Step $currentStep of 6: $stepTitle",
                    color = IhwMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BookingStickyBottomBar(
    draft: BookingDraft,
    isSubmitting: Boolean,
    onContinue: () -> Unit
) {
    val branchName = draft.selectedBranch?.name ?: "Sodepur"
    val dateText = draft.selectedDateStr.ifEmpty { "Oct 31" }
    val timeText = draft.selectedTimeSlot.ifEmpty { "1:00 PM" }
    val priceText = if (draft.totalPrice == 0) "FREE" else "Rs. ${draft.totalPrice}"

    val isValid = when (draft.currentStep) {
        1 -> draft.selectedService != null
        2 -> true
        3 -> draft.selectedBranch != null
        4 -> draft.selectedDateStr.isNotBlank()
        5 -> draft.selectedTimeSlot.isNotBlank()
        6 -> draft.customerName.isNotBlank() && draft.customerPhone.length >= 10 && draft.agreedToContact
        else -> true
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$branchName | $dateText, $timeText",
                    color = IhwMutedText,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Text(
                    text = "Total: $priceText",
                    color = IhwDarkMaroon,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Button(
                onClick = onContinue,
                enabled = isValid && !isSubmitting,
                modifier = Modifier
                    .height(46.dp)
                    .testTag("booking_continue_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IhwBookingMaroon,
                    contentColor = Color.White,
                    disabledContainerColor = IhwBookingMaroon.copy(alpha = 0.35f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (draft.currentStep == 6) "Confirm Booking" else "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// =================== STEP 1: CHOOSE SERVICE ===================
@Composable
private fun Step1ChooseService(
    viewModel: IhwViewModel,
    draft: BookingDraft,
    services: List<ServiceItem>
) {
    val categoryServices = services.filter {
        it.customerType == draft.customerType && it.isNewWork == draft.isNewWork
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Select your service",
                color = IhwDarkMaroon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select your client type and service category",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        // 1. Gents / Ladies Selector
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Client Type",
                        color = IhwNavyText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CustomerType.values().forEach { cType ->
                            val isSel = draft.customerType == cType
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.updateDraft { it.copy(customerType = cType, selectedService = null) }
                                    },
                                color = if (isSel) IhwBookingMaroon else IhwCreamBg
                            ) {
                                Text(
                                    text = cType.label,
                                    color = if (isSel) Color.White else IhwNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Two Large Selectable Cards: New Work vs Servicing Work
        item {
            Text(
                text = "Appointment Type",
                color = IhwNavyText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // New Work Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.updateDraft { it.copy(isNewWork = true, selectedService = null) }
                        },
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (draft.isNewWork) 2.dp else 1.dp,
                        if (draft.isNewWork) IhwBookingMaroon else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "New Work",
                                color = IhwNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (draft.isNewWork) IhwBookingMaroon else Color(0xFFE2E8F0),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fresh patch fixing in 60 mins",
                            color = IhwMutedText,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Servicing Work Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.updateDraft { it.copy(isNewWork = false, selectedService = null) }
                        },
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (!draft.isNewWork) 2.dp else 1.dp,
                        if (!draft.isNewWork) IhwBookingMaroon else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Servicing Work",
                                color = IhwNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (!draft.isNewWork) IhwBookingMaroon else Color(0xFFE2E8F0),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Maintenance of existing patch",
                            color = IhwMutedText,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
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
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "First time? Book a FREE Consultation for expert advice.",
                        color = Color(0xFF92400E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Service Cards List
        items(categoryServices) { service ->
            val isSelected = draft.selectedService?.id == service.id
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        viewModel.updateDraft { it.copy(selectedService = service) }
                    }
                    .testTag("service_item_${service.id}"),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) IhwBookingMaroon else Color(0xFFE2E8F0)
                ),
                shadowElevation = if (isSelected) 3.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Service Image Thumbnail matching exact service
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2B1417))
                    ) {
                        AsyncImage(
                            model = getServiceDrawableRes(service.imageResName),
                            contentDescription = service.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = service.name,
                                color = IhwNavyText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = IhwBookingMaroon,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${service.durationMinutes} min",
                                color = IhwMutedText,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (service.price == 0) "FREE" else "Rs. ${service.price}",
                                color = IhwDarkMaroon,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            if (service.originalPrice > service.price && service.price > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Rs. ${service.originalPrice}",
                                    color = IhwMutedText,
                                    fontSize = 11.sp,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    Surface(
                        color = if (isSelected) IhwBookingMaroon else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IhwBookingMaroon)
                    ) {
                        Text(
                            text = if (isSelected) "Selected" else "Select",
                            color = if (isSelected) Color.White else IhwBookingMaroon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// =================== STEP 2: CUSTOMISE ===================
@Composable
private fun Step2Customise(
    viewModel: IhwViewModel,
    draft: BookingDraft
) {
    val methods = viewModel.repository.fixingMethods
    val colors = viewModel.repository.colorOptions

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Personalise your service",
                color = IhwDarkMaroon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select your preferences",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        // For Servicing Work: Show optional "last service date" picker
        if (!draft.isNewWork) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Last Service Date",
                            color = IhwNavyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "When was your patch last serviced?",
                            color = IhwMutedText,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Within 25 days", "30-45 days ago", "Over 45 days ago").forEach { period ->
                                val isSel = draft.lastServiceDate == period
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.updateDraft { it.copy(lastServiceDate = period) } },
                                    color = if (isSel) IhwBookingMaroon else IhwCreamBg
                                ) {
                                    Text(
                                        text = period,
                                        color = if (isSel) Color.White else IhwNavyText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // For New Work: 2x2 Method tiles
            item {
                Text(
                    text = "Method",
                    color = IhwNavyText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in 0 until methods.size step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MethodTileItem(
                                method = methods[i],
                                isSelected = draft.selectedMethod?.id == methods[i].id,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.updateDraft { it.copy(selectedMethod = methods[i]) } }
                            )
                            if (i + 1 < methods.size) {
                                MethodTileItem(
                                    method = methods[i + 1],
                                    isSelected = draft.selectedMethod?.id == methods[i + 1].id,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.updateDraft { it.copy(selectedMethod = methods[i + 1]) } }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Patch Colour swatches
        item {
            Text(
                text = "Gold / Lace Patches Shades",
                color = IhwNavyText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(colors) { colorOpt ->
                    val isSel = draft.selectedColor?.id == colorOpt.id
                    Surface(
                        modifier = Modifier
                            .width(105.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.updateDraft { it.copy(selectedColor = colorOpt) } },
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSel) 2.dp else 1.dp,
                            if (isSel) IhwBookingMaroon else Color(0xFFCBD5E1)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorOpt.hexColor))
                                    .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = colorOpt.name,
                                color = IhwNavyText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Optional Note (200 characters)
        item {
            Text(
                text = "Add an optional note",
                color = IhwNavyText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = draft.specialNote,
                onValueChange = { if (it.length <= 200) viewModel.updateDraft { d -> d.copy(specialNote = it) } },
                placeholder = { Text("Any special requirements? (Max 200 chars)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun MethodTileItem(
    method: FixingMethod,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) IhwBookingMaroon else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = method.name,
                    color = IhwNavyText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isSelected) IhwBookingMaroon else Color(0xFFE2E8F0),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = method.subtitle,
                color = IhwMutedText,
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }
    }
}

// =================== STEP 3: BRANCH ===================
@Composable
private fun Step3ChooseBranch(
    viewModel: IhwViewModel,
    draft: BookingDraft,
    branches: List<Branch>
) {
    val context = LocalContext.current
    val sodepurBranch = branches.firstOrNull { it.id == "sodepur" } ?: branches.first()

    LaunchedEffect(Unit) {
        if (draft.selectedBranch == null) {
            viewModel.updateDraft { it.copy(selectedBranch = sodepurBranch) }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Select a studio",
                color = IhwDarkMaroon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Find a branch near you",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.dp, IhwBookingMaroon),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = sodepurBranch.name,
                            color = IhwNavyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IhwBookingMaroon,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sodepurBranch.address,
                        color = IhwMutedText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Open 11:00 AM – 7:00 PM (7 Days)",
                        color = IhwWhatsApp,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sodepurBranch.phone.replace(" ", "")}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Call", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode("Indian Hair World Sodepur")}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Maps", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// =================== STEP 4: DATE ===================
@Composable
private fun Step4ChooseDate(
    viewModel: IhwViewModel,
    draft: BookingDraft
) {
    var selectedDayIndex by remember { mutableIntStateOf(31) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Choose a date",
                color = IhwDarkMaroon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "When would you like to visit?",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        // Chips: Today, Tomorrow, This Weekend
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Today", "Tomorrow", "This Weekend").forEach { chip ->
                    val isSel = draft.selectedDateStr.contains(chip)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                viewModel.updateDraft { it.copy(selectedDateStr = "$chip, Oct 31, 2026") }
                            },
                        color = if (isSel) IhwBookingMaroon else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) IhwBookingMaroon else Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = chip,
                            color = if (isSel) Color.White else IhwNavyText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Month Calendar
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "October 2026",
                        color = IhwNavyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDays.forEach { d ->
                            Text(
                                text = d,
                                color = IhwMutedText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    for (row in 0 until 5) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (col in 0 until 7) {
                                val dayNum = (row * 7 + col) - 2
                                if (dayNum in 1..31) {
                                    val isSelected = selectedDayIndex == dayNum
                                    val isPast = dayNum < 31

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) IhwBookingMaroon else Color.Transparent)
                                            .clickable(enabled = !isPast) {
                                                selectedDayIndex = dayNum
                                                viewModel.updateDraft { it.copy(selectedDateStr = "Oct $dayNum, 2026") }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$dayNum",
                                            color = when {
                                                isSelected -> Color.White
                                                isPast -> Color(0xFFCBD5E1)
                                                else -> IhwNavyText
                                            },
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== STEP 5: TIME ===================
@Composable
private fun Step5PickTimeSlot(
    viewModel: IhwViewModel,
    draft: BookingDraft
) {
    // 1-hour slots from 11 AM to 7 PM only: 11 AM, 12 PM, 1 PM, 2 PM, 3 PM, 4 PM, 5 PM, 6 PM
    val morningSlots = listOf("11:00 AM")
    val afternoonSlots = listOf("12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM")
    val eveningSlots = listOf("4:00 PM", "5:00 PM", "6:00 PM")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = draft.selectedDateStr.ifEmpty { "Saturday, Oct 31, 2026" },
                color = IhwDarkMaroon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Available 1-hour slots for ${draft.customerType.label} chair",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TimeColumn("Morning", morningSlots, viewModel, draft, Modifier.weight(1f))
                TimeColumn("Afternoon", afternoonSlots, viewModel, draft, Modifier.weight(1f))
                TimeColumn("Evening", eveningSlots, viewModel, draft, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TimeColumn(
    label: String,
    slots: List<String>,
    viewModel: IhwViewModel,
    draft: BookingDraft,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = IhwNavyText,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        slots.forEach { slot ->
            val isAvailable = viewModel.repository.isSlotAvailable(draft.selectedDateStr, slot, draft.customerType)
            val isSelected = draft.selectedTimeSlot == slot

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = isAvailable) {
                        viewModel.updateDraft { it.copy(selectedTimeSlot = slot) }
                    },
                color = when {
                    isSelected -> IhwBookingMaroon
                    !isAvailable -> Color(0xFFF1F5F9)
                    else -> Color.White
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) IhwBookingMaroon else Color(0xFFE2E8F0)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isAvailable) {
                        Text(
                            text = "$slot • Booked",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        Text(
                            text = slot,
                            color = if (isSelected) Color.White else IhwNavyText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// =================== STEP 6: DETAILS AND CONFIRM ===================
@Composable
private fun Step6DetailsAndConfirm(
    viewModel: IhwViewModel,
    draft: BookingDraft
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Your details",
                color = IhwDarkMaroon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Please enter your contact details",
                color = IhwMutedText,
                fontSize = 13.sp
            )
        }

        // Full Name (required)
        item {
            OutlinedTextField(
                value = draft.customerName,
                onValueChange = { viewModel.updateDraft { d -> d.copy(customerName = it) } },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Phone with +91 and 10-digit validation (required)
        item {
            OutlinedTextField(
                value = draft.customerPhone,
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) viewModel.updateDraft { d -> d.copy(customerPhone = it) } },
                label = { Text("Mobile Number *") },
                prefix = { Text("+91 ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Email (optional)
        item {
            OutlinedTextField(
                value = draft.customerEmail,
                onValueChange = { viewModel.updateDraft { d -> d.copy(customerEmail = it) } },
                label = { Text("Email (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Review your booking card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Review your booking",
                        color = IhwDarkMaroon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    BookingReviewRow("Category", "${draft.customerType.label} Chair", onEdit = { viewModel.jumpToBookingStep(1) })
                    BookingReviewRow("Work Type", if (draft.isNewWork) "New Work" else "Servicing Work", onEdit = { viewModel.jumpToBookingStep(1) })
                    BookingReviewRow("Service", draft.selectedService?.name ?: "Hair System", onEdit = { viewModel.jumpToBookingStep(1) })
                    BookingReviewRow("Studio", draft.selectedBranch?.name ?: "Sodepur", onEdit = { viewModel.jumpToBookingStep(3) })
                    BookingReviewRow("Date & Time", "${draft.selectedDateStr}, ${draft.selectedTimeSlot}", onEdit = { viewModel.jumpToBookingStep(4) })
                    BookingReviewRow("Price", if (draft.totalPrice == 0) "FREE" else "Rs. ${draft.totalPrice} (Pay at studio)", onEdit = { viewModel.jumpToBookingStep(1) })
                }
            }
        }

        // WhatsApp / Call consent checkbox
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.updateDraft { it.copy(agreedToContact = !it.agreedToContact) } },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = draft.agreedToContact,
                    onCheckedChange = { checked -> viewModel.updateDraft { it.copy(agreedToContact = checked) } },
                    colors = CheckboxDefaults.colors(checkedColor = IhwBookingMaroon)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "I agree to be contacted on WhatsApp / call about this booking",
                    color = IhwNavyText,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun BookingReviewRow(label: String, value: String, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = IhwMutedText, fontSize = 11.sp)
            Text(value, color = IhwNavyText, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        Text(
            text = "Edit",
            color = IhwBookingMaroon,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.clickable { onEdit() }
        )
    }
}
