package assets;

/************************
 * @author docjaq
 * This is the interesting addition. I came up with the idea that instead of 'rooms',
 * 'maps', 'worlds' etc, we just have one world container (which doesn't subclass 
 * anything, as far as I can tell yet, but contains a collection of Tiles. All tiles are 
 * the same size, etc, and then tesselate together (indefinitely). I was thinking 
 * essentially like a Minecraft block, but bigger, and more like a bit of flat ground. 
 * Then we can, subclass this, to create, e.g. 'ClosedBoxTile' as our debug case, then 
 * things like 'forest tile', 'city tile', etc. That sort of thing.   
 */

public class AbstractTile implements Asset{

	public void draw(){
		
	}
}
