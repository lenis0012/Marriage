package me.lenis0012.mr.child.thinking;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.EntityLiving;
import net.minecraft.server.v1_5_R2.EntityVillager;
import net.minecraft.server.v1_5_R2.Vec3D;
import me.lenis0012.mr.child.Child;
import me.lenis0012.mr.util.PositionUtil;

public class PlayCell implements BrainCell {
	private Child child;
	private EntityLiving entity;
	private EntityLiving friend;
	private long playTicks;
	
	public PlayCell(Child child, long playTicks) {
		this.child = child;
		this.entity = child.getHandle();
		this.playTicks = playTicks;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() {
		if(!child.isSpawned()) {
			child.getBrain().removeCell(this);
			return;
		}
		
		List<Entity> villagers = (List<Entity>) entity.world.a(EntityVillager.class, entity.boundingBox.grow(6, 3, 6));
		double distance = Double.MAX_VALUE;
		Iterator<Entity> it = villagers.iterator();
		
		while(it.hasNext()) {
			EntityVillager villager = (EntityVillager) it.next();
			if(villager != this.entity && !villager.p() && villager.getAge() < 0) {
				double dis = villager.e(this.entity);
				if(dis <= distance) {
					distance = dis;
					this.friend = villager;
				}
			}
		}
		
		if(this.friend == null) {
			//We could not find a buddy ;(
			Vec3D vec = PositionUtil.getRandom(this.entity, 16, 3);
			
			if(vec == null) {
				//We could not even get a movement D:
				child.getBrain().removeCell(this);
				return;
			}
		}
	}

	@Override
	public void onUpdate() {
		if(this.playTicks <= 0) {
			child.getBrain().removeCell(this);
			return;
		} else
			this.playTicks--;
		
		if(this.friend != null) {
			if(this.entity.e(this.friend) > 4)
				this.child.move((LivingEntity) this.friend.getBukkitEntity());
		} else if(this.entity.getNavigation().f()) {
			Vec3D vec = PositionUtil.getRandom(this.entity, 16, 13);
			
			if(vec != null) {
				World world = this.entity.getBukkitEntity().getWorld();
				this.child.move(new Location(world, vec.c, vec.d, vec.e));
			}
		}
	}

	@Override
	public void onRemove() {}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public String getType() {
		return "Play";
	}
}