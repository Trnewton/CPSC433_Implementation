/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OrTree;

import Structures.Assignment;
import Structures.Course;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author thomasnewton
 */
public class AddOrderComparator2 implements Comparator<Assignment> {

    HashMap<Course, Integer> ranking;

    AddOrderComparator2(HashMap<Course, Integer> ranking) {
        this.ranking = ranking;
    }

    @Override
    public int compare(Assignment o1, Assignment o2) {
        return ranking.getOrDefault(o2.getCourse(), 0) - ranking.getOrDefault(o1.getCourse(), 0);
    }

}
