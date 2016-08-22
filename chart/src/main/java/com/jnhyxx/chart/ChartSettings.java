package com.jnhyxx.chart;

public class ChartSettings {

    private float[] mBaseLines;
    private float[] mIndexesBaseLines;
    private boolean mIndexesEnable;
    private int mNumberScale;
    private int mXAxis;
    private float mPreClosePrice;

    public ChartSettings() {
        mBaseLines = new float[0];
        mIndexesBaseLines = new float[0];
        mIndexesEnable = false;
        mNumberScale = 2;
        mXAxis = 0;
        mPreClosePrice = 0;
    }

    public float getPreClosePrice() {
        return mPreClosePrice;
    }

    public ChartSettings setPreClosePrice(float preClosePrice) {
        mPreClosePrice = preClosePrice;
        return this;
    }

    public float[] getBaseLines() {
        return mBaseLines;
    }

    public void setBaseLines(int baseLines) {
        if (baseLines < 2) {
            baseLines = 2;
        }
        if (baseLines % 2 == 0) {
            baseLines++;
        }
        mBaseLines = new float[baseLines];
    }

    public float[] getIndexesBaseLines() {
        return mIndexesBaseLines;
    }

    public void setIndexesBaseLines(int indexesBaseLines) {
        if (indexesBaseLines < 2) {
            indexesBaseLines = 2;
        }
        mIndexesBaseLines = new float[indexesBaseLines];
    }

    public boolean isIndexesEnable() {
        return mIndexesEnable;
    }

    public ChartSettings setIndexesEnable(boolean indexesEnable) {
        mIndexesEnable = indexesEnable;
        return this;
    }

    public int getNumberScale() {
        return mNumberScale;
    }

    public void setNumberScale(int numberScale) {
        mNumberScale = numberScale;
    }

    public void setXAxis(int XAxis) {
        mXAxis = XAxis;
    }

    public int getXAxis() {
        return mXAxis;
    }
}
