package collision;

public interface Collidable {
	public void collidedWith(Collidable subject);

	public boolean readyForCollisionDetection();
}
