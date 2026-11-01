package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ProductItem
import com.example.model.ServiceItem
import com.example.ui.theme.*

fun getServiceDrawableRes(imageResName: String): Int {
    return when (imageResName) {
        "img_service_hair_patch" -> com.example.R.drawable.img_service_hair_patch
        "img_service_patch_servicing" -> com.example.R.drawable.img_service_patch_servicing
        "img_service_wig" -> com.example.R.drawable.img_service_wig
        "img_service_topper" -> com.example.R.drawable.img_service_topper
        "img_service_extensions" -> com.example.R.drawable.img_service_extensions
        "img_service_consultation" -> com.example.R.drawable.img_service_consultation
        else -> com.example.R.drawable.img_service_hair_patch
    }
}

@Composable
fun OurServicesGridSection(
    services: List<ServiceItem>,
    onBookService: (ServiceItem) -> Unit,
    onViewAllServices: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Our Services",
                color = IhwDarkMaroon,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "View all services →",
                color = IhwMaroon,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onViewAllServices() }
                    .testTag("view_all_services_link")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2-column grid of image cards
        val serviceList = services.take(6)
        for (i in 0 until serviceList.size step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ServiceImageCard(
                    service = serviceList[i],
                    onBook = { onBookService(serviceList[i]) },
                    modifier = Modifier.weight(1f)
                )

                if (i + 1 < serviceList.size) {
                    ServiceImageCard(
                        service = serviceList[i + 1],
                        onBook = { onBookService(serviceList[i + 1]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ServiceImageCard(
    service: ServiceItem,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 4:3 Image card matching exact service
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2B1417))
            ) {
                AsyncImage(
                    model = getServiceDrawableRes(service.imageResName),
                    contentDescription = service.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Category overlay pill
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp),
                    color = IhwDarkMaroon.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (service.isNewWork) "New Work" else "Servicing",
                        color = IhwGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (service.price == 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        color = IhwWhatsApp,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "FREE",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = service.name,
                color = IhwNavyText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = service.description,
                color = IhwMutedText,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (service.price == 0) "Free" else "From ₹${service.price}",
                    color = IhwDarkMaroon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = IhwMutedText,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${service.durationMinutes}m",
                        color = IhwMutedText,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Full-width maroon Book Now button
            Button(
                onClick = onBook,
                colors = ButtonDefaults.buttonColors(
                    containerColor = IhwMaroon,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("service_card_book_btn_${service.id}"),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Book Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CleanProductRow(
    title: String,
    products: List<ProductItem>,
    onSelectProduct: (ProductItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = title,
            color = IhwDarkMaroon,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products.take(4)) { product ->
                CleanProductCard(product = product, onClick = { onSelectProduct(product) })
            }
        }
    }
}

@Composable
private fun CleanProductCard(
    product: ProductItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column {
            // Image box placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFF2B1417))
            ) {
                // Small TRY-ON tag
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = IhwMaroon,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "TRY-ON",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = product.brand,
                    color = IhwGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Name, price, strike price, discount
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.name,
                    color = IhwNavyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "₹${product.price}",
                        color = IhwDarkMaroon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "₹${product.originalPrice}",
                        color = IhwMutedText,
                        fontSize = 11.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${product.discountPercent}% OFF",
                    color = IhwWhatsApp,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
