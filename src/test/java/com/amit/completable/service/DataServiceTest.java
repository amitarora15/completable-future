package com.amit.completable.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataServiceTest {

    @Autowired
    private DataService dataServiceUnderTest;

    @Test
    public void testData(){
        dataServiceUnderTest.executeOrderFromInventory();
    }
}
