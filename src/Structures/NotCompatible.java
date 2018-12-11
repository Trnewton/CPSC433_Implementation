package Structures;

public class NotCompatible {

    private final Course c1;
    private final Course c2;

    public NotCompatible(Course c1, Course c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public NotCompatible(String[] input) {
        this(input[0], input[1]);
    }

    public NotCompatible(String c1, String c2) {
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

    @Override
    public String toString() {
        return String.format("%-24s\t:%s\n", c1.toString(), c2.toString());
    }

    public Course getClass(int n) {
        switch (n) {
            case 0:
                return c1;
            case 1:
                return c2;
            default:
                return null;
        }
    }
}
