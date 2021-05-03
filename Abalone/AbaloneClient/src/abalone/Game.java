/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;

import game.Message;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Furkan ATAK
 */
public class Game {

    public static final int POSITIONS = 61;

    private int currentHovered;
    private final List<Integer> selectedPositions = new ArrayList<Integer>();
    private List<Integer> movedPositions = new ArrayList<Integer>();

    private final Board board;
    private final Keys theKeyListener;
    private final MainGui mgui;
    private Rival me;
    private Rival enemy;
    public static Game game;
    private int meOut;
    private int enemyOut;
    Game(Rival rival) {
        game = this;
        board = new Board();
        theKeyListener = new Keys(this);
        me = rival;
        enemy = me == Rival.yellow ? Rival.pink : Rival.yellow;
        mgui = new MainGui(theKeyListener, rival, this);
        initPositions();
        Client.Start("localhost", 2000);
        initHover();
    }

    private void initPositions() {
        Rival[] initState = new Rival[POSITIONS];
        for (int i = 0; i < 11; i++) {
            initState[i] = Rival.yellow;
        }
        for (int i = 13; i < 16; i++) {
            initState[i] = Rival.yellow;
        }
        for (int i = 45; i < 48; i++) {
            initState[i] = Rival.pink;
        }
        for (int i = 50; i < 61; i++) {
            initState[i] = Rival.pink;
        }
        board.setState(initState);
        renderCurrentBoardState();
    }

    private void initHover() {
        currentHovered = me == Rival.yellow ? 0 : 60;
    }

    boolean validateMultipleSelection(int arrayPos) {
        return true;
    }

    public void setRival(Rival rival) {
        me = rival;
        enemy = me == Rival.yellow ? Rival.pink : Rival.yellow;
        mgui.resetComm(rival);
        initHover();
    }
    public Rival getRival(){
        return this.me;
    }

    void processMove(Move move) {
        stopShowingMoved();
        if (selectedPositions.isEmpty()) {
            moveHover(move);
        } else {
            MoveResult moveResult = move(selectedPositions, move);
            if (moveResult.isAchieved) {
                setInputEnabled(false);
                selectedPositions.clear();
                renderCurrentBoardState();
            }
        }
    }

    private MoveResult move(List<Integer> selected, Move move) {
        boolean success = false;
        List<Integer> moved = new ArrayList<Integer>();
        boolean out = false;
        Rival team = board.getPlayer(selected.get(0));
        
        if (team == me) {
            if (selected.size() == 1) {
                int selection = selected.get(0);
                List<Integer> line = Board.getStraightLine(selection, move); //get line in direction until the border of the board
                //			System.out.println("linesize: " + line.size());
                if (line.size() > 1) { //current position not on the borders of the board
                    Rival nextStoneTeam = board.getPlayer(line.get(1));

                    if (nextStoneTeam == null) { //target position is available to move
                        board.setTeam(me, line.get(1));
                        board.setTeam(null, selection);
                        success = true;

                    } else if (nextStoneTeam == me) { //same team next on the line -> try to push
                        int ownCount = 0;
                        while (board.getPlayer(line.get(ownCount)) == me) { //how many consecutive of my team
                            ownCount++;
                            if (line.size() == ownCount) {
                                return new MoveResult(false, false); //line filled only with my own team, no move possible
                            }
                        }
                        boolean enemies = board.getPlayer(line.get(ownCount)) == enemy; //is one enemy directly next to my team?
                        int totalCount = ownCount;
                        if (enemies) {
                            while (totalCount < line.size() && board.getPlayer(line.get(totalCount)) == enemy) { //how much own and enemy consecutive pieces? 
                                totalCount++;
                            }
                        }
                        int enemyCount = totalCount - ownCount; //how much enemies? To end game this checked also
                        //move my pieces if (there are no enemies) or ((if enemies are lesser) and (there is not a piece of me directly after them))
                        
                        if (!enemies || ((ownCount > enemyCount && ownCount < 4)
                                && (totalCount < line.size() ? !(board.getPlayer(line.get(totalCount)) == me) : true))) {
                            success = true;
                            board.setTeam(null, selection);
                            int position;
                            for (int i = 0; i < ownCount; i++) { //push all my pieces 1 position (last one overwrites enemy if there is one)
                                position = line.get(i + 1);
                                board.setTeam(me, position);
                                moved.add(position);
                            }
                            if (enemies) {
                                for (int i = ownCount; i < totalCount - 1; i++) { //push all oponent pieces (but last one) 1 position
                                    position = line.get(i + 1);
                                    board.setTeam(enemy, position);
                                    moved.add(position);
                                }
                                if (totalCount == line.size()) { //if the line was full (consecutive), one enemy piece has to be kicked
                                    kickOut(enemy);
                                    out = true;
                                } else {
                                    board.setTeam(enemy, line.get(totalCount)); //push last oponent piece 1 position
                                }
                            }
                        }
                    }
                }
            } else {
                   // do nothing (don't move) 
            }
        }
        // Sending server moved list board state after move and is any stone out
        ArrayList<Object> arr = new ArrayList<>();
        arr.add(board.getState());
        arr.add(moved);
        arr.add(out);
        Message msg = new Message(Message.Message_Type.Send);
        msg.content = arr;
        Client.Send(msg);
        return new MoveResult(success, out, moved);
    }
    // game status received
    void receiveGameState(Rival[] gameState, List<Integer> movedList, boolean out) {
        setNewBoardState(gameState);
        showMovedPositions(movedList);
        if (out) {
            kickOut(me);
        }
        setInputEnabled(true);
    }
    // show the moved position
    public void showMovedPositions(List<Integer> moved) {
        movedPositions = moved;
        for (int i : moved) {
            mgui.drawPositionState(i, board.getPlayer(i), false, false, true);
        }
        mgui.flushBoard();
    }
    //after oppenent plays(with hover) remove shadow
    public void stopShowingMoved() {
        if (!movedPositions.isEmpty()) {
            for (int i : movedPositions) {
                mgui.drawPositionState(i, board.getPlayer(i), false, false, false);
            }
            movedPositions.clear();
            mgui.flushBoard();
        }
    }
    //the stone kicked out
    private void kickOut(Rival rival) {
        if (rival == me) {
            meOut++;
        } else {
            enemyOut++;
        }
         mgui.showKickOut(rival);
        if (meOut == 6) {
                 mgui.showWinner(enemy.toString());
            setInputEnabled(false);
        } else if (enemyOut == 6) {
                 mgui.showWinner(me.toString());
            setInputEnabled(false);
        }
    }
    // keyboard input blocking according to turn of players
    void setInputEnabled(boolean inputEnabled) {
        theKeyListener.setInputEnabled(inputEnabled);
    }
    // after each change on the board restate the board
    void setNewBoardState(Rival[] boardState) {
        board.setState(boardState);
        renderCurrentBoardState();
    }
    
    void procesSingleSelection() {
        if (selectedPositions.isEmpty()) {
            addSelection();
        } else {
            clearSelections();
        }
    }

    private void addSelection() {
        if (board.getPlayer(currentHovered) == me) {
            selectedPositions.add(currentHovered);
            mgui.drawPositionState(currentHovered, me, true, false, false);
        }
    }

    private void addSelection(Move move) {
        System.out.println("Game.addSelection()");
    }

    private void clearSelections() {
        for (int i = 0; i < selectedPositions.size(); i++) {
            mgui.drawPositionState(selectedPositions.get(i), me, false, false, false);
        }
        mgui.flushBoard();
        selectedPositions.clear();
    }

    void processMultipleSelection(Move move) {
        stopShowingMoved();
        addSelection(move);
    }

    private void moveHover(Move move) {
        int newHovered = board.getNeighbour(currentHovered, move);
        if (newHovered != -1) { 
            mgui.drawPositionState(currentHovered, board.getPlayer(currentHovered), false, false, false);
            mgui.drawPositionState(newHovered, board.getPlayer(newHovered), false, true, false);
            mgui.flushBoard();
            currentHovered = newHovered;
        }
    }

    private void renderCurrentBoardState() {
        mgui.clearBoard();
        for (int i = 0; i < POSITIONS; i++) {
            mgui.drawPositionState(i, board.getPlayer(i), false, false, false);
        }
        mgui.drawPositionState(currentHovered, board.getPlayer(currentHovered), false, true, false);
        mgui.flushBoard();
    }

    void quit() {
        System.exit(0);
    }
}

// KeyListener interface implementation
class Keys implements KeyListener {

    private final Game game;

    private final int multiSelectKey = KeyEvent.VK_SHIFT;
    private final int selectKey = KeyEvent.VK_SPACE;

    private HashMap<Integer, Move> moveKey = new HashMap<Integer, Move>();

    private boolean inputEnabled = true;
    private boolean multiSelect = false;

    Keys(Game game) {
        this.game = game;
        moveKey.put(KeyEvent.VK_W, Move.leftUpper);
        moveKey.put(KeyEvent.VK_E, Move.rightUpper);
        moveKey.put(KeyEvent.VK_D, Move.right);
        moveKey.put(KeyEvent.VK_C, Move.rightDown);
        moveKey.put(KeyEvent.VK_X, Move.leftDown);
        moveKey.put(KeyEvent.VK_A, Move.upper);
    }

    void processKeyEvent(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (inputEnabled) {
            int keyCode = e.getKeyCode();

            if (keyCode == multiSelectKey) {
                multiSelect = true;
            } else if (keyCode == selectKey) {
                game.procesSingleSelection();
            } else if (moveKey.keySet().contains(keyCode)) {
                Move move = moveKey.get(keyCode);
                if (multiSelect) {
                    game.processMultipleSelection(move);
                } else {
                    game.processMove(move);
                }
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == multiSelectKey) {
            multiSelect = false;
        }
    }

    public void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }

    public void keyTyped(KeyEvent arg0) {
    }
}
// results of the move
class MoveResult {

    boolean isAchieved;
    List<Integer> movedList;
    boolean out;

    MoveResult(boolean isAchieved) {
        this.isAchieved = isAchieved;
    }

    MoveResult(boolean isAchieved, boolean out) {
        this.isAchieved = isAchieved;
        this.out = out;
    }

    MoveResult(boolean isAchieved, boolean out, List<Integer> movedList) {
        this.isAchieved = isAchieved;
        this.movedList = movedList;
        this.out = out;
    }
}
