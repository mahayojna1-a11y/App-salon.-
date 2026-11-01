package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.IhwViewModel

@Composable
fun CustomerHomeScreen(
    viewModel: IhwViewModel,
    onBookNow: (ServiceItem?) -> Unit,
    onViewAllTransformations: () -> Unit,
    onOpenMyBookings: () -> Unit,
    onAdminLogin: () -> Unit
) {
    val context = LocalContext.current
    val siteConfig by viewModel.repository.siteConfig.collectAsState()
    val mainTab by viewModel.mainTab.collectAsState()
    val audience by viewModel.customerAudience.collectAsState()
    val services by viewModel.repository.services.collectAsState()
    val products by viewModel.repository.products.collectAsState()
    val activeVideo by viewModel.activeVideo.collectAsState()

    val menProducts = products.filter { it.customerType == CustomerType.MEN && it.isVisible }
    val womenProducts = products.filter { it.customerType == CustomerType.WOMEN && it.isVisible }

    Scaffold(
        topBar = {
            // 1. Header: maroon bar, IHW logo on left, gold round Call button and menu icon on right
            IhwHeader(
                phone = siteConfig.phone,
                onCallClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${siteConfig.phone.replace(" ", "")}"))
                    context.startActivity(intent)
                }
            )
        },
        bottomBar = {
            // Fixed 4-Tab Bottom Navigation: Home, Services, Bookings, Profile
            IhwBottomNavigationBar(
                activeTab = mainTab,
                onTabSelect = { tab -> viewModel.setMainTab(tab) }
            )
        },
        containerColor = IhwCreamBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = mainTab,
                label = "customer_tab_transition"
            ) { tab ->
                when (tab) {
                    MainTab.HOME -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            // 2. Men / Women pill toggle (centered, small)
                            item {
                                CustomerAudienceSegmentedToggle(
                                    selectedType = audience,
                                    onSelect = { viewModel.setCustomerAudience(it) }
                                )
                            }

                            // 3. Festival banner image (one clean rounded image)
                            if (siteConfig.festivalBannerEnabled) {
                                item {
                                    FestivalBannerImage(bannerText = siteConfig.festivalBannerText)
                                }
                            }

                            // 4. Hero on maroon background
                            item {
                                HeroSection(
                                    startingPrice = siteConfig.startingPrice,
                                    onBookConsultation = { onBookNow(null) },
                                    onWhatsAppClick = {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://wa.me/${siteConfig.whatsAppNumber}?text=${Uri.encode("Hi IHW! I want to book a free consultation at Sodepur studio.")}")
                                        )
                                        context.startActivity(intent)
                                    }
                                )
                            }

                            // 5. Our Services Grid directly after Hero
                            item {
                                OurServicesGridSection(
                                    services = services,
                                    onBookService = { service -> onBookNow(service) },
                                    onViewAllServices = { viewModel.setMainTab(MainTab.SERVICES) }
                                )
                            }

                            // 6. "Featured on Shark Tank India" banner
                            if (siteConfig.sharkTankEnabled) {
                                item {
                                    SharkTankBanner(onClick = { onBookNow(null) })
                                }
                            }


                            // 9. Men's Hair Products: one horizontal row, 4 cards
                            item {
                                CleanProductRow(
                                    title = "Men's Hair Products",
                                    products = menProducts,
                                    onSelectProduct = { onBookNow(null) }
                                )
                            }

                            // 10. Women's Hair Products: same card style, one row
                            item {
                                CleanProductRow(
                                    title = "Women's Hair Products",
                                    products = womenProducts,
                                    onSelectProduct = { onBookNow(null) }
                                )
                            }

                            // 11. Why Choose IHW: one check-list of 6 points only
                            item {
                                WhyChooseSection()
                            }

                            // 12. Maroon CTA block with gold "Call +91 89100 97668" and white "WhatsApp Us"
                            item {
                                MaroonCtaBlock(
                                    phone = siteConfig.phone,
                                    whatsAppNumber = siteConfig.whatsAppNumber
                                )
                            }

                            // 13. Simple footer: address, hours 11 AM to 7 PM, phone
                            item {
                                SimpleFooter(
                                    siteConfig = siteConfig,
                                    onAdminClick = onAdminLogin
                                )
                            }
                        }
                    }

                    MainTab.SERVICES -> {
                        ServicesTabScreen(
                            viewModel = viewModel,
                            onBookService = { service -> onBookNow(service) }
                        )
                    }

                    MainTab.BOOKINGS -> {
                        BookingsTabScreen(
                            viewModel = viewModel,
                            onBookNow = { onBookNow(null) }
                        )
                    }

                    MainTab.PROFILE -> {
                        ProfileTabScreen(
                            viewModel = viewModel,
                            onAdminClick = onAdminLogin
                        )
                    }
                }
            }

            // Exactly ONE floating element: round blue Call button (bottom right)
            FloatingBlueCallButton(
                onCallClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${siteConfig.phone.replace(" ", "")}"))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp)
            )
        }
    }

}
