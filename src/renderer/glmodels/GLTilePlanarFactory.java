package renderer.glmodels;

import assets.world.AbstractTile;

public class GLTilePlanarFactory implements GLTileFactory {

	@Override
	public GLTilePlane create(AbstractTile tile) {
		return new GLTilePlane();
	}

}
