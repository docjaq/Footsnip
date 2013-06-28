package assets.world;

import renderer.glmodels.GLModel;
import assets.Asset;

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

public class AbstractTile implements Asset {

	protected GLModel model;

	// 00 10 20 /**/
	// 01 ** 21 /**/
	// 02 12 22 /**/

	private AbstractTile tile00, tile10, tile20, tile01, tile21, tile02, tile12, tile22;

	public AbstractTile() {
	}

	public AbstractTile(GLModel model) {
		this.model = model;
	}

	public GLModel getModel() {
		return model;
	}

	public void setModel(GLModel model) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		this.model = model;
	}

	public void destroy() {
		model.cleanUp();
	}

	public AbstractTile getTile00() {
		return tile00;
	}

	public void setTile00(AbstractTile tile00) {
		this.tile00 = tile00;
	}

	public AbstractTile getTile10() {
		return tile10;
	}

	public void setTile10(AbstractTile tile10) {
		this.tile10 = tile10;
	}

	public AbstractTile getTile20() {
		return tile20;
	}

	public void setTile20(AbstractTile tile20) {
		this.tile20 = tile20;
	}

	public AbstractTile getTile01() {
		return tile01;
	}

	public void setTile01(AbstractTile tile01) {
		this.tile01 = tile01;
	}

	public AbstractTile getTile21() {
		return tile21;
	}

	public void setTile21(AbstractTile tile21) {
		this.tile21 = tile21;
	}

	public AbstractTile getTile02() {
		return tile02;
	}

	public void setTile02(AbstractTile tile02) {
		this.tile02 = tile02;
	}

	public AbstractTile getTile12() {
		return tile12;
	}

	public void setTile12(AbstractTile tile12) {
		this.tile12 = tile12;
	}

	public AbstractTile getTile22() {
		return tile22;
	}

	public void setTile22(AbstractTile tile22) {
		this.tile22 = tile22;
	}
}
