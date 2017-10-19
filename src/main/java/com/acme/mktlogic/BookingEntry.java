package com.acme.mktlogic;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User's  booking info 
 */
public class BookingEntry implements Serializable {

    static final Logger LOGGER = Logger.getLogger(BookingEntry.class.getName());

    public final LocalDateTime submittedAt;
    public final String employeeId;

    public final LocalDateTime meetingStart;
    public final LocalDateTime meetingEnd;

    public LocalDateTime getMeetingStart() {
        return meetingStart;
    }

    public LocalDate getMeetingDate() {
        return meetingStart.toLocalDate();
    }

    public BookingEntry(Tuple2<LocalDateTime, String> submissionInfo, Tuple2<LocalDateTime, Integer> meetingInfo) {
        this.submittedAt = submissionInfo._1;
        this.employeeId = submissionInfo._2;
        this.meetingStart = meetingInfo._1;
        this.meetingEnd = meetingInfo._1.plusHours(meetingInfo._2);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("employeeId=" + this.employeeId)
                .add("submittedAt=" + this.submittedAt)
                .add("meetingStart=" + this.getMeetingStart())
                .add("meetingEnd=" + this.meetingEnd)
                .toString();
    }
    
    public static Optional<BookingEntry> create(String submisstionLine, String meetingInfoLine) {
        try {
            Optional<Tuple2<LocalDateTime, String>> sbms = ParseUtils.parseSubmissionLine(submisstionLine);
            Optional<Tuple2<LocalDateTime, Integer>> minfo = ParseUtils.parseMeetingInfoLine(meetingInfoLine);
            if (sbms.isPresent() && minfo.isPresent()) {
                return Optional.of(new BookingEntry(sbms.get(), minfo.get()));
            }

        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }
    
}
