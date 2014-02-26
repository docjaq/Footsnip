package util;

import maths.types.Matrix4;
import maths.types.Quaternion;
import maths.types.Vector2;
import maths.types.Vector3;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import util.MousePoles.MouseButton;
import util.MousePoles.MouseModifier;
import util.MousePoles.ObjectPole;
import util.MousePoles.ViewPole;

public final class Utils {
	/**
	 * Gets the current time in milliseconds, using LWJGLs high resolution
	 * timer.
	 * 
	 * @return The current time in milliseconds.
	 */
	public static long getTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static Quaternion angleAxisDeg(float angle, Vector3 vec) {
		return new Quaternion((float) Math.toRadians(angle), vec);
	}

	public static float clamp(float value, float low, float high) {
		return Math.min(Math.max(value, low), high);
	}

	public static float mix(float f1, float f2, float a) {
		return f1 + (f2 - f1) * a;
	}

	public static Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
		Vector3 f = center.copy().sub(eye).normalize();
		up = up.copy().normalize();

		Vector3 s = f.cross(up);
		Vector3 u = s.cross(f);

		return new Matrix4(new float[] { s.x(), u.x(), -f.x(), 0, s.y(), u.y(), -f.y(), 0, s.z(), u.z(), -f.z(), 0, 0, 0, 0, 1 })
				.translate(eye.copy().mult(-1));
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
