package com.bksgames.game.core.boards;

import com.bksgames.game.core.entities.Minion;
import com.bksgames.game.core.tiles.Mirror;
import com.bksgames.game.core.tiles.Tile;
import com.bksgames.game.core.tiles.Tunnel;
import com.bksgames.game.core.tiles.Nexus;
import com.bksgames.game.core.tiles.Wall;
import com.bksgames.game.core.utils.Point;
import com.bksgames.game.common.utils.Direction;
import com.bksgames.game.common.PlayerColor;

import java.util.*;

/**
 *  Representing {@code SquareBoard}
 * @author riper
 * @author jajko
 */
public class SquareBoard implements Board {

    final Tile[][] grid;
    final int size;
    final int baseSize;
    final EnumMap<PlayerColor, List<Nexus>> playerNexuses;

    //Board
    @Override
    public Tile getTile(int x, int y) {
        if(x>=size || y>=size || x<0 || y<0)
            return new Wall();
        return grid[x][y];
    }

    @Override
    public int getWidth() {
        return size;
    }
    @Override
    public int getHeight() {
        return size;
    }
    @Override
    public List<Nexus> getNexus(PlayerColor player) {
        return playerNexuses.get(player);
    }
    @Override
    public Set<Point> getVisible(Minion minion) {
        if(!getTile(minion.getX(),minion.getY()).isHollow())
            throw new IllegalArgumentException("Minion is in wall!");
        Set<Point> visible = new HashSet<>();
        visible.add(new Point(minion.getX(), minion.getY()));
        for(Direction d : Direction.values()){
            Point point = new Point(minion.getX(), minion.getY());
            visible.addAll(getLineOfSight(point, d,List.of(minion.owner())));
        }
        return getBiggerVision(visible);
    }
    @Override
    public Set<Point> getNexusesVision(PlayerColor player) {
        Set<Point> vision = new HashSet<>();
        for(Nexus nexus: playerNexuses.get(player))
        {
            vision.addAll(getNexusBase(nexus));
        }
        return vision;
    }
    @Override
    public List<Point> getLineOfSight(Point point, Direction direction, Collection<PlayerColor> canSee) {
        Map<Mirror, Set<Direction>> mirrorMap = new HashMap<>();
        List<Point> lineOfSight = new LinkedList<>();

       point = direction.getNext(point);
        Tile currentTile = getTile(point.x, point.y);

        while(currentTile.isHollow()){
            lineOfSight.add(new Point(point));
            Tunnel currentTunnel = currentTile.getTunnel();
            if(currentTunnel.getMirror()!=null ){
                if(!canSee.contains(currentTunnel.getMirror().owner())) {
                    break;
                }
                if(!mirrorMap.containsKey(currentTunnel.getMirror())){
                    mirrorMap.put(currentTunnel.getMirror(), new HashSet<>());
                    mirrorMap.get(currentTunnel.getMirror()).add(direction);
                }
                else if(!mirrorMap.get(currentTunnel.getMirror()).contains(direction)){
                    mirrorMap.get(currentTunnel.getMirror()).add(direction);
                }
                else {
                    direction = currentTunnel.getMirror().deflect(direction);
                    point = direction.getNext(point);
                    break;
                }
                direction = currentTunnel.getMirror().deflect(direction);
            }
            point = direction.getNext(point);
            currentTile = getTile(point.x, point.y);
        }
        lineOfSight.add(new Point(point));
        return lineOfSight;
    }

    /**
     * Constructs {@code SquareBoard} for {@code SquareBoardFactory}
     * @param size of {@code SquareBoard}
     * @param baseSize of bases when generating {@code SquareBoard}
     */
    SquareBoard(int size,int baseSize){
        this.size = size;
        this.baseSize = baseSize;
        playerNexuses = new EnumMap<>(PlayerColor.class);
        grid = new Tile[size][size];
    }

    private Set<Point> getNexusBase(Nexus nexus){
        Set<Point> base = new HashSet<>();
        Point actFP = new Point(nexus.getX()-baseSize/2,nexus.getY()-baseSize/2);
        for(int x=0;x<baseSize;x++)
        {

            actFP = new Point(actFP.x, nexus.getY()-baseSize/2);
            for(int y=0;y<baseSize;y++)
            {
                base.add(new Point(actFP));
                actFP = Direction.UP.getNext(actFP);
            }
            actFP = Direction.RIGHT.getNext(actFP);
        }
        return base;
    }

    private Set<Point> getAdjacent(Point point){
        Set<Point> adjacent = new HashSet<>();
        for(Direction direction: Direction.values()){
            adjacent.add(direction.getNext(point));
        }
        return adjacent;
    }

    private Set<Point> getBiggerVision(Set<Point> vision){
        Set<Point> biggerVision = new HashSet<>();
        for(Point p:vision){
            if(getTile(p).isHollow())
                biggerVision.addAll(getAdjacent(p));
        }
        biggerVision.addAll(vision);
        return biggerVision;
    }
}
