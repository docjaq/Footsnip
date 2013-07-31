package assets.world;

import renderer.glmodels.GLModel;
import assets.Asset;
import assets.world.datastructures.HashmapKey;

/************************
 * @author docjaq This is the interesting addition. I came up with the idea that
 *         instead of 'rooms', 'maps', 'worlds' etc, we just have one world
 *         container (which doesn't subclass anything, as far as I can tell yet,
 *         but contains a collection of Tiles. All tiles are the same size, etc,
 *         and then tesselate together (indefinitely). I was thinking
 *         essentially like a Minecraft block, but bigger, and more like a bit
 *         of flat ground. Then we can, subclass this, to create, e.g.
 *         'ClosedBoxTile' as our debug case, then things like 'forest tile',
 *         'city tile', etc. That sort of thing.
 */

public abstract class AbstractTile implements Asset {

	protected GLModel model;
	protected HashmapKey key;

	public AbstractTile() {
	}

	public AbstractTile(GLModel model) {
		this.model = model;
	}

	public GLModel getModel() {
		return model;
	}

	public abstract float getSize();

	public void setModel(GLModel model) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		this.model = model;
	}

	public void destroy() {
		model.cleanUp();
	}

	public HashmapKey getKey() {
		return key;
	}

	public void setKey(HashmapKey key) {
		this.key = key;
	}
}
