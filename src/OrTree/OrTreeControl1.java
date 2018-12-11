/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OrTree;

import static java.lang.Integer.max;
import java.util.Comparator;
import java.util.Random;

/**
 * Used by the PriorityQueue to order leaf nodes (Facts) based on or-tree search
 * control.
 *
 * @author thomasnewton
 */
public class OrTreeControl1 implements Comparator<Prob> {

    @Override
    public int compare(Prob o1, Prob o2) {
        int val1, val2;
        int max = 4 * max(o1.getScheduel().size(), o2.getScheduel().size());
        // 1st priority solved problems
        val1 = (o1.isSolved()) ? max : 0;
        val2 = (o2.isSolved()) ? max : 0;
        // 2nd priority unsolvable problems
        val1 += (o1.isUnsolvable()) ? max / 2 : 0;
        val2 += (o2.isUnsolvable()) ? max / 2 : 0;
        // 3rd priority deep problems
        val1 += o1.getScheduel().size();
        val2 += o2.getScheduel().size();
        // Add random offset
        if (val1 == val2) {
            Random rand = new Random();
            val1 += rand.nextInt(2);
            val2 += rand.nextInt(2);
        }

        return val2 - val1;
    }

}
