package me.lenis0012.mr.child;

import me.lenis0012.mr.child.thinking.Brain;
import net.minecraft.server.v1_5_R2.EntityLiving;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface Child {
	
	/**
	 * Is the child staying?
	 * 
	 * @return Staying
	 */
	public boolean isStaying();
	
	/**
	 * Get the bukkit apic class from the mob
	 * 
	 * @return Bukkit api class
	 */
	public LivingEntity getBukkitEntity();
	
	/**
	 * Get the handle of the child
	 * 
	 * @return Entity handle
	 */
	public EntityLiving getHandle();
	
	/**
	 * Spawn the child
	 * 
	 * @param loc Location to be spawned
	 * @param addCells Add default cells
	 */
	public void spawn(Location loc, boolean addCells);
	
	/**
	 * Despawn the child
	 * 
	 * @param removeCells Shall we remove the brain cells?
	 */
	public void despawn(boolean removeCells);
	
	/**
	 * Get the parents from the child
	 * 
	 * @return Parents
	 */
	public ParentPair getParents();
	
	/**
	 * Teleport the child
	 * 
	 * @param loc Location
	 */
	public void teleport(Location loc);
	
	/**
	 * Move the child to an entity
	 * 
	 * @param target Entity target
	 * @return Move succesfull?
	 */
	public boolean move(LivingEntity target);
	
	/**
	 * Move the child to a location
	 * 
	 * @param loc Location
	 * @return Move succesfull?
	 */
	public boolean move(Location loc);
	
	/**
	 * Get the childs brain
	 * 
	 * @return Brain
	 */
	public Brain getBrain();
	
	/**
	 * Check if the child is spawned
	 * 
	 * @return Child spawned?
	 */
	public boolean isSpawned();
}