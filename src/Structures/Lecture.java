package Structures;

import java.util.Arrays;

public class Lecture extends Course {

    private String[] labs;

    public Lecture(String name, String number, String type, String section) {
        super(name, number, type, section);
        this.labs = null;
    }

    public Lecture(String name, String number, String type, String section, String[] labs) {
        super(name, number, type, section);
        this.labs = labs;
    }

    public Lecture(String[] input) {
        super(input[0], input[1], input[2], input[3]);
        if (input.length >= 6) {
            this.labs = Arrays.copyOfRange(input, 5, input.length + 1);
            for (int i = 0; i < this.labs.length; i++) {
                this.labs[i] = this.labs[i].trim();
            }
        } else {
            this.labs = null;
        }
    }

    public Lecture(String input) {
        this(input.trim().split("\\s+"));
    }

    public String[] getLabs() {
        return labs;
    }

    public void setLabs(String[] labs) {
        this.labs = labs;
        for (int i = 0; i < this.labs.length; i++) {
            this.labs[i] = this.labs[i].trim();
        }
    }
}
