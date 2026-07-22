package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY timestamp DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE id = :id")
    suspend fun getDonationById(id: Int): DonationEntity?

    @Query("SELECT SUM(rawAmount) FROM donations")
    fun getTotalDonationsAmount(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity): Long

    @Delete
    suspend fun deleteDonation(donation: DonationEntity)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY category ASC, name ASC")
    fun getAllInventory(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE quantity <= minReorderLevel")
    fun getLowStockInventory(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItemEntity): Long

    @Update
    suspend fun updateItem(item: InventoryItemEntity)

    @Query("UPDATE inventory_items SET quantity = quantity - :amount, lastUpdated = :timestamp WHERE id = :id AND quantity >= :amount")
    suspend fun consumeItem(id: Int, amount: Double, timestamp: Long = System.currentTimeMillis()): Int

    @Query("UPDATE inventory_items SET quantity = quantity + :amount, lastUpdated = :timestamp WHERE id = :id")
    suspend fun replenishItem(id: Int, amount: Double, timestamp: Long = System.currentTimeMillis()): Int

    @Delete
    suspend fun deleteItem(item: InventoryItemEntity)
}

@Dao
interface VolunteerDao {
    @Query("SELECT * FROM volunteers WHERE active = 1 ORDER BY id DESC")
    fun getAllVolunteers(): Flow<List<VolunteerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: VolunteerEntity): Long

    @Delete
    suspend fun deleteVolunteer(volunteer: VolunteerEntity)
}

@Dao
interface ShiftDao {
    @Query("SELECT * FROM volunteer_shifts ORDER BY dateString ASC, timeSlot ASC")
    fun getAllShifts(): Flow<List<VolunteerShiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: VolunteerShiftEntity): Long

    @Query("UPDATE volunteer_shifts SET status = :status WHERE id = :id")
    suspend fun updateShiftStatus(id: Int, status: String)

    @Delete
    suspend fun deleteShift(shift: VolunteerShiftEntity)
}
