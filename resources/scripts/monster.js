function update(monster){
	var movementDirection = new Packages.math.types.Vector3();

    for (i = 0; i < monster.getCurrentTile().getContainedEntities().size(); i++) {
        var entity = monster.getCurrentTile().getContainedEntities().get(i);

        if (entity instanceof Packages.assets.entities.Player) {
            movementDirection = computeVector(monster, entity);
            movementDirection.mult(0.005);
        }
    }

	 var tileRigidBody = monster.getCurrentTile().getRigidBody()
	 var rigidBody = monster.getRigidBody();
	 if (tileRigidBody == null) {
	 	rigidBody.setActivationState(0);
	 } else {
	 	rigidBody.setActivationState(1);
	 	rigidBody.activate();

        var physicsTransform = monster.getPhysicsTransform();
		if (rigidBody.getMotionState() != null) {
			var myMotionState = rigidBody.getMotionState();
            physicsTransform.set(myMotionState.graphicsWorldTrans);

		} else {
			rigidBody.getWorldTransform(physicsTransform);
		}
        rigidBody.applyCentralImpulse(new Packages.javax.vecmath.Vector3f(movementDirection.x(), movementDirection.y(), movementDirection.z()));
       	rigidBody.applyCentralImpulse(new Packages.javax.vecmath.Vector3f(0, 0, 0 - physicsTransform.origin.z));
   		monster.getPosition().setModelPos(new Packages.math.types.Vector3(physicsTransform.origin.x, physicsTransform.origin.y, physicsTransform.origin.z));
	}
}

function computeVector(monster, player) {

    var monsterPos = monster.getPosition();
    var playerPos = player.getPosition();
    var direction = Packages.math.types.Vector3.sub(playerPos.getModelPos(), monsterPos.getModelPos());

    return direction.normalize().mult(0.1);

return 1;
}