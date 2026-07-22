package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddDonationDialog
import com.example.ui.components.AddInventoryDialog
import com.example.ui.components.AddShiftDialog
import com.example.ui.components.AddVolunteerDialog
import com.example.ui.components.AdminPinDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.StockAdjustDialog
import com.example.ui.screens.DonationsScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.VolunteersScreen
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.ShivaMandirTheme
import com.example.viewmodel.MandirViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShivaMandirTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: MandirViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isUnlocked by viewModel.isUnlocked.collectAsState()
    val showPinDialog by viewModel.showPinDialog.collectAsState()
    val pinError by viewModel.pinError.collectAsState()
    val selectedReceiptDonation by viewModel.selectedDonationForReceipt.collectAsState()

    val totalDonations by viewModel.totalDonationsAmount.collectAsState()
    val donations by viewModel.allDonations.collectAsState()
    val lowStockItems by viewModel.lowStockInventory.collectAsState()
    val shifts by viewModel.allShifts.collectAsState()
    val volunteers by viewModel.allVolunteers.collectAsState()

    val showAddDonation by viewModel.showAddDonationDialog.collectAsState()
    val showAddInventory by viewModel.showAddInventoryDialog.collectAsState()
    val showAddVolunteer by viewModel.showAddVolunteerDialog.collectAsState()
    val showAddShift by viewModel.showAddShiftDialog.collectAsState()

    val stockItemToAdjust by viewModel.stockItemToAdjust.collectAsState()
    val isAdjustingStockReplenish by viewModel.isAdjustingStockReplenish.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ॐ Shiva Mandir",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Management",
                            fontSize = 13.sp,
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    // Security PIN Lock/Unlock Status
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) SecurityGreen.copy(alpha = 0.9f)
                                else SaffronPrimary.copy(alpha = 0.9f)
                            )
                            .clickable { viewModel.openPinDialog() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Security Status",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isUnlocked) "UNLOCKED" else "PIN 1008",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndigoSecondary)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = (selectedTab == 0),
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Overview") },
                    label = { Text("Overview", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaffronPrimary,
                        selectedTextColor = SaffronPrimary
                    )
                )

                NavigationBarItem(
                    selected = (selectedTab == 1),
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Donations") },
                    label = { Text("Donations", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaffronPrimary,
                        selectedTextColor = SaffronPrimary
                    )
                )

                NavigationBarItem(
                    selected = (selectedTab == 2),
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(imageVector = Icons.Default.Inventory2, contentDescription = "Inventory") },
                    label = { Text("Inventory", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaffronPrimary,
                        selectedTextColor = SaffronPrimary
                    )
                )

                NavigationBarItem(
                    selected = (selectedTab == 3),
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Volunteers") },
                    label = { Text("Volunteers", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaffronPrimary,
                        selectedTextColor = SaffronPrimary
                    )
                )

                NavigationBarItem(
                    selected = (selectedTab == 4),
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(imageVector = Icons.Default.Shield, contentDescription = "Security") },
                    label = { Text("Security", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaffronPrimary,
                        selectedTextColor = SaffronPrimary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> OverviewScreen(
                    totalDonations = totalDonations ?: 0.0,
                    donations = donations,
                    lowStockItems = lowStockItems,
                    shifts = shifts,
                    isUnlocked = isUnlocked,
                    onNavigateTab = { viewModel.selectTab(it) },
                    onOpenAddDonation = { viewModel.showAddDonationDialog.value = true },
                    onOpenAddInventory = { viewModel.showAddInventoryDialog.value = true },
                    onOpenAddShift = { viewModel.showAddShiftDialog.value = true },
                    onOpenReceipt = { viewModel.showReceipt(it) },
                    onOpenPinDialog = { viewModel.openPinDialog() },
                    onReplenishStock = { item ->
                        viewModel.stockItemToAdjust.value = item
                        viewModel.isAdjustingStockReplenish.value = true
                    }
                )
                1 -> DonationsScreen(viewModel = viewModel)
                2 -> InventoryScreen(viewModel = viewModel)
                3 -> VolunteersScreen(viewModel = viewModel)
                4 -> SecurityScreen(viewModel = viewModel)
            }
        }
    }

    // Modal Dialogs
    if (showPinDialog) {
        AdminPinDialog(
            pinError = pinError,
            onVerifyPin = { viewModel.verifyPin(it) },
            onDismiss = { viewModel.dismissPinDialog() }
        )
    }

    if (selectedReceiptDonation != null) {
        ReceiptDialog(
            donation = selectedReceiptDonation!!,
            isUnlocked = isUnlocked,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }

    if (showAddDonation) {
        AddDonationDialog(
            onDismiss = { viewModel.showAddDonationDialog.value = false },
            onSubmit = { name, phone, email, amount, seva, mode, ref, notes ->
                viewModel.addDonation(name, phone, email, amount, seva, mode, ref, notes)
            }
        )
    }

    if (showAddInventory) {
        AddInventoryDialog(
            onDismiss = { viewModel.showAddInventoryDialog.value = false },
            onSubmit = { name, cat, qty, unit, minReorder, location, notes ->
                viewModel.addInventoryItem(name, cat, qty, unit, minReorder, location, notes)
            }
        )
    }

    if (stockItemToAdjust != null) {
        StockAdjustDialog(
            item = stockItemToAdjust!!,
            isReplenish = isAdjustingStockReplenish,
            onDismiss = { viewModel.stockItemToAdjust.value = null },
            onSubmit = { amount ->
                if (isAdjustingStockReplenish) {
                    viewModel.replenishStock(stockItemToAdjust!!.id, amount)
                } else {
                    viewModel.consumeStock(stockItemToAdjust!!.id, amount)
                }
            }
        )
    }

    if (showAddVolunteer) {
        AddVolunteerDialog(
            onDismiss = { viewModel.showAddVolunteerDialog.value = false },
            onSubmit = { name, phone, email, seva ->
                viewModel.addVolunteer(name, phone, email, seva)
            }
        )
    }

    if (showAddShift) {
        AddShiftDialog(
            volunteers = volunteers,
            onDismiss = { viewModel.showAddShiftDialog.value = false },
            onSubmit = { title, date, time, volId, volName, category, notes ->
                viewModel.addShift(title, date, time, volId, volName, category, notes)
            }
        )
    }
}
