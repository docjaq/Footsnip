package renderer.glmodels.factories;

import renderer.glmodels.GLModel;
import assets.world.AbstractTile;

public interface GLTileFactory {
	public GLModel create(AbstractTile tile);
}
