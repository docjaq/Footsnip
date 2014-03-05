package camera;

import math.types.Vector2;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import camera.CameraModel.MouseButton;
import camera.CameraModel.MouseModifier;
import camera.CameraModel.ObjectPole;
import camera.CameraModel.ViewPole;

public final class CameraUtils {
	/**
	 * Gets the current time in milliseconds, using LWJGLs high resolution
	 * timer.
	 * 
	 * @return The current time in milliseconds.
	 */
	public static long getTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static void updateMousePoles(ViewPole... viewPoles) {
		updateMousePoles(viewPoles, null);
	}

	public static void updateMousePoles(ObjectPole... objectPoles) {
		updateMousePoles(null, objectPoles);
	}

	public static void updateMousePoles(ViewPole viewPole, ObjectPole objectPole) {
		updateMousePoles(viewPole == null ? null : new ViewPole[] { viewPole }, objectPole == null ? null : new ObjectPole[] { objectPole });
	}

	public static void updateMousePoles(ViewPole[] viewPoles, ObjectPole[] objectPoles) {
		while (Mouse.next()) {
			MouseButton button = MouseButton.getButton(Mouse.getEventButton());
			if (button != null) {
				boolean pressed = Mouse.getEventButtonState();
				if (viewPoles != null)
					for (ViewPole v : viewPoles)
						v.mouseClick(button, pressed, getModifier(), new Vector2(Mouse.getX(), Mouse.getY()));
				if (objectPoles != null)
					for (ObjectPole o : objectPoles)
						o.mouseClick(button, pressed, getModifier(), new Vector2(Mouse.getX(), Mouse.getY()));
			} else {
				int dwheel = Mouse.getDWheel();

				if (dwheel != 0) {
					if (viewPoles != null)
						for (ViewPole v : viewPoles)
							v.mouseWheel(dwheel, getModifier());
					if (objectPoles != null)
						for (ObjectPole o : objectPoles)
							o.mouseWheel(dwheel, getModifier());
				} else {
					if (viewPoles != null)
						for (ViewPole v : viewPoles)
							v.mouseMove(new Vector2(Mouse.getX(), Mouse.getY()));
					if (objectPoles != null)
						for (ObjectPole o : objectPoles)
							o.mouseMove(new Vector2(Mouse.getX(), Mouse.getY()));
				}
			}
		}
	}

	private static MouseModifier getModifier() {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return MouseModifier.KEY_SHIFT;

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
			return MouseModifier.KEY_CTRL;

		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
			return MouseModifier.KEY_ALT;

		return null;
	}
}
