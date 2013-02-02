package me.lenis0012.mr.children;

import me.lenis0012.mr.children.thinking.Brain;
import net.minecraft.server.v1_4_R1.EntityLiving;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface Child {
	/**
	 * Check if the child is spawned
	 * 
	 * @return child spawned?
	 */
	public boolean isSpawned();
	
	/**
	 * Check if the child is staying
	 * 
	 * @return child staying?
	 */
	public boolean isStaying();
	
	/**
	 * Get the childs mind
	 * 
	 * @return childs mind
	 */
	public Brain getBrain();
	
	/**
	 * Getthe id of the child
	 * 
	 * @return id
	 */
	public int getID();
	
	/**
	 * Get the chidld as an enity
	 * 
	 * @return entity
	 */
	public LivingEntity getBukkitEnitity();
	
	/**
	 * Returns the CraftBukkit entity of the child
	 * ready to handle
	 * 
	 * @return handle
	 */
	public EntityLiving getHandle();
	
	/**
	 * Get the childs speed
	 * 
	 * @return speed
	 */
	public float getSpeed();
	
	/**
	 * Get the parent of the child
	 * 
	 * @return owner
	 */
	public String getParent();
	
	/**
	 * Get the childs location
	 * 
	 * @return location
	 */
	public Location getLocation();
	
	/**
	 * Check if the child is a baby
	 * 
	 * @return baby?
	 */
	public boolean isBaby();
	
	/**
	 * Spawn the child
	 * 
	 * @param loc location to spawn the child
	 */
	public void spawn(Location loc, boolean addCells);
	
	/**
	 * Set if the entity should stay
	 * 
	 * @param value	entity should stay?
	 */
	public void setStaying(boolean value);
	
	/**
	 * Set if the child is a baby or not
	 * 
	 * @param value baby or not?
	 */
	public void setBaby(boolean value);
	
	/**
	 * Despawn the child
	 */
	public void deSpawn(boolean removeCells);
	
	/**
	 * move the child
	 * 
	 * @param target target to move to
	 */
	public void move(LivingEntity target);
	
	/**
	 * Move the child
	 * 
	 * @param loc location to move to
	 */
	public void move(Location loc);
	
	/**
	 * save the child to the childs.yml
	 */
	public void save();
}
