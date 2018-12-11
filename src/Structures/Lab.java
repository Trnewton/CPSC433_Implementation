package Structures;

import java.util.Objects;

public class Lab extends Course {

    private final String lecture;

    public Lab(String name, String number, String type, String section) {
        super(name, number, type, section);
        lecture = "-1";
    }

    public Lab(String name, String number, String type, String section, String lecture) {
        super(name, number, type, section);
        this.lecture = lecture.trim();
    }

    public Lab(String[] input) {
        super(input[0], input[1], input[input.length - 2], input[input.length - 1]);
        if (input.length == 6) {
            this.lecture = input[3].trim();
        } else {
            this.lecture = "-1";
        }
    }

    public Lab(String input) {
        this(input.trim().split("\\s+"));
    }

    public String getLecture() {
        return lecture;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Lab) {
            return this.lecture.equals(((Lab) obj).getLecture()) && super.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.lecture);
        hash += super.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        String s = super.toString();
        if (!lecture.equals("-1")) {
            return String.format("%s LEC %s", s, lecture);
        } else {
            return s;
        }
    }
}
