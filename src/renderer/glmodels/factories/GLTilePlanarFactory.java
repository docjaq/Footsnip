package renderer.glmodels.factories;

import renderer.glmodels.GLTilePlane;
import assets.world.AbstractTile;

public class GLTilePlanarFactory implements GLTileFactory {

	@Override
	public GLTilePlane create(AbstractTile tile) {
		return new GLTilePlane();
	}

}
