package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InventoryItemEntity
import com.example.data.VolunteerEntity
import com.example.security.SecurityManager
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDonationDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        devoteeName: String,
        phone: String,
        email: String,
        amount: Double,
        sevaPurpose: String,
        paymentMode: String,
        paymentRef: String,
        notes: String
    ) -> Unit
) {
    var devoteeName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var sevaPurpose by remember { mutableStateOf("Rudrabhishek Seva") }
    var paymentMode by remember { mutableStateOf("UPI") }
    var paymentRef by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val sevaOptions = listOf(
        "Rudrabhishek Seva",
        "Annadanam Seva",
        "Mahashivratri Fund",
        "Akhand Jyoti Seva",
        "Temple Maintenance",
        "General Fund"
    )
    val paymentOptions = listOf("UPI", "Cash", "Card", "Bank Transfer")

    var expandedSeva by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Record Member Donation",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = devoteeName,
                    onValueChange = { devoteeName = it },
                    label = { Text("Devotee Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Seva Purpose Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedSeva,
                    onExpandedChange = { expandedSeva = !expandedSeva }
                ) {
                    OutlinedTextField(
                        value = sevaPurpose,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seva / Donation Purpose") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeva) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSeva,
                        onDismissRequest = { expandedSeva = false }
                    ) {
                        sevaOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sevaPurpose = option
                                    expandedSeva = false
                                }
                            )
                        }
                    }
                }

                // Payment Mode Selection
                Text(
                    text = "Payment Mode",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    paymentOptions.forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { paymentMode = mode }
                        ) {
                            RadioButton(
                                selected = (paymentMode == mode),
                                onClick = { paymentMode = mode }
                            )
                            Text(text = mode, fontSize = 11.sp)
                        }
                    }
                }

                // If UPI is selected, show official QR code Standee card
                if (paymentMode == "UPI") {
                    val currentAmount = amountText.toDoubleOrNull()
                    UpiPaymentQRCard(
                        amount = currentAmount,
                        modifier = Modifier.padding(vertical = 4.dp),
                        showActionButtons = true
                    )
                }

                OutlinedTextField(
                    value = paymentRef,
                    onValueChange = { paymentRef = it },
                    label = { Text("Payment Ref / Txn ID (e.g. UPI Ref / UTR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Prayer / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "🔒 Records are encrypted using AES-256 before saving to storage.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (devoteeName.isNotBlank() && amt > 0) {
                        onSubmit(
                            devoteeName,
                            phone,
                            email,
                            amt,
                            sevaPurpose,
                            paymentMode,
                            paymentRef.ifBlank { "N/A" },
                            notes
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save & Generate Receipt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        category: String,
        quantity: Double,
        unit: String,
        minReorderLevel: Double,
        storageLocation: String,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Puja Samagri") }
    var quantityText by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var minReorderText by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("Sanctum Store") }
    var notes by remember { mutableStateOf("") }

    val categoryOptions = listOf("Puja Samagri", "Sacred Offerings", "Temple Vessels", "Annadanam Provisions")
    val unitOptions = listOf("kg", "Liters", "Packets", "Pcs", "Boxes")

    var expandedCat by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Temple Inventory Item",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name (e.g., Cow Ghee, Camphor)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCat,
                    onExpandedChange = { expandedCat = !expandedCat }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }
                    ) {
                        categoryOptions.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (kg/Liters)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = minReorderText,
                    onValueChange = { minReorderText = it },
                    label = { Text("Low Stock Alert Level") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("Storage Location / Altar Cupboard") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Supplier details") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityText.toDoubleOrNull() ?: 0.0
                    val reorder = minReorderText.toDoubleOrNull() ?: 5.0
                    if (name.isNotBlank()) {
                        onSubmit(name, category, qty, unit, reorder, storageLocation, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun StockAdjustDialog(
    item: InventoryItemEntity,
    isReplenish: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isReplenish) "Replenish Stock: ${item.name}" else "Consume Stock: ${item.name}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Current Stock: ${item.quantity} ${item.unit}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Quantity to ${if (isReplenish) "Add" else "Consume"} (${item.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onSubmit(amt)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddVolunteerDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, email: String, seva: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var seva by remember { mutableStateOf("Aarti & Altar Setup") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Register Temple Volunteer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Volunteer Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = seva,
                    onValueChange = { seva = it },
                    label = { Text("Preferred Seva / Department") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSubmit(name, phone, email, seva)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Text("Register Volunteer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShiftDialog(
    volunteers: List<VolunteerEntity>,
    onDismiss: () -> Unit,
    onSubmit: (
        shiftTitle: String,
        dateString: String,
        timeSlot: String,
        volunteerId: Int,
        volunteerName: String,
        sevaCategory: String,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("Morning Shiv Aarti Duty") }
    var dateString by remember { mutableStateOf("2026-07-23") }
    var timeSlot by remember { mutableStateOf("05:00 AM - 08:00 AM") }
    var notes by remember { mutableStateOf("") }

    var selectedVolunteer by remember { mutableStateOf(volunteers.firstOrNull()) }
    var expandedVol by remember { mutableStateOf(false) }

    val volunteerName = selectedVolunteer?.let { SecurityManager.decrypt(it.nameEncrypted) } ?: "Unassigned Volunteer"
    val volunteerId = selectedVolunteer?.id ?: 0
    val sevaCategory = selectedVolunteer?.preferredSeva ?: "General Seva"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Schedule Volunteer Shift",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Shift Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Volunteer Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedVol,
                    onExpandedChange = { expandedVol = !expandedVol }
                ) {
                    OutlinedTextField(
                        value = volunteerName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign Volunteer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVol) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedVol,
                        onDismissRequest = { expandedVol = false }
                    ) {
                        volunteers.forEach { vol ->
                            val decryptedName = SecurityManager.decrypt(vol.nameEncrypted)
                            DropdownMenuItem(
                                text = { Text("$decryptedName (${vol.preferredSeva})") },
                                onClick = {
                                    selectedVolunteer = vol
                                    expandedVol = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        label = { Text("Time Slot") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Duties & Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(
                            title,
                            dateString,
                            timeSlot,
                            volunteerId,
                            volunteerName,
                            sevaCategory,
                            notes
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Text("Create Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
