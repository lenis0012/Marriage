package me.lenis0012.mr.children;

import me.lenis0012.mr.children.thinking.Brain;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.World;
import net.minecraft.server.v1_4_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class ChildControler implements Child {
	private Brain brain;
	private EntityLiving cbEntity;
	private LivingEntity entity;
	private Player owner;
	private ChildManager manager;
	private boolean spawned = false, staying = false, baby = true;
	public Location loc;
	
	public ChildControler(int id, Player owner) {
		this.brain = new Brain(this);
		this.manager = ChildManager.getInstance();
		this.owner = owner;
	}
	
	public void spawn(Location loc) {
		if(this.isSpawned() || !loc.getChunk().isLoaded())
			return;
		
		try {
			WorldServer ws = ((CraftWorld)loc.getWorld()).getHandle();
			this.cbEntity = (EntityLiving)EntityChild.class.getConstructor(World.class, Child.class).newInstance(ws, this);
			cbEntity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			ws.addEntity(this.cbEntity);
			this.entity = (LivingEntity)this.cbEntity.getBukkitEntity();
			this.spawned = true;
			this.loc = loc;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deSpawn() {
		this.spawned = false;
		this.getBukkitEnitity().remove();
	}

	@Override
	public boolean isSpawned() {
		return this.spawned;
	}

	@Override
	public boolean isStaying() {
		return this.staying;
	}
	
	@Override
	public void setStaying(boolean value) {
		this.staying = value;
	}

	@Override
	public Brain getBrain() {
		return brain;
	}

	@Override
	public LivingEntity getBukkitEnitity() {
		return this.entity;
	}

	@Override
	public EntityLiving getHandle() {
		return this.cbEntity;
	}

	@Override
	public float getSpeed() {
		return 0.25F;
	}
	
	@Override
	public void setBaby(boolean value) {
		this.baby = value;
	}
	
	@Override
	public boolean isBaby() {
		return this.baby;
	}
	
	public Location getLocation() {
		return this.loc;
	}
	
	@Override
	public Player getOwner() {
		return this.owner;
	}
}
