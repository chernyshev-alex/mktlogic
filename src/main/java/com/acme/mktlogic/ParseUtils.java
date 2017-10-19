package com.acme.mktlogic;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class ParseUtils {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    /**
     * @param submissionLine in format : "yyyy-MM-dd' 'HH:mm:ss" EmploeeId
     * @return datetime and employeeId or None
     * @throws java.text.ParseException
     */
    public static Optional<Tuple2<LocalDateTime, String>> parseSubmissionLine(String submissionLine) throws ParseException {
        String[] parts = submissionLine.split(" ");
        if (parts.length == 3) {
            LocalDateTime dt = LocalDateTime.parse(parts[0] + "T" + parts[1]);
            String employeeId = parts[2];
            return Optional.of(new Tuple2<>(dt, employeeId));
        }
        throw new ParseException(submissionLine, parts.length);
    }

    /**
     * @param meetingInfoLine in format : "yyyy-MM-dd' 'HH:mm:ss" Duration hours
     * @return datetime and duration or None
     * @throws java.text.ParseException
     */
    public static Optional<Tuple2<LocalDateTime, Integer>>  parseMeetingInfoLine(String meetingInfoLine) throws ParseException {
        String[] parts = meetingInfoLine.split(" ");
        if (parts.length == 3) {
            LocalDateTime dt = LocalDateTime.parse(parts[0] + "T" + parts[1]);
            Integer duration = Integer.parseInt(parts[2]);
            return Optional.of(new Tuple2<>(dt, duration));
        }
        throw new ParseException(meetingInfoLine, parts.length);
    }

    /**
     * @return  office close time
     */
    static LocalTime parseOfficeCloseTime(String officeHours) throws ParseException {
        String[] parts = officeHours.split(" ");
        if (parts.length == 2) {
            return LocalTime.parse(parts[1], TIME_FORMATTER);
        }
        throw new ParseException(officeHours, parts.length);
    }
    
}
