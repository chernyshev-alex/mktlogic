package com.acme.mktlogic;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OfficeBooking implements Consumer<String>, Serializable {

    static final Logger LOGGER = Logger.getLogger(OfficeBooking.class.getName());

    // Internal parse state
    private static enum EParsePhase {
        ON_HEADER, ON_EMPL_SUBMISSION, ON_EMPL_BOOKING
    }

    // temporary vars
    private String empSubmission;
    private String meetingInfo;
    private EParsePhase parsePhase = EParsePhase.ON_HEADER;

    // office working close time
     private LocalTime officeCloseTime;
    // booking set
    private final List<BookingEntry> employeeBookings = new ArrayList<>();
    
    public LocalDateTime getCloseTimeForDate(LocalDate date) {
        return date.atTime(officeCloseTime);
    }
    
    @Override
    public void accept(String part) {
        switch (parsePhase) {
            case ON_HEADER:
                assignOfficeHours(part);
                parsePhase = EParsePhase.ON_EMPL_SUBMISSION;
                break;
            case ON_EMPL_SUBMISSION:
                empSubmission = part;
                parsePhase = EParsePhase.ON_EMPL_BOOKING;
                break;
            case ON_EMPL_BOOKING:
                meetingInfo = part;
                parsePhase = EParsePhase.ON_EMPL_SUBMISSION;
                onParsed();
        }
    }

    private void assignOfficeHours(String part) {
        try {
            officeCloseTime = ParseUtils.parseOfficeCloseTime(part);
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    protected void onParsed() {
        Optional<BookingEntry> eb = BookingEntry.create(empSubmission, meetingInfo);
        if (eb.isPresent()) {
            employeeBookings.add(eb.get());
        }
        // TODO : log if wasn't be created 
    }

    public List<BookingEntry> getEmployeeBookings() {
        return Collections.unmodifiableList(employeeBookings);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("closeTime=" + officeCloseTime)
                .add(employeeBookings.toString())
                .toString();
    }
}
