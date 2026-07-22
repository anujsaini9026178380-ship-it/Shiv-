package com.example.data

import com.example.security.SecurityManager
import kotlinx.coroutines.flow.Flow

class MandirRepository(private val db: MandirDatabase) {

    private val donationDao = db.donationDao()
    private val inventoryDao = db.inventoryDao()
    private val volunteerDao = db.volunteerDao()
    private val shiftDao = db.shiftDao()

    // Donations & Receipts
    val allDonations: Flow<List<DonationEntity>> = donationDao.getAllDonations()
    val totalDonationsAmount: Flow<Double?> = donationDao.getTotalDonationsAmount()

    suspend fun getDonationById(id: Int): DonationEntity? = donationDao.getDonationById(id)

    suspend fun addDonation(
        devoteeName: String,
        phone: String,
        email: String,
        amount: Double,
        sevaPurpose: String,
        paymentMode: String,
        paymentRef: String,
        notes: String
    ): Long {
        // Auto-generate next receipt number
        val timestamp = System.currentTimeMillis()
        val randomSuffix = (1000..9999).random()
        val receiptNo = "SM-2026-$randomSuffix"

        val entity = DonationEntity(
            receiptNo = receiptNo,
            devoteeNameEncrypted = SecurityManager.encrypt(devoteeName),
            phoneEncrypted = SecurityManager.encrypt(phone),
            emailEncrypted = SecurityManager.encrypt(email),
            rawAmount = amount,
            amountEncrypted = SecurityManager.encrypt(amount.toString()),
            sevaPurpose = sevaPurpose,
            paymentMode = paymentMode,
            paymentRef = paymentRef,
            timestamp = timestamp,
            notesEncrypted = SecurityManager.encrypt(notes)
        )
        return donationDao.insertDonation(entity)
    }

    suspend fun deleteDonation(donation: DonationEntity) {
        donationDao.deleteDonation(donation)
    }

    // Inventory Management
    val allInventory: Flow<List<InventoryItemEntity>> = inventoryDao.getAllInventory()
    val lowStockInventory: Flow<List<InventoryItemEntity>> = inventoryDao.getLowStockInventory()

    suspend fun addInventoryItem(
        name: String,
        category: String,
        quantity: Double,
        unit: String,
        minReorderLevel: Double,
        storageLocation: String,
        notes: String
    ): Long {
        val item = InventoryItemEntity(
            name = name,
            category = category,
            quantity = quantity,
            unit = unit,
            minReorderLevel = minReorderLevel,
            storageLocation = storageLocation,
            notes = notes
        )
        return inventoryDao.insertItem(item)
    }

    suspend fun consumeInventory(itemId: Int, amount: Double): Boolean {
        val result = inventoryDao.consumeItem(itemId, amount)
        return result > 0
    }

    suspend fun replenishInventory(itemId: Int, amount: Double): Boolean {
        val result = inventoryDao.replenishItem(itemId, amount)
        return result > 0
    }

    suspend fun deleteInventoryItem(item: InventoryItemEntity) {
        inventoryDao.deleteItem(item)
    }

    // Volunteers & Scheduling
    val allVolunteers: Flow<List<VolunteerEntity>> = volunteerDao.getAllVolunteers()
    val allShifts: Flow<List<VolunteerShiftEntity>> = shiftDao.getAllShifts()

    suspend fun addVolunteer(
        name: String,
        phone: String,
        email: String,
        preferredSeva: String
    ): Long {
        val entity = VolunteerEntity(
            nameEncrypted = SecurityManager.encrypt(name),
            phoneEncrypted = SecurityManager.encrypt(phone),
            emailEncrypted = SecurityManager.encrypt(email),
            preferredSeva = preferredSeva
        )
        return volunteerDao.insertVolunteer(entity)
    }

    suspend fun addShift(
        shiftTitle: String,
        dateString: String,
        timeSlot: String,
        volunteerId: Int,
        volunteerName: String,
        sevaCategory: String,
        notes: String
    ): Long {
        val shift = VolunteerShiftEntity(
            shiftTitle = shiftTitle,
            dateString = dateString,
            timeSlot = timeSlot,
            volunteerId = volunteerId,
            volunteerName = volunteerName,
            sevaCategory = sevaCategory,
            status = "Scheduled",
            notes = notes
        )
        return shiftDao.insertShift(shift)
    }

    suspend fun updateShiftStatus(shiftId: Int, status: String) {
        shiftDao.updateShiftStatus(shiftId, status)
    }

    suspend fun deleteShift(shift: VolunteerShiftEntity) {
        shiftDao.deleteShift(shift)
    }
}
