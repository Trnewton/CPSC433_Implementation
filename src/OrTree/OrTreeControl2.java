/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OrTree;

import Structures.Assignment;
import java.util.Comparator;
import java.util.Random;

/**
 * Used by the PriorityQueue to order leaf nodes (Facts) based on or-tree search
 * control.
 *
 * @author thomasnewton
 */
public class OrTreeControl2 implements Comparator<Prob> {

    private final Assignment[] guide;
    private final int numPartAssign;

    public OrTreeControl2(Assignment[] guide, int numPartAssign) {
        this.guide = guide;
        this.numPartAssign = numPartAssign;
    }

    @Override
    public int compare(Prob o1, Prob o2) {
        int val1, val2;
        int max = 4 * guide.length;
        // 1st priority solved problems
        val1 = (o1.isSolved()) ? max : 0;
        val2 = (o2.isSolved()) ? max : 0;
        // 2nd priority unsolvable problems
        val1 += (o1.isUnsolvable()) ? max / 2 : 0;
        val2 += (o2.isUnsolvable()) ? max / 2 : 0;
        if (!o1.isUnsolvable() && !o2.isUnsolvable() && !o1.isSolved() && !o2.isSolved()) {
            // 3rd priority guide problems
            Assignment g = guide[o1.getScheduel().size() - numPartAssign - 1];
            val1 += (g.getSlot().equals(o1.getScheduel().get(g.getCourse()))) ? max / 4 : 0;
            g = guide[o2.getScheduel().size() - numPartAssign - 1];
            val2 += (g.getSlot().equals(o2.getScheduel().get(g.getCourse()))) ? max / 4 : 0;
            // 4th priority deep problems
            val1 += o1.getScheduel().size() / 2;
            val2 += o2.getScheduel().size() / 2;
        }
        // Add random offset
        Random rand = new Random();
        val1 += rand.nextInt(1);

        return val2 - val1;
    }

}
