package com.company;

public class BoxPanel {
    private int h;
    private int w;

    public BoxPanel(BoxPanel boxPanel) {
        this.h = boxPanel.h;
        this.w = boxPanel.w;
    }

    public BoxPanel(int h, int w) {
        this.h = h;
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoxPanel boxPanel = (BoxPanel) o;

        return (h == boxPanel.h && w == boxPanel.w) || (h == boxPanel.w && w == boxPanel.h);

    }

    @Override
    public int hashCode() {
        int result = h;
        result = 31 * result + w;
        return result;
    }
}
