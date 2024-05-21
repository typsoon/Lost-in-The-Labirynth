package com.bksgames.game.views.gameScreen.legalMovesHandling;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.bksgames.game.globalClasses.enums.Direction;
import com.bksgames.game.globalClasses.enums.ActionToken;
import com.bksgames.game.viewmodels.moves.IncompleteMove;
import com.bksgames.game.views.gameScreen.MazeMapFactory;
import com.bksgames.game.views.gameScreen.legalMovesHandling.actionButtons.ActionButtonFactory;

import java.util.HashMap;
import java.util.Map;

public class MainTableFactory {
	static final int arrowButtonSize = 100;
	static final int distanceToAdjacentButton = 10;
	static final int arrowTableWidth = 200;
	static final int arrowTableHeight = 100;

	static Map<ActionToken, Table> mapping = new HashMap<>();
	public static Table produce(Table arrowTable, Table actionsTable, ActionButtonFactory factory){

		Table mainTable = new Table();
		mainTable.setPosition((MazeMapFactory.maxBoardWidth+ 10)*MazeMapFactory.tilePixelSize, (MazeMapFactory.maxBoardHeight)*MazeMapFactory.tilePixelSize);
		mainTable.align(Align.right);
		mainTable.setVisible(false);

		arrowTable.align(Align.bottom);
		actionsTable.align(Align.top);

		mapping.put(ActionToken.MOVE, arrowTable);

		mapping.put(ActionToken.DOOR, actionsTable);
		mapping.put(ActionToken.LASER, actionsTable);
		mapping.put(ActionToken.SWORD, actionsTable);
		mapping.put(ActionToken.MIRROR, actionsTable);

		arrowTable.setFillParent(true);

		Actor upArrow = factory.getButton(new IncompleteMove(ActionToken.MOVE, Direction.UP));

		arrowTable.add().expand();

		addArrow(upArrow, arrowTable).padBottom(distanceToAdjacentButton).padRight(distanceToAdjacentButton);

		arrowTable.row();

		addArrow(factory.getButton(new IncompleteMove(ActionToken.MOVE, Direction.LEFT)), arrowTable).padRight(distanceToAdjacentButton);
		addArrow(factory.getButton(new IncompleteMove(ActionToken.MOVE, Direction.DOWN)), arrowTable).padRight(distanceToAdjacentButton);
		addArrow(factory.getButton(new IncompleteMove(ActionToken.MOVE, Direction.RIGHT)), arrowTable);


		mainTable.addActor(actionsTable);
		mainTable.addCaptureListener(event -> arrowTable.notify(event, true));
		mainTable.addCaptureListener(event -> actionsTable.notify(event, true));

		mainTable.row();
		mainTable.addActor(arrowTable);
//		arrowTable.debugAll();

		for (Actor actor : arrowTable.getChildren()) {
			// Set the alpha value (opacity) for each actor
			Color color = actor.getColor(); // Get the current color of the actor
			color.a = 0.5f; // Set the alpha channel to the desired opacity value
			actor.setColor(color); // Apply the modified color to the actor
		}

		return mainTable;
	}

	private static Cell<Actor> addArrow(Actor arrowButton, Table arrowTable) {
		Cell<Actor> answer = arrowTable.add(arrowButton);
		arrowTable.addCaptureListener(event -> {
			if (!arrowButton.isVisible())
				return false;
			return arrowButton.notify(event, true);
		});
		return answer;
	}
}
