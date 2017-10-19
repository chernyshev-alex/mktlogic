package com.acme.mktlogic;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class MktLogicAppIT {

    @Test
    public void testThatBookingIsFullfilledByTaskRequirements() {
        MktLogicApp app = new MktLogicApp();
        Map<LocalDate, List<BookingEntry>> test = app.handleBooking("./data/input.txt");
        //test.entrySet().forEach(System.out::println); 

        LocalDate dt = LocalDate.parse("2015-08-22");
        List<BookingEntry> entries = test.get(dt);
        assertTrue(entries.stream().allMatch(e ->
                e.employeeId.equalsIgnoreCase("EMP004")
                || e.employeeId.equalsIgnoreCase("EMP003")));

        dt = LocalDate.parse("2015-08-21");
        entries = test.get(dt);
        assertTrue(entries.stream().allMatch(e -> e.employeeId.equalsIgnoreCase("EMP002")));

    }

}
