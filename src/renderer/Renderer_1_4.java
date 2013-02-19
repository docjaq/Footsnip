package renderer;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class Renderer_1_4 {

	/** position of quad */
	float x = 0, y = 0, z = -400;
	/** angle of quad rotation */
	float rotation = 0;
	
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	private final int HALF_WIDTH = WIDTH/2;
	private final int HALF_HEIGHT = HEIGHT/2;
	
	/** time at last frame */
	long lastFrame;
	
	/** frames per second */
	int fps;
	/** last fps time */
	long lastFPS;

	public void start() {
		try {
			/*PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
			.withForwardCompatible(false)
			.withProfileCore(true);*/
			
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(/*pixelFormat, contextAtrributes*/);
			System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		initGL(); // init OpenGL
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer

		while (!Display.isCloseRequested()) {
			int delta = getDelta();
			
			update(delta);
			renderGL();

			Display.update();
			Display.sync(0); // cap fps to 60fps
		}

		Display.destroy();
	}
	
	public void update(int delta) {
		// rotate quad
		rotation += 0.15f * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) x -= 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) x += 0.35f * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) y -= 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) y += 0.35f * delta;
		
		//For now, forces a shitty bounding box
		//Doesn't really work with a perspective projection
		if (x < -HALF_WIDTH) x = -HALF_WIDTH;
		if (x > HALF_WIDTH) x = HALF_WIDTH;
		if (y < -HALF_HEIGHT) y = -HALF_HEIGHT;
		if (y > HALF_HEIGHT) y = HALF_HEIGHT;
		
		updateFPS(); // update FPS Counter
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	//Have converted it to a camera with perspective
	public void initGL() {
		GL11.glClearDepth(1.0f); // clear depth buffer
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables depth testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // sets the type of test to use for depth
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		float fovy = 45.0f;
        float aspect = (float)WIDTH/(float)HEIGHT;
        float zNear = 0.1f;
        float zFar = 100000.0f;
        GLU.gluPerspective(fovy, aspect, zNear, zFar);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
	}

	/*
	 * No vertex buffer objects (VBOs) or even display lists used yet,
	 * so must redraw object every time
	 */
	public void renderGL() {
		GL11.glLoadIdentity();
		// Clear The Screen And The Depth Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		//GL11.glColor3f(1.0f, 0.0f, 0.0f);

		//System.out.println("Position: " + x + "," + y + "," + z);
		
		// draw quad
		GL11.glPushMatrix();
			
			GL11.glTranslatef(x, y, z);
			//GL11.glRotatef(rotation, 0f, 0f, 1f);
			//GL11.glTranslatef(x, y, z);
			
			/*GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex3f(x - 50, y - 50, z);
				GL11.glVertex3f(x + 50, y - 50, z);
				GL11.glVertex3f(x + 50, y + 50, z);
				GL11.glVertex3f(x - 50, y + 50, z);
			GL11.glEnd();*/
			
			GL11.glScalef(10.1f, 10.1f, 10.1f);
			renderCube();
			
			//Sphere sphere = new Sphere();
			//sphere.draw(50f, 16, 16);
		GL11.glPopMatrix();
	}
	
	/*
	 * Just using this so that I can see what projection the camera is applying
	 * a bit more visually
	 */
	private void renderCube(){
		GL11.glBegin(GL11.GL_QUADS);            // Draw A Quad
        GL11.glColor3f(0.0f, 1.0f, 0.0f);     // Set The Color To Green
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);   // Top Right Of The Quad (Top)
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Left Of The Quad (Top)
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);   // Bottom Left Of The Quad (Top)
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);    // Bottom Right Of The Quad (Top)

        GL11.glColor3f(1.0f, 0.5f, 0.0f);     // Set The Color To Orange
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);   // Top Right Of The Quad (Bottom)
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);  // Top Left Of The Quad (Bottom)
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Bottom)
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Right Of The Quad (Bottom)

        GL11.glColor3f(1.0f, 0.0f, 0.0f);     // Set The Color To Red
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);    // Top Right Of The Quad (Front)
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);   // Top Left Of The Quad (Front)
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);  // Bottom Left Of The Quad (Front)
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);   // Bottom Right Of The Quad (Front)

        GL11.glColor3f(1.0f, 1.0f, 0.0f);     // Set The Color To Yellow
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Left Of The Quad (Back)
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Back)
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Right Of The Quad (Back)
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);   // Top Left Of The Quad (Back)

        GL11.glColor3f(0.0f, 0.0f, 1.0f);     // Set The Color To Blue
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);   // Top Right Of The Quad (Left)
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Left Of The Quad (Left)
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Left)
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);  // Bottom Right Of The Quad (Left)

        GL11.glColor3f(1.0f, 0.0f, 1.0f);     // Set The Color To Violet
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);   // Top Right Of The Quad (Right)
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);    // Top Left Of The Quad (Right)
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);   // Bottom Left Of The Quad (Right)
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Right Of The Quad (Right)
        GL11.glEnd();
	}
	
	public static void main(String[] argv) {
		Renderer_1_4 example = new Renderer_1_4();
		example.start();
	}
}
