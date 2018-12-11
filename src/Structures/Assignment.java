/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structures;

/**
 *
 * @author thomasnewton
 */
public class Assignment {

    private final Course course;
    private final Slot slot;

    public Assignment() {
        this.course = null;
        this.slot = null;
    }

    public Assignment(Course course, Slot slot) {
        this.course = course;
        this.slot = slot;
    }

    public Course getCourse() {
        return course;
    }

    public Slot getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return String.format("%-24s\t:%s", course.toString(), slot.toString());
    }
}
