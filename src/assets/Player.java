package assets;

import renderer.glmodels.GLModel;

public class Player extends Character {

	private int age;
	private float[] color;

	public Player(GLModel model, String name, int age, float[] color) {
		super(model, name);
		this.age = age;
		this.color = color;
	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

}
