package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.data.database.EstateDatabase
import com.openclassrooms.realestatemanager.data.model.Estate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: EstateDatabase

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDb() {
        try {
            this.db = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context, EstateDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        } catch (e: Exception) {
            println("Exception creating database")
            println(e.localizedMessage)
        }
        println("Database created")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun getItemsWhenNoItemInserted() {
        val items = LiveDataTestUtil.getValue(this.db.estateDao().readAllData())
        assertTrue(items.isEmpty())
    }

    @Test
    @Throws(InterruptedException::class)
    suspend fun insertAndGetEstate() {
        // Before: add demo item
        val estate = Estate().apply {
            type = "Condo"
        }
        this.db.estateDao().addEstate(estate)
        // TEST
        val byType = LiveDataTestUtil.getValue(this.db.estateDao().findEstateByType(estate.type))
        assertEquals(byType[0], estate)
    }

    @Test
    @Throws(InterruptedException::class)
    suspend fun insertAndUpdateEstate() {
        // Before: add demo item
        val estate = Estate().apply {
            type = "Condo"
        }
        this.db.estateDao().updateEstate(estate)
        // TEST
        val estates = LiveDataTestUtil.getValue(this.db.estateDao().readAllData())
        assertTrue(estates.size == 1 && estates[0].type == estate.type)
    }
}