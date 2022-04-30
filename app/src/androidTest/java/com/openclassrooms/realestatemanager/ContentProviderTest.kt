package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.data.database.EstateDatabase
import com.openclassrooms.realestatemanager.provider.EstateContentProvider
import com.openclassrooms.realestatemanager.utils.Constants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentProviderTest {

    private var contentResolver: ContentResolver? = null

    companion object {
        private const val TEST_ID: Long = 1
    }

    @Before
    fun setUp() {
        Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            EstateDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun getItemsWhenNoItemInserted() {
        val cursor = contentResolver!!.query(
            ContentUris.withAppendedId(
                EstateContentProvider.URI_ITEM,
                TEST_ID
            ), null, null, null, null
        )
        assertNotNull(cursor)
        assertEquals(cursor!!.count, 0)
        cursor.close()
    }

    @Test
    fun insertAndGetItems() {
        // Before: add demo item
        val testUri = contentResolver!!.insert(EstateContentProvider.URI_ITEM, generateItem())
        // Test
        val cursor = contentResolver!!.query(
            ContentUris.withAppendedId(
                EstateContentProvider.URI_ITEM,
                TEST_ID
            ), null, null, null, null
        )
        assertNotNull(cursor)
        assertEquals(cursor!!.count, 1)
        assertTrue(cursor.moveToFirst())
        assertEquals(cursor.getString(cursor.getColumnIndexOrThrow("description")), "wow")
    }

    private fun generateItem(): ContentValues {
        val values = ContentValues()
        values.put(Constants.DESCRIPTION, "wow")
        return values
    }
}