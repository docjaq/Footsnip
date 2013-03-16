package assets;

import renderer.glmodels.GLModel;

public class Character extends AbstractEntity {

	private String name;

	public Character(String name) {
		this.name = name;
	}

	public Character(GLModel model, String name) {
		super(model);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
