package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DonationEntity
import com.example.data.InventoryItemEntity
import com.example.data.VolunteerShiftEntity
import com.example.security.SecurityManager
import com.example.ui.components.HeaderBanner
import com.example.ui.components.UpiPaymentQRCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SecurityGreen
import java.util.Locale

@Composable
fun OverviewScreen(
    totalDonations: Double,
    donations: List<DonationEntity>,
    lowStockItems: List<InventoryItemEntity>,
    shifts: List<VolunteerShiftEntity>,
    isUnlocked: Boolean,
    onNavigateTab: (Int) -> Unit,
    onOpenAddDonation: () -> Unit,
    onOpenAddInventory: () -> Unit,
    onOpenAddShift: () -> Unit,
    onOpenReceipt: (DonationEntity) -> Unit,
    onOpenPinDialog: () -> Unit,
    onReplenishStock: (InventoryItemEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            HeaderBanner(
                isUnlocked = isUnlocked,
                onLockClick = onOpenPinDialog
            )
        }

        // Quick Stats Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "TEMPLE OVERVIEW SUMMARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Donations",
                        value = "₹${String.format(Locale.US, "%,.0f", totalDonations)}",
                        icon = Icons.Default.CurrencyRupee,
                        accentColor = SaffronPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTab(1) }
                    )

                    StatCard(
                        title = "Low Stock Alerts",
                        value = "${lowStockItems.size} Items",
                        icon = Icons.Default.Warning,
                        accentColor = if (lowStockItems.isNotEmpty()) AlertRed else SecurityGreen,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTab(2) }
                    )

                    StatCard(
                        title = "Active Shifts",
                        value = "${shifts.size} Shifts",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = IndigoSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTab(3) }
                    )
                }
            }
        }

        // Action Shortcuts
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        QuickActionButton(
                            label = "+ New Donation",
                            icon = Icons.Default.ReceiptLong,
                            color = SaffronPrimary,
                            onClick = onOpenAddDonation
                        )
                    }
                    item {
                        QuickActionButton(
                            label = "+ Add Inventory",
                            icon = Icons.Default.Inventory2,
                            color = IndigoSecondary,
                            onClick = onOpenAddInventory
                        )
                    }
                    item {
                        QuickActionButton(
                            label = "+ Schedule Shift",
                            icon = Icons.Default.CalendarMonth,
                            color = Color(0xFF0D9488),
                            onClick = onOpenAddShift
                        )
                    }
                    item {
                        QuickActionButton(
                            label = if (isUnlocked) "Lock Session" else "Admin PIN",
                            icon = Icons.Default.Lock,
                            color = if (isUnlocked) SecurityGreen else Color.DarkGray,
                            onClick = onOpenPinDialog
                        )
                    }
                }
            }
        }

        // Official Temple UPI QR Standee Card
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "OFFICIAL TEMPLE UPI QR PAYMENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                UpiPaymentQRCard(
                    amount = null,
                    showActionButtons = true
                )
            }
        }

        // Low Stock Alert Cards
        if (lowStockItems.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOW INVENTORY ALERTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "View All Inventory >",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary,
                            modifier = Modifier.clickable { onNavigateTab(2) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        lowStockItems.take(3).forEach { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = AlertRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = item.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Current: ${item.quantity} ${item.unit} (Min: ${item.minReorderLevel})",
                                                fontSize = 12.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onReplenishStock(item) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("+ Stock Up", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Volunteer Shifts
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UPCOMING VOLUNTEER SEVA SHIFTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Volunteer Roster >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary,
                        modifier = Modifier.clickable { onNavigateTab(3) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (shifts.isEmpty()) {
                    Text(
                        text = "No shifts scheduled. Click '+ Schedule Shift' to assign volunteers.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        shifts.take(3).forEach { shift ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = shift.shiftTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = IndigoSecondary
                                        )
                                        Text(
                                            text = "👤 ${shift.volunteerName} • 🕒 ${shift.timeSlot}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "Seva: ${shift.sevaCategory}",
                                            fontSize = 11.sp,
                                            color = SaffronPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (shift.status == "Completed") SecurityGreen.copy(alpha = 0.15f)
                                                else SaffronContainer
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = shift.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (shift.status == "Completed") SecurityGreen else SaffronPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Member Donations
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT MEMBER DONATIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "All Donations & Receipts >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary,
                        modifier = Modifier.clickable { onNavigateTab(1) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    donations.take(4).forEach { donation ->
                        val devoteeName = SecurityManager.decrypt(donation.devoteeNameEncrypted)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = devoteeName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = "Encrypted",
                                            tint = SecurityGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Text(
                                        text = "${donation.sevaPurpose} • ${donation.paymentMode}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "Receipt: ${donation.receiptNo}",
                                        fontSize = 10.sp,
                                        color = SaffronPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${String.format(Locale.US, "%,.2f", donation.rawAmount)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = IndigoSecondary
                                    )
                                    Button(
                                        onClick = { onOpenReceipt(donation) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronContainer),
                                        modifier = Modifier.height(30.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Receipt",
                                            color = SaffronPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = accentColor)
            Text(text = title, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
