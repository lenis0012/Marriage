package me.lenis0012.mr.child;

import me.lenis0012.mr.child.thinking.Brain;
import me.lenis0012.mr.util.ReflectionUtil;
import net.minecraft.server.v1_5_R2.EntityLiving;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class ChildControler implements Child {
	private EntityLiving handle;
	private ParentPair parents;
	private Brain brain;
	private boolean staying;
	private boolean spawned;
	
	public ChildControler(ParentPair parents) {
		this.parents = parents;
		this.brain = new Brain();
	}
	
	@Override
	public boolean isStaying() {
		return this.staying;
	}

	@Override
	public LivingEntity getBukkitEntity() {
		return (LivingEntity) getHandle().getBukkitEntity();
	}

	@Override
	public EntityLiving getHandle() {
		return this.handle;
	}

	@Override
	public void spawn(Location loc, boolean addCells) {
		ReflectionUtil.registerEntityType(EntityChild.class, "Child", EntityType.VILLAGER.getTypeId());
		this.spawned = true;
	}

	@Override
	public void despawn(boolean removeCells) {
		this.spawned = false;
	}

	@Override
	public ParentPair getParents() {
		return this.parents;
	}

	@Override
	public void teleport(Location loc) {
		this.getBukkitEntity().teleport(loc);
	}

	@Override
	public boolean move(LivingEntity target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean move(Location loc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Brain getBrain() {
		return this.brain;
	}

	@Override
	public boolean isSpawned() {
		return this.spawned;
	}
}