package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DonationEntity
import com.example.security.SecurityManager
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SecurityGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptDialog(
    donation: DonationEntity,
    isUnlocked: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val devoteeName = SecurityManager.decrypt(donation.devoteeNameEncrypted)
    val devoteePhone = if (isUnlocked) SecurityManager.decrypt(donation.phoneEncrypted) else SecurityManager.getMaskedPhone(donation.phoneEncrypted)
    val devoteeEmail = if (isUnlocked) SecurityManager.decrypt(donation.emailEncrypted) else "****@email.com"
    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(donation.timestamp))
    val amountInWords = SecurityManager.convertAmountToWords(donation.rawAmount)

    val receiptText = """
        ========================================
        SHRI SHRI MOTE SHIV NATH MANDIR TRUST
        UPI: 0113224768b@mairtel (Airtel Payments Bank)
        Reg. No. SMT-1008/2012 | 80G Tax Exempted
        OFFICIAL DONATION RECEIPT
        ========================================
        Receipt No: ${donation.receiptNo}
        Date: $formattedDate
        
        Devotee Name: $devoteeName
        Phone: $devoteePhone
        Seva / Purpose: ${donation.sevaPurpose}
        
        Amount Received: ₹${String.format(Locale.US, "%.2f", donation.rawAmount)}
        In Words: $amountInWords
        Payment Mode: ${donation.paymentMode} (${donation.paymentRef})
        
        Security: AES-256 Record Encrypted
        ========================================
        May Lord Shiva bless you and your family!
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Outer Receipt Card Frame
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, SaffronPrimary, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Om Header & Mandir Name
                        Text(
                            text = "ॐ",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        Text(
                            text = "SHRI SHRI MOTE SHIV NATH MANDIR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = IndigoSecondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "UPI: 0113224768b@mairtel • Airtel Payments Bank",
                            fontSize = 11.sp,
                            color = SaffronPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Reg SMT-1008/2012 • 80G Tax Exemption Certified",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = SaffronPrimary.copy(alpha = 0.3f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Receipt Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SaffronContainer)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OFFICIAL SEVA RECEIPT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Receipt Attributes
                        ReceiptRow("Receipt No:", donation.receiptNo, isBoldValue = true)
                        ReceiptRow("Date & Time:", formattedDate)
                        ReceiptRow("Devotee Name:", devoteeName, isBoldValue = true)
                        ReceiptRow("Contact No:", devoteePhone)
                        ReceiptRow("Email:", devoteeEmail)
                        ReceiptRow("Seva / Purpose:", donation.sevaPurpose, isHighlight = true)
                        ReceiptRow("Payment Mode:", "${donation.paymentMode} (${donation.paymentRef})")

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.LightGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Amount Highlight Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(IndigoSecondary)
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "AMOUNT RECEIVED",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹${String.format(Locale.US, "%,.2f", donation.rawAmount)}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFDE68A)
                                )
                                Text(
                                    text = amountInWords,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mandir Seal & Security Stamp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = SecurityGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AES-256 Encrypted Record",
                                        fontSize = 10.sp,
                                        color = SecurityGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Digital Mandir Seal Verified",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Pandit / Trustee",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoSecondary
                                )
                                Text(
                                    text = "Shiva Mandir Management",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(receiptText))
                            Toast.makeText(context, "Receipt text copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Copy", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Receipt ready for Print / Share", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Share", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Close Receipt", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBoldValue: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBoldValue || isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) SaffronPrimary else Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}
