package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DonationEntity
import com.example.data.InventoryItemEntity
import com.example.data.MandirDatabase
import com.example.data.MandirRepository
import com.example.data.VolunteerEntity
import com.example.data.VolunteerShiftEntity
import com.example.security.SecurityManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MandirViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MandirRepository

    init {
        val db = MandirDatabase.getDatabase(application)
        repository = MandirRepository(db)
    }

    // Active Navigation Tab Index (0: Overview, 1: Donations, 2: Inventory, 3: Volunteers, 4: Security)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // Reactive StateFlows from Repository
    val allDonations: StateFlow<List<DonationEntity>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDonationsAmount: StateFlow<Double?> = repository.totalDonationsAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val allInventory: StateFlow<List<InventoryItemEntity>> = repository.allInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockInventory: StateFlow<List<InventoryItemEntity>> = repository.lowStockInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVolunteers: StateFlow<List<VolunteerEntity>> = repository.allVolunteers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allShifts: StateFlow<List<VolunteerShiftEntity>> = repository.allShifts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Security Unlock State
    private val _isUnlocked = MutableStateFlow(SecurityManager.isUnlocked())
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _showPinDialog = MutableStateFlow(false)
    val showPinDialog: StateFlow<Boolean> = _showPinDialog.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    fun openPinDialog() {
        _pinError.value = null
        _showPinDialog.value = true
    }

    fun dismissPinDialog() {
        _showPinDialog.value = false
    }

    fun verifyPin(pin: String): Boolean {
        val success = SecurityManager.verifyPin(pin)
        if (success) {
            _isUnlocked.value = true
            _showPinDialog.value = false
            _pinError.value = null
        } else {
            _pinError.value = "Incorrect Security PIN. Try '1008'"
        }
        return success
    }

    fun lockSession() {
        SecurityManager.lockSession()
        _isUnlocked.value = false
    }

    // Selected donation for automated receipt generation
    private val _selectedDonationForReceipt = MutableStateFlow<DonationEntity?>(null)
    val selectedDonationForReceipt: StateFlow<DonationEntity?> = _selectedDonationForReceipt.asStateFlow()

    fun showReceipt(donation: DonationEntity) {
        _selectedDonationForReceipt.value = donation
    }

    fun dismissReceipt() {
        _selectedDonationForReceipt.value = null
    }

    // Dialog state toggles
    val showAddDonationDialog = MutableStateFlow(false)
    val showAddInventoryDialog = MutableStateFlow(false)
    val showAddVolunteerDialog = MutableStateFlow(false)
    val showAddShiftDialog = MutableStateFlow(false)
    val stockItemToAdjust = MutableStateFlow<InventoryItemEntity?>(null)
    val isAdjustingStockReplenish = MutableStateFlow(true)

    // Search and filter options
    val donationSearchQuery = MutableStateFlow("")
    val donationSevaFilter = MutableStateFlow("All")
    val inventoryCategoryFilter = MutableStateFlow("All")
    val showOnlyLowStock = MutableStateFlow(false)

    // ViewModel Actions
    fun addDonation(
        devoteeName: String,
        phone: String,
        email: String,
        amount: Double,
        sevaPurpose: String,
        paymentMode: String,
        paymentRef: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addDonation(
                devoteeName = devoteeName,
                phone = phone,
                email = email,
                amount = amount,
                sevaPurpose = sevaPurpose,
                paymentMode = paymentMode,
                paymentRef = paymentRef,
                notes = notes
            )
            showAddDonationDialog.value = false
        }
    }

    fun deleteDonation(donation: DonationEntity) {
        viewModelScope.launch {
            repository.deleteDonation(donation)
        }
    }

    fun addInventoryItem(
        name: String,
        category: String,
        quantity: Double,
        unit: String,
        minReorderLevel: Double,
        storageLocation: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addInventoryItem(
                name = name,
                category = category,
                quantity = quantity,
                unit = unit,
                minReorderLevel = minReorderLevel,
                storageLocation = storageLocation,
                notes = notes
            )
            showAddInventoryDialog.value = false
        }
    }

    fun consumeStock(itemId: Int, amount: Double) {
        viewModelScope.launch {
            repository.consumeInventory(itemId, amount)
            stockItemToAdjust.value = null
        }
    }

    fun replenishStock(itemId: Int, amount: Double) {
        viewModelScope.launch {
            repository.replenishInventory(itemId, amount)
            stockItemToAdjust.value = null
        }
    }

    fun deleteInventoryItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    fun addVolunteer(
        name: String,
        phone: String,
        email: String,
        preferredSeva: String
    ) {
        viewModelScope.launch {
            repository.addVolunteer(
                name = name,
                phone = phone,
                email = email,
                preferredSeva = preferredSeva
            )
            showAddVolunteerDialog.value = false
        }
    }

    fun addShift(
        shiftTitle: String,
        dateString: String,
        timeSlot: String,
        volunteerId: Int,
        volunteerName: String,
        sevaCategory: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addShift(
                shiftTitle = shiftTitle,
                dateString = dateString,
                timeSlot = timeSlot,
                volunteerId = volunteerId,
                volunteerName = volunteerName,
                sevaCategory = sevaCategory,
                notes = notes
            )
            showAddShiftDialog.value = false
        }
    }

    fun updateShiftStatus(shiftId: Int, status: String) {
        viewModelScope.launch {
            repository.updateShiftStatus(shiftId, status)
        }
    }

    fun deleteShift(shift: VolunteerShiftEntity) {
        viewModelScope.launch {
            repository.deleteShift(shift)
        }
    }
}
