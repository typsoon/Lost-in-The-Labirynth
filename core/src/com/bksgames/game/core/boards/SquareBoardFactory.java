package com.bksgames.game.core.boards;

import com.bksgames.game.common.utils.Direction;
import com.bksgames.game.core.main.BoardManager;
import com.bksgames.game.core.tiles.Nexus;
import com.bksgames.game.core.utils.Parameters;
import com.bksgames.game.core.tiles.Tunnel;
import com.bksgames.game.core.tiles.Wall;
import com.bksgames.game.core.utils.Point;
import com.bksgames.game.common.PlayerColor;

import java.util.ArrayList;
import java.util.Random;

/**
 * Factory class for {@code SquareBoard}-s
 * @author riper
 */
public class SquareBoardFactory
{
    /**
     * @return simple {@code SquareBoard} for 2 {@code Players}
     */
    static public SquareBoard CreateSBFor2Players(Parameters parameters, BoardManager boardManager)    {
        SquareBoard board = new SquareBoard(Math.max(parameters.mapSize(), 27), parameters.baseSize()); // do przemyslenia

        board.playerNexuses.put(PlayerColor.RED,new ArrayList<>());
        board.playerNexuses.put(PlayerColor.BLUE,new ArrayList<>());

        Random rng = new Random();
        int[][] genGrid = new int[board.size][board.size];
        int realSize = board.size - (board.size%3);
        int sectionSize = realSize/3;

        //============================================NEXUS SECTION
        int fpNexusSec = rng.nextInt(8);
        if(fpNexusSec==4)
            fpNexusSec=8;
        ArrayList<Integer> choose = new ArrayList<>();
        for(int i=0;i<9;i++)
        {
            if(i==4||i==fpNexusSec || i==fpNexusSec+3 || i==fpNexusSec-3)
                continue;
            if(fpNexusSec%3 != 0 && i == fpNexusSec-1)
                continue;
            if(fpNexusSec%3 != 2 && i == fpNexusSec+1)
                continue;
            choose.add(i);
        }
        int spNexusSec = choose.get(rng.nextInt(choose.size()));
        if(fpNexusSec > spNexusSec)
        {
            int pom = spNexusSec;
            spNexusSec=fpNexusSec;
            fpNexusSec=pom;
        }
        //==============================================NEXUS OFFSET
        Point fpNexusOffset = new Point(
                rng.nextInt(sectionSize- parameters.baseSize()-1) + (fpNexusSec%3) * sectionSize,
                rng.nextInt(sectionSize-parameters.baseSize()-1) + (fpNexusSec/3) * sectionSize
        );
        Point spNexusOffset = new Point(
                rng.nextInt(sectionSize-parameters.baseSize()-1) + (spNexusSec%3) * sectionSize,
                rng.nextInt(sectionSize-parameters.baseSize()-1) + (spNexusSec/3) * sectionSize
        );

        for(int x=1;x<=parameters.baseSize();x++)
        {
            for(int y=1;y<=parameters.baseSize();y++)
            {
                genGrid[x+fpNexusOffset.x][y+fpNexusOffset.y] = 1;
                genGrid[x+spNexusOffset.x][y+spNexusOffset.y] = 2;
            }
        }

        //==========================================FIRST PATH
        Point fpSplit,spSplit;
        if(fpNexusSec/3 == spNexusSec/3) // w jednym wierszu
        {
            fpSplit = new Point(fpNexusOffset.x+parameters.baseSize()+1,fpNexusOffset.y+2+rng.nextInt(parameters.baseSize()-2));
            spSplit = new Point(spNexusOffset.x,spNexusOffset.y+2+rng.nextInt(parameters.baseSize()-2));
        }
        else if(fpNexusSec%3 == spNexusSec%3) // w jednej kolumnie
        {
            fpSplit = new Point(fpNexusOffset.x+2+rng.nextInt(parameters.baseSize()-2),fpNexusOffset.y+parameters.baseSize()+1);
            spSplit = new Point(spNexusOffset.x+2+rng.nextInt(parameters.baseSize()-2),spNexusOffset.y);
        }
        else //reszta
        {
            ArrayList<Point> conFP = new ArrayList<>();
            ArrayList<Point> conSP = new ArrayList<>();
            for(int i=1;i<parameters.baseSize()-1;i++){
                conFP.add(new Point(fpNexusOffset.x+1+i,fpNexusOffset.y+parameters.baseSize()+1));
                conSP.add(new Point(spNexusOffset.x+1+i,spNexusOffset.y));
            }
            if(fpNexusSec%3<spNexusSec%3)
            {
                for(int i=1;i<parameters.baseSize()-1;i++){
                    conFP.add(new Point(fpNexusOffset.x+parameters.baseSize()+1,fpNexusOffset.y+1+i));
                    conSP.add(new Point(spNexusOffset.x,spNexusOffset.y+1+i));
                }
            }
            else{
                for(int i=1;i<parameters.baseSize()-1;i++){
                    conFP.add(new Point(fpNexusOffset.x,fpNexusOffset.y+1+i));
                    conSP.add(new Point(spNexusOffset.x+parameters.baseSize()+1,spNexusOffset.y+1+i));
                }

            }
            fpSplit = conFP.get(rng.nextInt(conFP.size()));
            spSplit = conSP.get(rng.nextInt(conSP.size()));
        }
        BoardGenerationUtils.randomPath(fpSplit,spSplit,3,genGrid,board.size,board.size);
        //====================================SECOND & THIRD PATH
        ArrayList<Point> conFP = new ArrayList<>();
        for(int i=2;i<parameters.baseSize();i++)
            conFP.add(new Point(fpNexusOffset.x+i,fpNexusOffset.y));
        for(int i=2;i<parameters.baseSize();i++)
            conFP.add(new Point(fpNexusOffset.x+parameters.baseSize()+1,fpNexusOffset.y+i));
        for(int i=parameters.baseSize()-1;i>=2;i--)
            conFP.add(new Point(fpNexusOffset.x+i,fpNexusOffset.y+parameters.baseSize()+1));
        for(int i=parameters.baseSize()-1;i>=2;i--)
            conFP.add(new Point(fpNexusOffset.x,fpNexusOffset.y+i));
        while(genGrid[conFP.getFirst().x][conFP.getFirst().y]==0)
            conFP.add(conFP.removeFirst());
        while(genGrid[conFP.getFirst().x][conFP.getFirst().y]!=0)
            conFP.removeFirst();
        Point sdConFP = conFP.get(rng.nextInt(conFP.size()/2));
        Point tdConFP = conFP.get(conFP.size() - 1 - rng.nextInt(conFP.size()/2));



        ArrayList<Point> conSP = new ArrayList<>();
        for(int i=2;i<parameters.baseSize();i++)
            conSP.add(new Point(spNexusOffset.x+i,spNexusOffset.y));
        for(int i=2;i<parameters.baseSize();i++)
            conSP.add(new Point(spNexusOffset.x+parameters.baseSize()+1,spNexusOffset.y+i));
        for(int i=parameters.baseSize()-1;i>=2;i--)
            conSP.add(new Point(spNexusOffset.x+i,spNexusOffset.y+parameters.baseSize()+1));
        for(int i=parameters.baseSize()-1;i>=2;i--)
            conSP.add(new Point(spNexusOffset.x,spNexusOffset.y+i));
        while(genGrid[conSP.getFirst().x][conSP.getFirst().y]==0)
            conSP.add(conSP.removeFirst());
        while(genGrid[conSP.getFirst().x][conSP.getFirst().y]!=0)
            conSP.removeFirst();
        Point tdConSP = conSP.get(rng.nextInt(conSP.size()/2));
        Point sdConSP = conSP.get(conSP.size() - 1 - rng.nextInt(conSP.size()/2));

        BoardGenerationUtils.randomPath(sdConSP,sdConFP,3,genGrid,board.size,board.size);
        BoardGenerationUtils.randomPath(tdConSP,tdConFP,3,genGrid,board.size,board.size);


        BoardGenerationUtils.generateRest(genGrid,board.size,board.size,3);
        /* TEST
          for(int y=0;y<board.size;y++) {
            for (int x = 0; x < board.size; x++) {
                System.out.print(genGrid[x][y]);
            }
            System.out.println();
        }*/



        for(int y=0;y<board.size;y++)
        {
            for(int x=0;x<board.size;x++)
            {
                if(genGrid[x][y] == 0)
                    board.grid[x][y] = new Wall();
                else if(genGrid[x][y] == 3)
                    board.grid[x][y] = new Tunnel();
            }
        }
        Point actFP = new Point(fpNexusOffset.x,fpNexusOffset.y),
                actSP = new Point(spNexusOffset.x,spNexusOffset.y);
        for(int x=1;x<=parameters.baseSize();x++)
        {
            actFP = Direction.RIGHT.getNext(actFP);
            actSP = Direction.RIGHT.getNext(actSP);

            actFP = new Point(actFP.x,fpNexusOffset.y);
            actSP = new Point(actSP.x,spNexusOffset.y);
            for(int y=1;y<=parameters.baseSize();y++)
            {

                actFP = Direction.UP.getNext(actFP);
                actSP = Direction.UP.getNext(actSP);
                if(x==4 && y==4)
                {
                    if(rng.nextInt(10000)%2==0){
                        board.grid[actFP.x][actFP.y] = new Nexus(PlayerColor.RED,actFP,parameters.nexusHitPoints(),boardManager);
                        board.playerNexuses.get(PlayerColor.RED).add((Nexus) board.grid[actFP.x][actFP.y]);
                        board.grid[actSP.x][actSP.y] = new Nexus(PlayerColor.BLUE,actSP,parameters.nexusHitPoints(),boardManager);
                        board.playerNexuses.get(PlayerColor.BLUE).add((Nexus) board.grid[actSP.x][actSP.y]);
                    }else {
                        board.grid[actFP.x][actFP.y] = new Nexus(PlayerColor.BLUE,actFP,parameters.nexusHitPoints(),boardManager);
                        board.playerNexuses.get(PlayerColor.BLUE).add((Nexus) board.grid[actFP.x][actFP.y]);
                        board.grid[actSP.x][actSP.y] = new Nexus(PlayerColor.RED,actSP,parameters.nexusHitPoints(),boardManager);
                        board.playerNexuses.get(PlayerColor.RED).add((Nexus) board.grid[actSP.x][actSP.y]);
                    }

                    continue;
                }
                if(!(x==1 || x==parameters.baseSize() || y==1 || y==parameters.baseSize()))
                {
                    board.grid[actFP.x][actFP.y] = new Tunnel();
                    board.grid[actSP.x][actSP.y] = new Tunnel();
                    continue;
                }
                if(BoardGenerationUtils.adjacent(actFP,fpSplit) || BoardGenerationUtils.adjacent(actFP,sdConFP) ||
                        BoardGenerationUtils.adjacent(actFP,tdConFP))
                    board.grid[actFP.x][actFP.y] = new Tunnel();
                else
                    board.grid[actFP.x][actFP.y] = new Wall();

                if(BoardGenerationUtils.adjacent(actSP,spSplit) || BoardGenerationUtils.adjacent(actSP,sdConSP) ||
                        BoardGenerationUtils.adjacent(actSP,tdConSP))
                    board.grid[actSP.x][actSP.y] = new Tunnel();
                else
                    board.grid[actSP.x][actSP.y] = new Wall();
            }
        }
        return board;
    }
}
