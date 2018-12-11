/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OrTree;

import Structures.Course;
import Structures.Lab;
import Structures.Lecture;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author thomasnewton
 */
public class AddOrderComparator implements Comparator<Course> {

    HashMap<Course, Integer> ranking;

    AddOrderComparator(HashMap<Course, Integer> ranking) {
        this.ranking = ranking;
    }

    @Override
    public int compare(Course o1, Course o2) {
        return ranking.getOrDefault(o2, 0) - ranking.getOrDefault(o1, 0);
    }

}
