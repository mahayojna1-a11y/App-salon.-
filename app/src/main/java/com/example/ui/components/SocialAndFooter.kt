package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SiteConfig
import com.example.ui.theme.*

@Composable
fun WhyChooseSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Why Choose Indian Hair World",
            color = IhwDarkMaroon,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // White card, 16px radius, soft shadow, 16px inner padding
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // One checklist of 6 points only
                val points = listOf(
                    "100% natural human hair systems with undetectable hairline",
                    "Same-day hair fixing completed in just 60 minutes",
                    "Featured on Shark Tank India with proven customer satisfaction",
                    "Affordable packages starting from Rs. 5,999/- with EMI options",
                    "Experienced specialists and private consultation cabins",
                    "Convenient location on RN Avenue, Sodepur (Open 7 days)"
                )

                points.forEach { pt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = IhwMaroon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = pt,
                            color = IhwNavyText,
                            fontSize = 13.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MaroonCtaBlock(
    phone: String,
    whatsAppNumber: String
) {
    val context = LocalContext.current

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
            Text(
                text = "Ready For Your Transformation?",
                color = IhwGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Visit our Sodepur studio or book your free consultation today.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Two buttons: gold "Call +91 89100 97668" and white "WhatsApp Us"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gold Call Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.replace(" ", "")}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("cta_call_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IhwGold,
                        contentColor = IhwDarkMaroon
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Call $phone",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                // White WhatsApp Us Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$whatsAppNumber?text=${Uri.encode("Hi IHW Sodepur! I would like to book a free consultation.")}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("cta_whatsapp_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = IhwDarkMaroon
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "WhatsApp Us",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleFooter(
    siteConfig: SiteConfig,
    onAdminClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Indian Hair World - Sodepur",
                color = IhwDarkMaroon,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = siteConfig.address,
                color = IhwNavyText,
                fontSize = 12.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Hours: 11:00 AM – 7:00 PM (Open 7 Days)",
                color = IhwNavyText,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Phone: ${siteConfig.phone}",
                color = IhwDarkMaroon,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "© 2026 Indian Hair World",
                    color = IhwMutedText,
                    fontSize = 11.sp
                )

                Text(
                    text = "Studio Admin",
                    color = IhwMutedText,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { onAdminClick() }
                )
            }
        }
    }
}
