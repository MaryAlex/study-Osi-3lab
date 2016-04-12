package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestData {
    int n;
    BoxPanel[] panels = new BoxPanel[n];

    public TestData(List<BoxPanel> panels) {
        this.panels = panels.toArray(new BoxPanel[panels.size()]);
    }

    public Boolean isBox() {
        List<BoxPanel> list = new ArrayList<>(Arrays.asList(panels));
        for (int i = 0; i < n; i++) {
            List<BoxPanel> tmp = new ArrayList<>(list);
            tmp.remove(i);
            if (!tmp.contains(list.get(i))) {
                return false;
            }
        }
        return true;
    }
}
