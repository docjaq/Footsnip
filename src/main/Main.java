package main;

import java.util.ArrayList;
import java.util.List;

import renderer.Renderer_3_2;
import thread.GameThread;
import thread.ObservableThread;
import thread.ThreadObserver;
import assets.AssetContainer;
import control.ControlThread;
import exception.RendererException;

public class Main {

	private List<GameThread> childThreads = new ArrayList<GameThread>(2);

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
		final AssetContainer assContainer = new AssetContainer();

		GameThread rendererThread = new Renderer_3_2(assContainer, this);

		rendererThread.setPriority(Thread.MAX_PRIORITY);
		rendererThread.start();
		childThreads.add(rendererThread);

		rendererThread.registerSetupObserver(new ThreadObserver() {
			@Override
			public void setupDone(ObservableThread subject) {
				// Renderer is set up, so start the control thread.
				GameThread controlThread = new ControlThread(assContainer, 10, Main.this);
				controlThread.setPriority(Thread.MIN_PRIORITY);
				controlThread.start();
				childThreads.add(controlThread);
			}
		});
	}

	public void quitGame() {
		// Nicely stop the child threads.
		for (GameThread thread : childThreads) {
			thread.stopThread();
		}
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
