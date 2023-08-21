package com.ecinema.app.util;

import com.ecinema.app.domain.enums.SecurityQuestions;
import com.ecinema.app.domain.forms.RegistrationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The type Util methods.
 */
public class UtilMethods {

    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * Gets random.
     *
     * @return the random
     */
    public static Random getRandom() {
        return random;
    }

    /**
     * Random int between int.
     *
     * @param min the min
     * @param max the max
     * @return the int
     */
    public static int randomIntBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Local date formatted string.
     *
     * @param localDate the local date
     * @return the string
     */
    public static String localDateFormatted(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.getMonth() + " " + localDate.getDayOfMonth() + ", " + localDate.getYear();
    }

    /**
     * Local time formatted string.
     *
     * @param localTime the local time
     * @return the string
     */
    public static String localTimeFormatted(LocalTime localTime) {
        StringBuilder sb = new StringBuilder();
        String append = "am";
        int hour = localTime.getHour();
        if (hour >= 12) {
            append = "pm";
        }
        if (hour > 12) {
            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }
        String minute = localTime.getMinute() < 10 ? "0" + localTime.getMinute() :
                String.valueOf(localTime.getMinute());
        sb.append(hour).append(":").append(minute).append(append);
        return sb.toString();
    }

    /**
     * Local date time formatted string.
     *
     * @param localDateTime the local date time
     * @return the string
     */
    public static String localDateTimeFormatted(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateFormatted(localDateTime.toLocalDate()) + ", " +
                localTimeFormatted(localDateTime.toLocalTime());
    }

    /**
     * Add page numbers attribute.
     *
     * @param model the model
     * @param page  the page
     */
    public static void addPageNumbersAttribute(Model model, Page<?> page) {
        if (page == null) {
            return;
        }
        int totalPages = page.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                                                 .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
    }

    /**
     * Gets 2 d map of.
     *
     * @param <T>             the type parameter
     * @param iterable        the iterable
     * @param elementsPerList the elements per list
     * @return the 2d map of
     */
    public static <T> Map<Integer, List<T>> get2dMapOf(Iterable<T> iterable, int elementsPerList) {
        Map<Integer, List<T>> map = new HashMap<>();
        map.put(0, new ArrayList<>());
        int i = 0;
        int j = 0;
        for (T t : iterable) {
            if (j >= elementsPerList) {
                j = 0;
                i++;
                map.put(i, new ArrayList<>());
            }
            map.get(i).add(j, t);
            j++;
        }
        return map;
    }

    /**
     * Gets delimiter line.
     *
     * @return the delimiter line
     */
    public static String getLoggingSubjectDelimiterLine() {
        return "---------------------------------------------------------------------------------------------";
    }

    /**
     * Local date time overlap boolean.
     *
     * @param start1 the start 1
     * @param end1   the end 1
     * @param start2 the start 2
     * @param end2   the end 2
     * @return the boolean
     */
    public static boolean localDateTimeOverlap(LocalDateTime start1, LocalDateTime end1,
                                               LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    /**
     * Convert list to page.
     *
     * @param <T>      the type parameter
     * @param list     the list
     * @param pageable the pageable
     * @return the page
     */
    public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * Find all that collection contains if any list.
     *
     * @param <T>                        the type parameter
     * @param checkIfThisContains        the check if this contains
     * @param checkIfOtherContainsOfThis the check if other contains of this
     * @return the list
     */
    public static <T> List<T> findAllThatCollectionContainsIfAny(
            Collection<T> checkIfThisContains, Collection<T> checkIfOtherContainsOfThis) {
        List<T> list = new ArrayList<>();
        for (T t : checkIfOtherContainsOfThis) {
            if (checkIfThisContains.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Find all keys that map contains if any list.
     *
     * @param <T>        the type parameter
     * @param map        the map
     * @param collection the collection
     * @return the list
     */
    public static <T> List<T> findAllKeysThatMapContainsIfAny(Map<T, ?> map, Collection<T> collection) {
        return findAllThatCollectionContainsIfAny(map.keySet(), collection);
    }

    /**
     * Is alphabetical only boolean.
     *
     * @param s the s
     * @return the boolean
     */
    public static boolean isAlphabeticalOnly(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is digits only boolean.
     *
     * @param s the s
     * @return the boolean
     */
    public static boolean isDigitsOnly(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is alpha and digits only boolean.
     *
     * @param s the s
     * @return the boolean
     */
    public static boolean isAlphaAndDigitsOnly(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Num special chars int.
     *
     * @param s the s
     * @return the int
     */
    public static int numSpecialChars(String s) {
        int num = 0;
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isAlphabetic(c)) {
                num++;
            }
        }
        return num;
    }

    /**
     * Remove substrings string.
     *
     * @param s       the s
     * @param targets the targets
     * @return the string
     */
    public static String removeSubstrings(String s, String... targets) {
        String str = "";
        for (String target : targets) {
            str = s.replace(target, "");
        }
        return str;
    }

    /**
     * Remove whitespace string.
     *
     * @param s the s
     * @return the string
     */
    public static String removeWhitespace(String s) {
        return s.replaceAll("\\s+", "");
    }

    /**
     * Random date time local date time.
     *
     * @return the local date time
     */
    public static LocalDateTime randomDateTime() {
        return LocalDateTime.of(randomDate(), randomTime());
    }

    /**
     * Random date local date.
     *
     * @return the local date
     */
    public static LocalDate randomDate() {
        long minDay = LocalDate.of(2022, Month.JANUARY, 1).toEpochDay();
        LocalDate localDate = LocalDate.now();
        long maxDay = LocalDate.of(localDate.getYear(),
                                   localDate.getMonth(), localDate.getDayOfMonth()).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    /**
     * Random time local time.
     *
     * @return the local time
     */
    public static LocalTime randomTime() {
        int randomHour = ThreadLocalRandom.current().nextInt(0, 23 + 1);
        int randomMinute = ThreadLocalRandom.current().nextInt(0, 59 + 1);
        return LocalTime.of(randomHour, randomMinute);
    }

    public static void addRegistrationPageAttributes(final Model model,
                                                     final RegistrationForm registrationForm) {
        model.addAttribute("registrationForm", registrationForm);
        List<String> securityQuestions = SecurityQuestions.getList();
        model.addAttribute("securityQuestions", securityQuestions);
        model.addAttribute("maxDate", LocalDate.now().minusYears(16));
        model.addAttribute("minDate", LocalDate.now().minusYears(120));
    }

}
