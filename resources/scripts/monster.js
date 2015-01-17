//importClass(Packages.math.types.Vector3);


//var imported = new JavaImporter(Packages.math.types.Vector3);

function update(monster){
	//value += 20;
	//print("New value = " + value);
	//importPackage(Packages.math.types);	
	
	
	var movementDirection = new Packages.math.types.Vector3();
	
	 for (entity in monster.getCurrentTile().getContainedEntities()) {
		if (entity instanceof Packages.assets.entities.Player) {
			//movementDirection = MonsterScript.update(this, (Player) entity);
	 		//movementDirection.mult(0.01f);
	 		print(entity.getHealth());
	 	}
	 }
	//print("Key = " + monster.getCurrentTile().getKey().x  + ", "  + monster.getCurrentTile().getKey().y);

	// // Check that the tile rigid body is active, if not,
	// // suspend the model
	// if (getCurrentTile().getRigidBody() == null) {
	// 	rigidBody.setActivationState(0);
	// } else {
	// 	rigidBody.setActivationState(1);
	// 	rigidBody.activate();

	// 	if (rigidBody != null && rigidBody.getMotionState() != null) {
	// 		DefaultMotionState myMotionState = (DefaultMotionState) rigidBody.getMotionState();
	// 		physicsTransform.set(myMotionState.graphicsWorldTrans);

	// 	} else {
	// 		rigidBody.getWorldTransform(physicsTransform);
	// 	}

	// 	rigidBody.applyCentralImpulse(new Vector3f(movementDirection.x(), movementDirection.y(), movementDirection.z()));
	// 	// Force the body to hover on a plane. May cause
	// 	// z-oscillations; I don't fucking know, I'm not a
	// 	// physicist.
	// 	rigidBody.applyCentralImpulse(new Vector3f(0, 0, 0 - physicsTransform.origin.z));

	// 	// Update its rendering position
	// 	getPosition().setModelPos(new Vector3(physicsTransform.origin.x, physicsTransform.origin.y, physicsTransform.origin.z));
	// }
}