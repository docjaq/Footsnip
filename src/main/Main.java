package main;

import java.util.ArrayList;
import java.util.List;

import renderer.Renderer_3_2;
import thread.GameThread;
import assets.Player;
import control.ControlThread;
import exception.RendererException;

public class Main {

	List<GameThread> childThreads = new ArrayList<GameThread>(2);

	/*********************************
	 * JAQ Levels should maybe be entities, as it would seemingly make
	 * intersections etc simpler to do (as you only ever have to intersect two
	 * entities). Having said that, we might use special rules to intersect
	 * entities, with, say, zone 'boundaries' or something... so I'm not sure
	 * again. Plus things like boundbox unions would be confusing up for a map.
	 * Hmm.
	 */

	public static void main(String[] args) {
		try {
			new Main();
		} catch (RendererException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	public Main() throws RendererException {
		Player player = new Player("Dave the Cunt", 0, new float[] { 1.0f, 0.0f, 0.0f });

		GameThread rendererThread = new Renderer_3_2(player, 10, this);
		rendererThread.start();
		childThreads.add(rendererThread);

		// Wait for the renderer thread to be set up.
		// TODO: Some kind of notification mechanism would probably be better
		// here...
		while (!rendererThread.isSetupDone()) {
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Setting up control");

		// Thread for input.
		GameThread controlThread = new ControlThread(player, 10, this);
		controlThread.start();
		childThreads.add(controlThread);
	}

	public void quitGame() {
		// Nicely stop the child threads.
		for (GameThread thread : childThreads) {
			thread.stopThread();
		}
	}
}

/**
 * TODO: J: Need to set the model centres better, as currently they're on the
 * plane
 * 
 * TODO: J: LIGHTING: Add lighting to the game. I'm currently looking into this
 * in OGL3.3
 */
