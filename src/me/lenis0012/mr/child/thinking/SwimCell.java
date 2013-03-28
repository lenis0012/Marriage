package me.lenis0012.mr.child.thinking;

import net.minecraft.server.v1_5_R2.EntityLiving;
import me.lenis0012.mr.child.Child;

public class SwimCell implements BrainCell {
	private Child child;
	private EntityLiving entity;
	
	public SwimCell(Child child) {
		this.child = child;
		this.entity = child.getHandle();
	}
	
	@Override
	public void onCreate() {}

	@Override
	public void onUpdate() {
		if(entity.aE().nextFloat() < 0.8F) {
			entity.getControllerJump().a();
		}
	}

	@Override
	public void onRemove() {}

	@Override
	public boolean canExecute() {
		return child.isSpawned() && (entity.G() || entity.I());
	}

	@Override
	public String getType() {
		return "Swim";
	}
}