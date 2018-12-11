/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SetBased;

import Structures.Course;
import java.util.Comparator;

/**
 *
 * @author thomasnewton
 */
public class CourseSorterAlpaNum implements Comparator<Course> {

    @Override
    public int compare(Course o1, Course o2) {
        if (o1.getName().compareTo(o2.getName()) != 0) {
            return o1.getName().compareTo(o2.getName());
        } else {
            return o1.getNumber().compareTo(o2.getNumber());
        }
    }

}
