package physics;

import com.bulletphysics.ContactProcessedCallback;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;

public class DefaultContactProcessedCallback extends ContactProcessedCallback {

	@Override
	public boolean contactProcessed(ManifoldPoint cp, Object body0, Object body1) {
		System.err.println("Contact Processed!");
		return false;
	}
}
