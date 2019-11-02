package com.jayfella.jme.worldpager.core;

import com.jayfella.jme.worldpager.core.CellSize;

public interface GridSettingsListener {

    void viewDistanceChanged(int oldValue, int newValue);
    void cellSizeChanged(CellSize oldSize, CellSize newSize);
}
