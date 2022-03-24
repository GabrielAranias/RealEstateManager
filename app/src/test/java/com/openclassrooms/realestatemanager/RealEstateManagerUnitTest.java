package com.openclassrooms.realestatemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RealEstateManagerUnitTest {

    @Test
    public void checkDollarToEuroConversion() {
        assertEquals(81, Utils.convertDollarToEuro(100));
    }

    @Test
    public void checkEuroToDollarConversion() {
        assertEquals(100, Utils.convertEuroToDollar(81));
    }

    @Test
    public void checkDateFormat() {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        assertEquals(date, Utils.getTodayDate());
    }
}