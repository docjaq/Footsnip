package renderer.glmodels;

import assets.world.AbstractTile;

public interface GLTileFactory {
	public GLModel create(AbstractTile tile);
}
