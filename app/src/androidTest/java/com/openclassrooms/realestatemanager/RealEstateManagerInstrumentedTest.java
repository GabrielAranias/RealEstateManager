package com.openclassrooms.realestatemanager;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.utils.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RealEstateManagerInstrumentedTest {

    Context appContext = getInstrumentation().getTargetContext();

    @Test
    public void useAppContext() {
        assertEquals("com.openclassrooms.realestatemanager", appContext.getPackageName());
    }

    @Test
    public void checkConnectivity() {
        assertEquals(true, Utils.isInternetAvailable(appContext));
    }
}
