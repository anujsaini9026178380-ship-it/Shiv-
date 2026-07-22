package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiptNo: String,
    val devoteeNameEncrypted: String,
    val phoneEncrypted: String,
    val emailEncrypted: String,
    val rawAmount: Double,
    val amountEncrypted: String,
    val sevaPurpose: String,
    val paymentMode: String,
    val paymentRef: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val notesEncrypted: String = ""
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val quantity: Double,
    val unit: String,
    val minReorderLevel: Double,
    val storageLocation: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameEncrypted: String,
    val phoneEncrypted: String,
    val emailEncrypted: String,
    val preferredSeva: String,
    val active: Boolean = true
)

@Entity(tableName = "volunteer_shifts")
data class VolunteerShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shiftTitle: String,
    val dateString: String,
    val timeSlot: String,
    val volunteerId: Int = 0,
    val volunteerName: String,
    val sevaCategory: String,
    val status: String = "Scheduled",
    val notes: String = ""
)
