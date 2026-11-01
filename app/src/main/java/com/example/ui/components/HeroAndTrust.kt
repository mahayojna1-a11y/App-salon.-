package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HeroSection(
    startingPrice: Int,
    onBookConsultation: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(IhwMaroon)
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Small gold label
            Text(
                text = "Indian Hair World, Sodepur",
                color = IhwGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Gold serif italic headline
            Text(
                text = "Hair Patch, Wigs & Non-Surgical Hair Replacement in Sodepur",
                color = IhwGold,
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // One line
            Text(
                text = "Look Natural. Feel Confident. Walk Out in 60 Minutes.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cream price card: "Starting at Rs. 5,999/-" with "EMI Available"
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = IhwCreamCard,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Starting at Rs. $startingPrice/-",
                            color = IhwDarkMaroon,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "100% Natural Human Hair",
                            color = IhwNavyText,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IhwGold)
                    ) {
                        Text(
                            text = "EMI Available",
                            color = IhwDarkMaroon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Two full-width buttons side by side: maroon "Book Free Consultation" and green "WhatsApp Us"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onBookConsultation,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp)
                        .wrapContentHeight()
                        .testTag("hero_book_consultation_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IhwDarkMaroon,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Book Free Consultation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }

                Button(
                    onClick = onWhatsAppClick,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp)
                        .wrapContentHeight()
                        .testTag("hero_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IhwWhatsApp,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "WhatsApp Us",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Row of 4 small trust icons with labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrustIconItem("100K+ Happy Clients", Icons.Default.People)
                TrustIconItem("Non-Surgical & Safe", Icons.Default.Shield)
                TrustIconItem("Premium Quality", Icons.Default.Verified)
                TrustIconItem("Expert Support", Icons.Default.HeadsetMic)
            }
        }
    }
}

@Composable
private fun TrustIconItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IhwGold,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

@Composable
fun SharkTankBanner(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color(0xFF0F172A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FEATURED ON SHARK TANK INDIA",
                color = IhwGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = IhwGold,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
