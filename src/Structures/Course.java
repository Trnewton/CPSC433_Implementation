package Structures;

import java.util.Objects;

public class Course {

    private final String name;
    private final String number;
    private final String type;
    private final String section;
    private final String id;

    public Course(String name, String number, String type, String section) {
        this.name = name.trim();
        this.number = number.trim();
        this.type = type.trim();
        this.section = section.trim();
        this.id = this.name + this.number + this.type + this.section;
    }

    public Course(String input) {
        this(input.trim().split("\\s+"));
    }

    public Course(String[] input) {
        this(input[0].trim(), input[1].trim(), input[2].trim(), input[3].trim());
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", name, number, type, section);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Course) {
            return this.id.equals(((Course) obj).id);
        } else if (obj instanceof String) {
            return this.id.equals(((String) obj).replace(" ", ""));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getSection() {
        return section;
    }
}
