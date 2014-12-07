package main;

import java.util.ArrayList;
import java.util.List;

public final class GameControl {
	private static List<GameListener> gameListeners;

	private static GameState gameState;

	public static void registerGameListener(GameListener gameListener) {
		if (gameListeners == null) {
			gameListeners = new ArrayList<GameListener>();
		}

		gameListeners.add(gameListener);
	}

	public static void playerDead() {
		GameControl.setGameState(GameState.PLAYER_LOST);
	}

	public static void asteroidsDead() {
		GameControl.setGameState(GameState.PLAYER_WON);
	}

	public static GameState getGameState() {
		return gameState;
	}

	public static void setGameState(GameState gameState) {
		GameControl.gameState = gameState;
	}
}
