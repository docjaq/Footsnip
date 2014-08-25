package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import location.LocationThread;
import physics.PhysicsThread;
import renderer.Renderer_4_0;
import thread.GameThread;
import thread.ObservableThread;
import thread.ThreadObserver;
import assets.AssetContainer;
import audio.AudioEngine;
import collision.CollisionThread;
import control.ControlThread;
import exception.RendererException;

public class Main implements GameListener {

	private List<GameThread> childThreads = new ArrayList<GameThread>(5);

	private long startMillis = System.currentTimeMillis();

	// TODO: define this threadpool size AFTER the threads have been defined so
	// we don't have to hardcode a value
	final ExecutorService executor = Executors.newFixedThreadPool(5);

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
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	public Main() throws RendererException, ExecutionException, InterruptedException {
		// Tell GameControl that this class wants to know when big stuff
		// happens.
		GameControl.registerGameListener(this);

		final AssetContainer assContainer = new AssetContainer();

		GameThread physicsThread = new PhysicsThread(assContainer, 2, Main.this);

		GameThread rendererThread = new Renderer_4_0(assContainer, this);
		Future<?> rendererFuture = executor.submit(rendererThread);
		childThreads.add(rendererThread);

		rendererThread.registerSetupObserver(new ThreadObserver() {
			@Override
			public void setupDone(ObservableThread subject) {

				// Created before renderer to get reference
				childThreads.add(physicsThread);
				executor.execute(physicsThread);

				// Renderer is set up, so start the control thread.
				GameThread controlThread = new ControlThread(assContainer, 10, Main.this);
				childThreads.add(controlThread);
				executor.execute(controlThread);

				GameThread collisionThread = new CollisionThread(assContainer, 10, Main.this);
				childThreads.add(collisionThread);
				executor.execute(collisionThread);

				GameThread locationThread = new LocationThread(assContainer, 10, Main.this);
				childThreads.add(locationThread);
				executor.execute(locationThread);
			}
		});

		AudioEngine.getInstance();

		GameControl.startGame();

		quitWhenRendererFinishes(rendererFuture);
	}

	private void quitWhenRendererFinishes(Future<?> rendererFuture) throws ExecutionException, InterruptedException {
		rendererFuture.get();
		quitGame();
	}

	public void quitGame() {
		GameControl.stopGame();
		AudioEngine.getInstance().close();
		shutdownExecutor();
	}

	private void shutdownExecutor() {
		executor.shutdown();
		try {
			if (!executor.awaitTermination(10L, TimeUnit.SECONDS)) {
				System.err.println("Gave up waiting for threads to end naturally; killing them...");
				executor.shutdownNow();
				if (!executor.awaitTermination(10L, TimeUnit.SECONDS)) {
					System.err.println("Thread pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void gameOver(boolean playerWon) {
		if (playerWon) {
			System.out.println("You win.");
		} else {
			System.out.println("You lose.");
		}

		long score = System.currentTimeMillis() - startMillis;
		System.out.println("Score: " + score);

		quitGame();
	}

	@Override
	public void levelUp() {
		// TODO Is this a thing?
	}
}

/**
 * 
 * 
 * TODO: RENDERING:
 * 
 * - Seems sort of acceptable for now I think. Other more important things.
 * 
 * - Figure out how to do more passes on a single object. I.e. multiple lights,
 * procedural textures, etc. Think you can either call multiple shader programs,
 * or attach multiple shaders to a program. Going to need to read-up on this.
 * 
 * TODO: COLLISION:
 * 
 * - This is now a next step I guess. The one problem is it A) relies on a mesh
 * data-structure (see below), and B) on a scenegraph (see below)
 * 
 * TODO: SCENE-GRAPH:
 * 
 * - This is again a fair bit of work, but is pretty important for A)
 * hierarchies of stuff for lots of things (rendering, physics, collision, etc)
 * 
 * TODO: MESH STUFF:
 * 
 * - An easier way to define geometry. Soooo messy atm. Need to decide how we're
 * going to do this. I.e. just procedural algorithms, some loading/saving, etc.
 * 
 * - Some classes to create some basic Parametric (possibly?) mesh geometry
 * 
 * - A mesh data-structure. I'm for a half-edge one. It's overkill for gaming,
 * but will allow us to more easily do some cool stuff like: subdivision, CSG
 * (and other boolean-type operations). Judging by what I've found online, we
 * may need to write this ourselves...
 * 
 * - These are all really linked. 1) Define data-structure (DS) for meshes, 2)
 * Algorithms to create mesh. Put into DS, 3) Algorithms to put DS mesh into
 * opengl VBOs/VAOs, 4) Algorithms to correctly handle updating VBOs/VAOs when
 * mesh changes
 * 
 * TODO: PERSISTENCE:
 * 
 * - For defining a monster, game parameters. Perhaps JSON?
 * 
 * - Probably quite important so that we can get rid of some complexity in the
 * rendering/main methods?
 * 
 * TODO: PHYSICS:
 * 
 * - Our ship physics work, but are pretty basic. Not sure how to integrate more
 * into the rest of the game. More reading required.
 */
