package Structures;

public class Pair {

    private final Course c1;
    private final Course c2;

    public Pair(Lecture c1, Lecture c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public Pair(String[] input) {
        this(input[0], input[1]);
    }

    public Pair(String c1, String c2) {
        if (c1.matches(".*(TUT|LAB).*")) {
            this.c1 = new Lab(c1);
        } else {
            this.c1 = new Lecture(c1);
        }
        if (c2.matches(".*(TUT|LAB).*")) {
            this.c2 = new Lab(c2);
        } else {
            this.c2 = new Lecture(c2);
        }
    }

    public Course getCourse(int i) {
        switch (i) {
            case (0):
                return c1;
            case (1):
                return c2;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-24s\t:%s", c1.toString(), c2.toString());
    }
}
