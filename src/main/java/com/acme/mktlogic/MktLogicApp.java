package com.acme.mktlogic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MktLogicApp {

    static final Logger LOGGER = Logger.getLogger(MktLogicApp.class.getName());

    private Stream<String> streamFromFile(String filePath) throws IOException {
        return Files.lines(new File(filePath).toPath());
    }

    private OfficeBooking consume(Stream<String> stream, OfficeBooking consumer) {
        stream.forEach(consumer);
        return consumer;
    }

    /**
     * @return true if b2 start after b1
     */
    private static boolean isMeetingAfterInclusive(BookingEntry b1, BookingEntry b2) {
        return b1.meetingStart.isEqual(b2.meetingEnd) || b1.meetingStart.isAfter(b2.meetingEnd);
    }

    /**
     * @return true if meeting can be finished before office closing time
     */
    private static boolean beforeClosingOffice(BookingEntry b1, LocalDateTime b2) {
        return b1.meetingEnd.isBefore(b2);
    }

    /**
     * Conflict resolution booking. 
     * Removed entries out of office hours.
     * Removed employees conflicted entries. Winner resolves by submission time.
     * 
     * @param source - ordered by meeting end time booking entries
     * @param booking source
     * @return booking map with removed conflicted entries
     */
    private Map<LocalDate, List<BookingEntry>> applyConstraints(Map<LocalDate, List<BookingEntry>> source, OfficeBooking booking) {
        for (Map.Entry<LocalDate, List<BookingEntry>> entry : source.entrySet()) {
            LocalDate ld = entry.getKey();
            LocalDateTime endOfficeHours = booking.getCloseTimeForDate(ld);
            List<BookingEntry> ls = entry.getValue();
            if (!ls.isEmpty()) {
                List<BookingEntry> result = new ArrayList<>();
                BookingEntry eb0 = ls.get(0);
                result.add(eb0);
                for (int i = 1; i < ls.size(); i++) {
                    BookingEntry next = ls.get(i);
                    if (isMeetingAfterInclusive(next, eb0) && beforeClosingOffice(next, endOfficeHours)) {
                        eb0 = next;
                        result.add(eb0);
                    }
                }
                entry.setValue(result);
            }
        }
        return source;
    }

    /**
     * Sort by meeting end and submission time
     * @return sorter 
     */
    private static Comparator<BookingEntry> getBookingSortComparator() {
        return (BookingEntry o1, BookingEntry o2) -> {
            int res = o1.meetingEnd.compareTo(o2.meetingEnd);
            if (res == 0) {
                return o1.submittedAt.compareTo(o2.submittedAt);
            }
            return res;
        };
    }

    /**
     * Apply  activity selection algorithm with office constraints, 
     * resolve conflicts between booking entries
     * @param filePath - path to booking file
     * @return ready booking plan
     */
    public Map<LocalDate, List<BookingEntry>> handleBooking(String filePath) {
        try {
            OfficeBooking booking = consume(streamFromFile(filePath), new OfficeBooking());
            // selection activity algorithm, sort by meeting end time and resolve constraints
            // not functional style, ashamed
            Map<LocalDate, List<BookingEntry>> res = booking.getEmployeeBookings()
                    .stream()
                    .sorted(getBookingSortComparator())
                    .collect(Collectors.groupingBy(BookingEntry::getMeetingDate));

            Map<LocalDate, List<BookingEntry>> resultBooking = applyConstraints(res, booking);
            resultBooking.entrySet().forEach(System.out::println);
            return resultBooking;
            
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return Collections.EMPTY_MAP;
    }

    private void run(String[] args) {
        if (args == null) {
            LOGGER.info("Usage\n  : options : <full file name>");
            System.exit(-1);
        }
        handleBooking(args[0]);
    }

    public static void main(String[] args) {
        new MktLogicApp().run(args);
    }

}
