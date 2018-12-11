package OrTree;

import Exceptions.InvalidSchedulingException;
import Parser.Reader;
import Structures.Assignment;
import Structures.Course;
import Structures.Lab;
import Structures.Lecture;
import Structures.NotCompatible;
import Structures.Slot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OTreeModel {

    private final Reader parser;
    private final int evening = 12;
    private Prob root;
    private Set<NotCompatible> notCompatible;
    private HashMap<Course, Slot> usedCourses;
    private final Slot emptySlot;
    private int numExtraCourses;
    private String inputName;
    private ArrayList<Course> addOrder;
    private HashMap<Course, Integer> ranking;

    /**
     *
     * @author thomasnewton
     * @param parser
     * @param inputName
     * @throws Exceptions.InvalidSchedulingException
     */
    public OTreeModel(Reader parser, String inputName) throws InvalidSchedulingException {
        this.parser = parser;
        this.inputName = inputName;
        this.emptySlot = new Slot("", "");
        this.numExtraCourses = 0;
        // Get partial assignments
        HashMap<Course, Slot> partAssign = parser.getPartialAssignments();
        usedCourses = partAssign;

        // Check for CPSC 313 and 413 and add 813/913 and their not-compatibles
        notCompatible = parser.getNotCompatible();
        // Check for CPSC 313
        Set<Lecture> courses = parser.getCourses();
        if (courses.contains(new Lecture("CPSC 313 LEC 01"))) {
            Set<NotCompatible> toAdd = new LinkedHashSet();
            if (!parser.getLabSlots().contains(new Slot("TU", "18:00"))) {
                throw new InvalidSchedulingException("Status: Slot TU, 18:00 does not exist for assignment of CPSC 813");
            }
            for (Slot slot : parser.getLabSlots()) {
                if (slot.equals("TU18:00")) {
                    this.numExtraCourses++;
                    Lab cpscQuiz = new Lab("CPSC 813 TUT 01");
                    partAssign.put(cpscQuiz, slot);
                    Iterator<NotCompatible> itor = notCompatible.iterator();
                    while (itor.hasNext()) {
                        NotCompatible noPair = itor.next();
                        // If the first noPair is CPSC 413 LEC 01 create new with second and quiz
                        if (noPair.getClass(0).getName().equals("CPSC") && noPair.getClass(0).getNumber().equals("313")) {
                            toAdd.add(new NotCompatible(noPair.getClass(1), cpscQuiz));
                        } // If the second noPair is CPSC 413 LEC 01 create new with first and quiz
                        else if (noPair.getClass(1).getName().equals("CPSC") && noPair.getClass(1).getNumber().equals("313")) {
                            toAdd.add(new NotCompatible(noPair.getClass(0), cpscQuiz));
                        }
                    }
                }
            }
            notCompatible.addAll(toAdd);
        }

        // Check for CPSC 413
        if (courses.contains(new Lecture("CPSC 413 LEC 01"))) {
            Set<NotCompatible> toAdd = new LinkedHashSet();
            if (!parser.getLabSlots().contains(new Slot("TU", "18:00"))) {
                throw new InvalidSchedulingException("Status: Slot TU, 18:00 does not exist for assignment of CPSC 913");
            }
            for (Slot slot : parser.getLabSlots()) {
                if (slot.equals("TU18:00")) {
                    this.numExtraCourses++;
                    Lab cpscQuiz = new Lab("CPSC 913 TUT 01");
                    partAssign.put(cpscQuiz, slot);
                    Iterator<NotCompatible> itor = notCompatible.iterator();
                    while (itor.hasNext()) {
                        NotCompatible noPair = itor.next();
                        // If the first noPair is CPSC 413 LEC 01 create new with second and quiz
                        if (noPair.getClass(0).getName().equals("CPSC") && noPair.getClass(0).getNumber().equals("413")) {
                            toAdd.add(new NotCompatible(noPair.getClass(1), cpscQuiz));
                        } // If the second noPair is CPSC 413 LEC 01 create new with first and quiz
                        else if (noPair.getClass(1).getName().equals("CPSC") && noPair.getClass(1).getNumber().equals("413")) {
                            toAdd.add(new NotCompatible(noPair.getClass(0), cpscQuiz));
                        }
                    }
                }
            }
            notCompatible.addAll(toAdd);
        }

        Prob part = checkPartials(partAssign);
        if (part.isUnsolvable()) {
            throw new InvalidSchedulingException("Status: Partial Assignments are not solvable.");
        } else {
            root = part;
        }

        HashMap<Course, Integer> rankingLec = new HashMap();
        HashMap<Course, Integer> rankingLab = new HashMap();

        Iterator<NotCompatible> itor1 = parser.getNotCompatible().iterator();
        while (itor1.hasNext()) {
            NotCompatible noPair = itor1.next();
            if (noPair.getClass(0) instanceof Lecture) {
                rankingLec.put(noPair.getClass(0), rankingLec.getOrDefault(noPair.getClass(0), 0) + 1);
            } else {
                rankingLab.put(noPair.getClass(0), rankingLab.getOrDefault(noPair.getClass(0), 0) + 1);
            }

            if (noPair.getClass(1) instanceof Lecture) {
                rankingLec.put(noPair.getClass(1), rankingLec.getOrDefault(noPair.getClass(1), 0) + 1);
            } else {
                rankingLab.put(noPair.getClass(1), rankingLab.getOrDefault(noPair.getClass(1), 0) + 1);
            }
        }

        Iterator<Course> itor2 = parser.getUnwanted().keySet().iterator();
        while (itor2.hasNext()) {
            Course c = itor2.next();
            if (c instanceof Lecture) {
                rankingLec.put((Lecture) c, rankingLec.getOrDefault(c, 0) + 1);
            } else {
                rankingLab.put((Lab) c, rankingLab.getOrDefault(c, 0) + 1);
            }
        }

        ranking = new HashMap(rankingLec);
        ranking.putAll(rankingLab);

        addOrder = new ArrayList(parser.getCourses());
        addOrder.addAll(parser.getLabs());
        addOrder.sort(new AddOrderComparator(ranking));
    }

    /**
     * Checks the state of a parent with a parent and a new assignment.
     *
     * @param parent
     * @param newAsign
     * @return
     */
    private String getState(Prob parent, Assignment newAsign) {
        Course newCourse = newAsign.getCourse();
        Slot newSlot = newAsign.getSlot();
        HashMap<Course, Slot> schedule = (HashMap<Course, Slot>) parent.getScheduel();

        // Check Not Compatible set against new assignment
        for (NotCompatible notComp : notCompatible) {
            if (notComp.getClass(0).equals(newAsign.getCourse())) {
                if (schedule.getOrDefault(notComp.getClass(1), emptySlot).equals(newAsign.getSlot())) {
                    return "No";
                }
            } else if (notComp.getClass(1).equals(newAsign.getCourse())) {
                if (schedule.getOrDefault(notComp.getClass(0), emptySlot).equals(newAsign.getSlot())) {
                    return "No";
                }
            }
        }

        // Check Unwanted
        HashMap<Course, Set<Slot>> unwanted = parser.getUnwanted();
        if (unwanted.getOrDefault(newCourse, new LinkedHashSet()).contains(newSlot)) {
            return "No";
        }

        // Check courseMax
        if (newCourse instanceof Lecture) {
            HashMap<Slot, Integer> numCourse = parent.getNumCourses();
            if (numCourse.getOrDefault(newSlot, 0) + 1 > newSlot.getMax()) {
                return "No";
            }
        } else {        // Check labMax
            HashMap<Slot, Integer> numLab = parent.getNumLabs();
            if (numLab.getOrDefault(newSlot, 0) + 1 > newSlot.getMax()) {
                return "No";
            }
        }

        // Check labs and courses are not at same time
        if (newCourse instanceof Lecture) {
            Lecture newLecture = (Lecture) newCourse;
            // Get set of labs if it exists else get empty set
            Set<Lab> labs = parser.getCourseLabs().getOrDefault(newLecture, new LinkedHashSet());
            for (Lab lab : labs) {
                if (newSlot.equals(schedule.getOrDefault(lab, emptySlot))) {
                    return "No";
                }
            }
        } else { // Check to see if lab conflicts with lecture
            Lab newLab = (Lab) newCourse;
            String format = String.format("%s %s LEC %s", newLab.getName(), newLab.getNumber(), newLab.getLecture());
            if (newSlot.equals(schedule.getOrDefault(new Lecture(format), emptySlot))) {
                return "No";
            }

        }

        // Check additional constraints
        // Nothing at TU 11:00-12:30
        if (newSlot.getDay().equals("TU") && newSlot.getTime().equals("11:00")) {
            return "No";
        }

        // Check to make sure all Lec 9 are after evening(variable [0,24])
        if (newCourse.getSection().matches("9\\d")) {
            if (Integer.parseInt(newSlot.getTime().split(":")[0]) < evening) {
                return "No";
            }
        }

        // Check 500-level classes dont conflict
        if (newCourse.getNumber().matches("5\\d\\d") && newCourse.getType().equals("LEC")) {
            if (parent.get500Slots().contains(newSlot)) {
                return "No";
            }
        }

        // Check if all labs and courses are scheduled
        int numCourseLab = parser.getCourses().size() + parser.getLabs().size() + numExtraCourses;
        if (schedule.size() + 1 == numCourseLab) {
            return "Yes";
        } else if (schedule.size() + 1 > numCourseLab) {
            return "No";
        }
        return "?";
    }

    /**
     * Checks the state of a schedule with no parent.
     *
     * @param schedule
     * @return
     */
    private Prob checkPartials(HashMap<Course, Slot> schedule) {
        Set<Map.Entry<Course, Slot>> map = schedule.entrySet();
        Iterator<Map.Entry<Course, Slot>> itor = map.iterator();
        HashSet<Slot> num500 = new HashSet();

        // Iterate over all assignments checking one-side of constraints for each
        while (itor.hasNext()) {
            Map.Entry<Course, Slot> entry = itor.next();
            Course course = entry.getKey();
            Slot slot = entry.getValue();

            // Check not compatible set
            for (NotCompatible notComp : notCompatible) {
                if (notComp.getClass(0).equals(course)) {
                    if (schedule.getOrDefault(notComp.getClass(1), emptySlot).equals(slot)) {
                        return new Prob(schedule, "No");
                    }
                }
            }

            // Check Unwanted
            HashMap<Course, Set<Slot>> unwanted = parser.getUnwanted();
            if (unwanted.getOrDefault(course, new LinkedHashSet()).contains(slot)) {
                return new Prob(schedule, "No");
            }

            // Check labs and courses are not at the same time
            if (course instanceof Lecture) { // Check to see if lecture conflicts
                Lecture newLecture = (Lecture) course;
                Set<Lab> labs = parser.getCourseLabs().getOrDefault(newLecture, new LinkedHashSet());
                for (Lab lab : labs) {
                    if (slot.equals(schedule.getOrDefault(lab, emptySlot))) {
                        return new Prob(schedule, "No");
                    }
                }
            }

            // Check no lecture at TU 11:00-12:30
            if (course instanceof Lecture) {
                if (slot.getDay().equals("TU") && slot.getTime().equals("11:00")) {
                    return new Prob(schedule, "No");
                }
            }
            // Check to make sure all Lec 09 are after evening(variable [0,24])
            if (course.getSection().matches("9\\d")) {
                if (Integer.parseInt(slot.getTime().split(":")[0]) < evening) {
                    return new Prob(schedule, "No");
                }
            }
            // Check 500-level classes dont conflict
            if (course.getSection().matches("5\\d\\d")) {
                if (num500.contains(slot)) {
                    return new Prob(schedule, "No");
                } else {
                    num500.add(slot);
                }
            }
        }

        Prob part = new Prob(schedule);

        // Check max course
        Iterator<Map.Entry<Slot, Integer>> itorCMax = part.getNumCourses().entrySet().iterator();
        while (itorCMax.hasNext()) {
            Map.Entry<Slot, Integer> entrCMax = itorCMax.next();
            if (entrCMax.getKey().getMax() < entrCMax.getValue()) {
                part.setState("No");
                return part;
            }
        }

        // Check max lab
        Iterator<Map.Entry<Slot, Integer>> itorLMax = part.getNumLabs().entrySet().iterator();
        while (itorLMax.hasNext()) {
            Map.Entry<Slot, Integer> entrLMax = itorLMax.next();
            if (entrLMax.getKey().getMax() < entrLMax.getValue()) {
                part.setState("No");
                return part;
            }
        }

        // Check if all labs and courses are scheduled
        int numCourseLab = parser.getCourses().size() + parser.getLabs().size() + numExtraCourses;
        if (schedule.size() == numCourseLab) {
            part.setState("Yes");
            return part;
        }
        return part;
    }

    private ArrayList<Prob> altern(Prob leaf, Course g) {
        ArrayList<Prob> alterns = new ArrayList();
        Set<Slot> slots;
        if (g instanceof Lecture) {
            slots = parser.getCourseSlots();
        } else {
            slots = parser.getLabSlots();
        }
        for (Slot slot : slots) {
            Assignment newAsign = new Assignment(g, slot);
            alterns.add(new Prob(leaf, newAsign, getState(leaf, newAsign)));
        }
        return alterns;
    }

    public Prob depthFirst() {
        OrTreeControl1 control = new OrTreeControl1();
        PriorityQueue<Prob> leafs = new PriorityQueue(addOrder.size() * addOrder.size(), control);
        ArrayList<Prob> roots = new ArrayList();
        if (root != null) {
            leafs.add(root);
        } else {
            Random rand = new Random();
            Course course = addOrder.get(0);
            if (course instanceof Lecture) {
                for (Slot slot : parser.getCourseSlots()) {
                    HashMap<Course, Slot> schedule = new HashMap();
                    schedule.put(course, slot);
                    Prob newRoot = checkPartials(schedule);
                    roots.add(newRoot);
                    leafs.add(newRoot);
                }
            } else {
                for (Slot slot : parser.getLabSlots()) {
                    HashMap<Course, Slot> schedule = new HashMap();
                    schedule.put(course, slot);
                    Prob newRoot = checkPartials(schedule);
                    roots.add(newRoot);
                    leafs.add(newRoot);
                }
            }
        }

        while (!leafs.isEmpty()) {
            Prob leaf = leafs.poll();
            if (leaf.isSolved()) {
                return leaf; // Return solution
            } else if (!leaf.isUnsolvable()) {
                Random rand = new Random();
                LinkedList<Course> posCourses = new LinkedList(addOrder);
                posCourses.removeAll(leaf.getScheduel().keySet());
                Course newCourse;
                newCourse = posCourses.get(0);
                altern(leaf, newCourse).forEach((fact) -> {
                    leafs.add(fact);
                });
            }
        }
        return null; // Should never happen unless no solution
    }

    public Prob guided(ArrayList<Assignment> guide) {
        for (int i = 0; i < guide.size(); i++) {
            Assignment assign = guide.get(i);
            if (usedCourses.containsKey(assign.getCourse())) {
                guide.remove(assign);
                i--;
            }
        }
        Course course = guide.remove(0).getCourse();
        guide.sort(new AddOrderComparator2(ranking));
        OrTreeControl2 control = new OrTreeControl2(guide.toArray(new Assignment[0]), usedCourses.size());
        PriorityQueue<Prob> leafs = new PriorityQueue(guide.size() * guide.size(), control);
        ArrayList<Prob> roots = new ArrayList();

        if (course instanceof Lecture) {
            for (Slot slot : parser.getCourseSlots()) {
                HashMap<Course, Slot> map = root.getScheduel();
                HashMap<Course, Slot> schedule = new HashMap(map);
                schedule.put(course, slot);
                Prob newRoot = checkPartials(schedule);
                roots.add(newRoot);
                leafs.add(newRoot);
            }
        } else {
            for (Slot slot : parser.getLabSlots()) {
                HashMap<Course, Slot> map = root.getScheduel();
                HashMap<Course, Slot> schedule = new HashMap(map);
                schedule.put(course, slot);
                Prob newRoot = checkPartials(schedule);
                roots.add(newRoot);
                leafs.add(newRoot);
            }
        }

        while (!leafs.isEmpty()) {
            Prob leaf = leafs.poll();
            if (leaf.isSolved()) {
                return leaf;
            } else if (!leaf.isUnsolvable()) { // Leaf is in guide or not, altern
                for (Prob newLeaf : altern(leaf, guide.get(leaf.getScheduel().size() - usedCourses.size() - 1).getCourse())) {
                    leafs.add(newLeaf);
                }
            }
        }
        return null; // shouldnt happen unless no solution
    }
}
