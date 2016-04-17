package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestData {
    private int n;
    private BoxPanel[] panels = new BoxPanel[n];

    public TestData(TestData data) {
        this.n = data.n;
        this.panels = new BoxPanel[data.panels.length];
        for (int i = 0; i < panels.length; i++) {
            this.panels[i] = new BoxPanel(data.panels[i]);
        }
    }

    public TestData(List<BoxPanel> panels) {
        this.panels = panels.toArray(new BoxPanel[panels.size()]);
        this.n = panels.size();
    }

    public Boolean isBox() {
        List<BoxPanel> list = new ArrayList<>(Arrays.asList(panels));
        //Check for duplicates. If they doesn't exist - return false
        for (int i = 0; i < n; i++) {
            List<BoxPanel> tmp = new ArrayList<>(list);
            tmp.remove(i);
            if (!tmp.contains(list.get(i))) {
                return false;
            }
        }
        //Count numbers. If they more then 3 - return false
        //If it only one - return true(cube)
        Set<Integer> numbers = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            numbers.add(list.get(i).getH());
            numbers.add(list.get(i).getW());
        }
        if (numbers.size() > 3) {
            return false;
        } else if (numbers.size() == 1) {
            return true;
        }
        //Map for easier work with matrix(number - key in matrix)
        Map<Integer, Integer> map = new HashMap<>();
        int i = 0;
        for (Integer num: numbers) {
            map.put(num, i++);
        }
        //Create and zeroing of matrix
        Integer[][] matrix = new Integer[numbers.size()][numbers.size()];
        for (int j = 0; j < matrix.length; j++) {
            for (int k = 0; k < matrix[j].length; k++) {
                matrix[j][k] = 0;
            }
        }
        //Feeling of matrix
        for (int j = 0; j < panels.length; j++) {
            matrix[map.get(panels[j].getH())][map.get(panels[j].getW())] += 1;
            matrix[map.get(panels[j].getW())][map.get(panels[j].getH())] += 1;
        }
        //Checking. If matrix 2x2 then diag must be 0, not diag = 4
        //If 3x3 then diag = 0, not diag = 2
        if (matrix.length == 2) {
            if ((matrix[0][0] == 2 && matrix[1][1] == 0) || (matrix[0][0] == 0 && matrix[1][1] == 2)) {
                if (matrix[0][1] == 4) {
                    return true;
                } else return false;
            } else return true;
        } else if (matrix.length == 3) {
            for (int j = 0; j < matrix.length; j++) {
                for (int k = j; k < matrix.length; k++) {
                    if (k == j) {
                        if (matrix[j][k] != 0) {
                            return false;
                        }
                    } else  {
                        if (matrix[j][k] != 2) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
