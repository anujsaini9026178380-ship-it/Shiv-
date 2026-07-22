package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.security.SecurityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DonationEntity::class,
        InventoryItemEntity::class,
        VolunteerEntity::class,
        VolunteerShiftEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MandirDatabase : RoomDatabase() {

    abstract fun donationDao(): DonationDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun volunteerDao(): VolunteerDao
    abstract fun shiftDao(): ShiftDao

    companion object {
        @Volatile
        private var INSTANCE: MandirDatabase? = null

        fun getDatabase(context: Context): MandirDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MandirDatabase::class.java,
                    "shiva_mandir_database"
                )
                .addCallback(DatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: MandirDatabase) {
            val invDao = db.inventoryDao()
            val donDao = db.donationDao()
            val volDao = db.volunteerDao()
            val shiftDao = db.shiftDao()

            // 1. Initial Inventory Items (Puja Samagri & Provisions)
            val initialInventory = listOf(
                InventoryItemEntity(
                    name = "Pure Cow Ghee",
                    category = "Puja Samagri",
                    quantity = 25.0,
                    unit = "Liters",
                    minReorderLevel = 10.0,
                    storageLocation = "Sanctum Store Room",
                    notes = "Used for Akhand Jyoti and Abhishekam"
                ),
                InventoryItemEntity(
                    name = "Bhimseni Camphor (Kapur)",
                    category = "Puja Samagri",
                    quantity = 3.5,
                    unit = "kg",
                    minReorderLevel = 5.0, // Low stock alert!
                    storageLocation = "Aarti Counter Drawer 1",
                    notes = "Organic fragrant camphor for daily Aarti"
                ),
                InventoryItemEntity(
                    name = "Fresh Bel Patra (Bilva Leaves)",
                    category = "Sacred Offerings",
                    quantity = 120.0,
                    unit = "Packets",
                    minReorderLevel = 50.0,
                    storageLocation = "Cold Storage / Shrine Counter",
                    notes = "Triple-leaf sacred Bilva for Shiva Puja"
                ),
                InventoryItemEntity(
                    name = "Pure Sandalwood Paste (Chandan)",
                    category = "Puja Samagri",
                    quantity = 2.0,
                    unit = "kg",
                    minReorderLevel = 1.0,
                    storageLocation = "Sanctum Store Room",
                    notes = "Natural Mysore Sandalwood for Tilak"
                ),
                InventoryItemEntity(
                    name = "Brass Diyas & Oil Lamps",
                    category = "Temple Vessels",
                    quantity = 48.0,
                    unit = "Pcs",
                    minReorderLevel = 20.0,
                    storageLocation = "Main Altar Store",
                    notes = "Polished brass lamps for evening Deeparadhana"
                ),
                InventoryItemEntity(
                    name = "Premium Incense Sticks (Agarbatti)",
                    category = "Puja Samagri",
                    quantity = 4.0,
                    unit = "Boxes",
                    minReorderLevel = 8.0, // Low stock alert!
                    storageLocation = "Sanctum Cupboard 2",
                    notes = "Guggal and Kesar scented sticks"
                ),
                InventoryItemEntity(
                    name = "Basmati Rice (Annadanam)",
                    category = "Annadanam Provisions",
                    quantity = 150.0,
                    unit = "kg",
                    minReorderLevel = 50.0,
                    storageLocation = "Annadanam Kitchen Pantry",
                    notes = "For Sunday Prasadam and daily bhog"
                ),
                InventoryItemEntity(
                    name = "Pure Natural Honey (Madhu)",
                    category = "Sacred Offerings",
                    quantity = 8.0,
                    unit = "Liters",
                    minReorderLevel = 3.0,
                    storageLocation = "Abhishekam Counter",
                    notes = "Panchamrit preparation"
                )
            )
            initialInventory.forEach { invDao.insertItem(it) }

            // 2. Initial Member Donations (AES-256 Encrypted personal details)
            val initialDonations = listOf(
                DonationEntity(
                    receiptNo = "SM-2026-0101",
                    devoteeNameEncrypted = SecurityManager.encrypt("Ramesh Sharma"),
                    phoneEncrypted = SecurityManager.encrypt("+91 98765 43210"),
                    emailEncrypted = SecurityManager.encrypt("ramesh.sharma@email.com"),
                    rawAmount = 5100.0,
                    amountEncrypted = SecurityManager.encrypt("5100.00"),
                    sevaPurpose = "Rudrabhishek Seva",
                    paymentMode = "UPI",
                    paymentRef = "UPI/6204918239/OKICICI",
                    notesEncrypted = SecurityManager.encrypt("For Monday Special Puja and Shiva Parvati Blessings")
                ),
                DonationEntity(
                    receiptNo = "SM-2026-0102",
                    devoteeNameEncrypted = SecurityManager.encrypt("Sunita & Anil Verma"),
                    phoneEncrypted = SecurityManager.encrypt("+91 91234 56789"),
                    emailEncrypted = SecurityManager.encrypt("anil.verma@email.com"),
                    rawAmount = 11000.0,
                    amountEncrypted = SecurityManager.encrypt("11000.00"),
                    sevaPurpose = "Annadanam Seva",
                    paymentMode = "Bank Transfer",
                    paymentRef = "NEFT-HDFC-9918234",
                    notesEncrypted = SecurityManager.encrypt("Sponsoring Sunday Bhandara prasadam for 200 devotees")
                ),
                DonationEntity(
                    receiptNo = "SM-2026-0103",
                    devoteeNameEncrypted = SecurityManager.encrypt("Mahesh Kulkarni"),
                    phoneEncrypted = SecurityManager.encrypt("+91 98111 22233"),
                    emailEncrypted = SecurityManager.encrypt("mahesh.k@email.com"),
                    rawAmount = 2100.0,
                    amountEncrypted = SecurityManager.encrypt("2100.00"),
                    sevaPurpose = "Mahashivratri Fund",
                    paymentMode = "Cash",
                    paymentRef = "CASH-REC-003",
                    notesEncrypted = SecurityManager.encrypt("Contribution for Mahashivratri illumination and flowers")
                ),
                DonationEntity(
                    receiptNo = "SM-2026-0104",
                    devoteeNameEncrypted = SecurityManager.encrypt("Sujata Patel"),
                    phoneEncrypted = SecurityManager.encrypt("+91 99887 76655"),
                    emailEncrypted = SecurityManager.encrypt("sujata.patel@email.com"),
                    rawAmount = 1008.0,
                    amountEncrypted = SecurityManager.encrypt("1008.00"),
                    sevaPurpose = "Akhand Jyoti Seva",
                    paymentMode = "UPI",
                    paymentRef = "PAYTM-88219318",
                    notesEncrypted = SecurityManager.encrypt("Monthly Cow Ghee sponsorship for continuous lamp")
                )
            )
            initialDonations.forEach { donDao.insertDonation(it) }

            // 3. Initial Volunteer Roster (AES-256 Encrypted contact info)
            val initialVolunteers = listOf(
                VolunteerEntity(
                    nameEncrypted = SecurityManager.encrypt("Priya Joshi"),
                    phoneEncrypted = SecurityManager.encrypt("+91 97654 32109"),
                    emailEncrypted = SecurityManager.encrypt("priya.joshi@email.com"),
                    preferredSeva = "Aarti & Altar Setup",
                    active = true
                ),
                VolunteerEntity(
                    nameEncrypted = SecurityManager.encrypt("Vijay Kumar"),
                    phoneEncrypted = SecurityManager.encrypt("+91 98220 11223"),
                    emailEncrypted = SecurityManager.encrypt("vijay.k@email.com"),
                    preferredSeva = "Crowd & Queue Management",
                    active = true
                ),
                VolunteerEntity(
                    nameEncrypted = SecurityManager.encrypt("Sangeeta Rai"),
                    phoneEncrypted = SecurityManager.encrypt("+91 99123 44556"),
                    emailEncrypted = SecurityManager.encrypt("sangeeta.rai@email.com"),
                    preferredSeva = "Prasadam Distribution",
                    active = true
                ),
                VolunteerEntity(
                    nameEncrypted = SecurityManager.encrypt("Amitabh Das"),
                    phoneEncrypted = SecurityManager.encrypt("+91 94331 10099"),
                    emailEncrypted = SecurityManager.encrypt("a.das@email.com"),
                    preferredSeva = "Sanctum Cleaning & Flowers",
                    active = true
                )
            )
            initialVolunteers.forEach { volDao.insertVolunteer(it) }

            // 4. Initial Volunteer Shifts
            val initialShifts = listOf(
                VolunteerShiftEntity(
                    shiftTitle = "Morning Mangala Aarti Duty",
                    dateString = "2026-07-22",
                    timeSlot = "05:00 AM - 08:00 AM",
                    volunteerId = 1,
                    volunteerName = "Priya Joshi",
                    sevaCategory = "Aarti & Altar Setup",
                    status = "Scheduled",
                    notes = "Prepare lamps, flowers, and holy water for morning Aarti"
                ),
                VolunteerShiftEntity(
                    shiftTitle = "Prasadam Service & Counter",
                    dateString = "2026-07-22",
                    timeSlot = "12:00 PM - 02:30 PM",
                    volunteerId = 3,
                    volunteerName = "Sangeeta Rai",
                    sevaCategory = "Prasadam Distribution",
                    status = "Scheduled",
                    notes = "Distribute Kheer & Panchamrit prasadam after afternoon Bhog"
                ),
                VolunteerShiftEntity(
                    shiftTitle = "Evening Maha Shiv Aarti & Queue Duty",
                    dateString = "2026-07-22",
                    timeSlot = "06:00 PM - 09:00 PM",
                    volunteerId = 2,
                    volunteerName = "Vijay Kumar",
                    sevaCategory = "Crowd & Queue Management",
                    status = "Scheduled",
                    notes = "Manage devotee line for Evening Aarti and Darshan"
                ),
                VolunteerShiftEntity(
                    shiftTitle = "Sanctum Garland & Flower Decor",
                    dateString = "2026-07-23",
                    timeSlot = "04:30 PM - 06:30 PM",
                    volunteerId = 4,
                    volunteerName = "Amitabh Das",
                    sevaCategory = "Sanctum Cleaning & Flowers",
                    status = "Scheduled",
                    notes = "Arrange fresh Bilva leaves and Marigold garlands around Lingam"
                )
            )
            initialShifts.forEach { shiftDao.insertShift(it) }
        }
    }
}
