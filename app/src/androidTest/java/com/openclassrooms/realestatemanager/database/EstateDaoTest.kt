package com.openclassrooms.realestatemanager.database

import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.openclassrooms.realestatemanager.data.database.EstateDao
import com.openclassrooms.realestatemanager.data.database.EstateDatabase
import com.openclassrooms.realestatemanager.data.model.Estate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class EstateDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: EstateDatabase
    private lateinit var dao: EstateDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            EstateDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.estateDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addEstateAndReadAllData() = runBlocking {
        dao.addEstate(estate)
        val allEstates = dao.readAllData().getOrAwaitValue()
        assertEquals(allEstates.size, 1)
        assertEquals(allEstates[0], estate)
    }

    @Test
    fun deleteEstate() = runBlocking {
        addEstateAndReadAllData()
        dao.deleteEstate(estate)
        val allEstates = dao.readAllData().getOrAwaitValue()
        assertEquals(allEstates.size, 0)
    }

    @Test
    fun updateEstate() = runBlocking {
        dao.addEstate(estate)
        assertEquals(estate.type, "Condo")
        estate.type = "Duplex"
        dao.updateEstate(estate)
        val allEstates = dao.readAllData().getOrAwaitValue()
        assertEquals(allEstates.size, 1)
        assertEquals(allEstates[0].type, "Duplex")
        assertEquals(allEstates[0].realtor, "Bobby")
    }

    companion object {
        val estate = Estate().apply {
            id = 1
            type = "Condo"
            district = "Hell's Kitchen"
            price = 20000000
            description = "wow"
            surface = 2000
            realtor = "Bobby"
            status = "Vacant"
            nbRooms = 20
            nbBedrooms = 10
            nbBathrooms = 5
            address = "754 9th Ave At 51st Street, New York City, NY 10019-8419"
            entryDate = "5 May 2022"
            saleDate = "Not sold yet"
            vicinity = arrayListOf("park", "restaurants", "stores")
            photoUris =
                arrayListOf("https://media.istockphoto.com/photos/white-penthouse-interior-picture-id160641325?k=20&m=160641325&s=612x612&w=0&h=118D4VVyxMJUaJ5tbxX1VztEtRMmqQkeHbCGZXQTFiQ=")
            photoCaptions = arrayListOf("Beautiful room")
        }
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if LiveData isn't set
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}