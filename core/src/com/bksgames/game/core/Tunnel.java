package com.bksgames.game.core;

import java.util.ArrayList;
import java.util.Collection;

public class Tunnel implements Tile{

    @Override
    public boolean isHollow() {
        return true;
    }

    Collection<Entity> entities = new ArrayList<>();

    Mirror mirror;
}
