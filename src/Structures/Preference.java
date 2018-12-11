package Structures;

import java.util.Arrays;

public class Preference {

    private final Course course;
    private final Slot slot;
    private final int value;

    public Preference(Course course, Slot slot, String value) {
        this.course = course;
        this.slot = slot;
        this.value = Integer.parseInt(value.trim());
    }

    public Preference(String[] input) {
        this(Arrays.copyOfRange(input, 0, 2), input[2], input[3]);
    }

    public Preference(String[] slot, String course, String pref) {
        this.slot = new Slot(slot);
        this.value = Integer.parseInt(pref.trim());
        if (course.matches(".*(TUT|LAB).*")) {
            this.course = new Lab(course);
        } else {
            this.course = new Lecture(course);
        }
    }

    @Override
    public String toString() {
        return String.format("%-24s\t:%-24s\t:%s",
                slot.toString(), course.toString(), value);
    }

    public Course getCourse() {
        return course;
    }

    public Slot getSlot() {
        return slot;
    }

    public int getValue() {
        return value;
    }
}
