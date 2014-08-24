package main;

public interface GameListener {
	public void gameOver(boolean playerWon);

    public void quitGame();

	/** TODO: Does this mean anything? */
	public void levelUp();
}
