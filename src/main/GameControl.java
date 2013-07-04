package main;

import java.util.ArrayList;
import java.util.List;

public final class GameControl {
	private static List<GameListener> gameListeners;

	public static void registerGameListener(GameListener gameListener) {
		if (gameListeners == null) {
			gameListeners = new ArrayList<GameListener>();
		}

		gameListeners.add(gameListener);
	}

	public static void playerDead() {
		for (GameListener gameListener : gameListeners) {
			gameListener.gameOver(false);
		}
	}

	public static void monstersDead() {
		for (GameListener gameListener : gameListeners) {
			gameListener.gameOver(true);
		}
	}
}
