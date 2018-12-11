package Structures;

import java.util.Objects;

public class Slot {

    private final String day;
    private final String time;
    private final int max;
    private final int min;
    private final String id;

    public Slot(String[] input) {
        this.day = input[0].trim();
        this.time = input[1].trim();
        if (input.length == 4) {
            this.max = Integer.parseInt(input[2].trim());
            this.min = Integer.parseInt(input[3].trim());
        } else {
            this.max = Integer.MAX_VALUE;
            this.min = 0;
        }
        this.id = this.day + this.time;
    }

    public Slot(String day, String time) {
        this.day = day.trim();
        this.time = time.trim();
        this.max = Integer.MAX_VALUE;
        this.min = 0;
        this.id = this.day + this.time;
    }

    public Slot(String day, String time, String max, String min) {
        this.day = day.trim();
        this.time = time.trim();
        this.max = Integer.parseInt(max.trim());
        this.min = Integer.parseInt(min.trim());
        this.id = this.day + this.time;
    }

    public Slot(String day, String time, int max, int min) {
        this.day = day.trim();
        this.time = time.trim();
        this.max = max;
        this.min = min;
        this.id = this.day + this.time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Slot) {
            return this.id.equals(((Slot) obj).id);
        } else if (obj instanceof String) {
            return this.id.equals(((String) obj).replace("\\s+", ""));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", day, time, max, min);
    }

    public String getDay() {
        return this.day;
    }

    public String getTime() {
        return this.time;
    }

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }
}
