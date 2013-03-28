package me.lenis0012.mr.children.thinking;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;

import me.lenis0012.mr.children.Child;
import me.lenis0012.mr.util.PositionUtil;
import net.minecraft.server.v1_5_R2.EntityVillager;
import net.minecraft.server.v1_5_R2.Vec3D;

public class PlayCell implements BrainCell {
	private EntityVillager villager;
	private EntityVillager friend = null;
	private Child child;
	private boolean cc = true;
	private int count = 0;
	
	public PlayCell(Child child) {
		this.child = child;
		this.villager = (EntityVillager) child.getHandle();
	}

	@Override
	public void onUpdate() {
		if(isSafe()) {
			if(villager.e(friend) > 4)
				PositionUtil.move(villager, friend, child.getSpeed());
			else if(villager.getNavigation().f()) {
				Vec3D vec1 = PositionUtil.getRandom(villager, 16, 3);
				Vec3D vec2 = PositionUtil.getRandom(friend, 16, 3);
				
				if(vec1 != null)
					PositionUtil.move(villager, new Location(child.getBukkitEnitity().getWorld(), vec1.c, vec1.d, vec1.e), child.getSpeed());
				if(vec2 != null)
					PositionUtil.move(friend, new Location(child.getBukkitEnitity().getWorld(), vec2.c, vec2.d, vec2.e), child.getSpeed());
			}
		} else {
			if(count >= 20)
				check();
			else
				count++;
		}
	}

	@Override
	public void onRemove() {
		villager.f(false);
	}

	@Override
	public void onCreate() {
		this.check();
	}
	
	@SuppressWarnings("rawtypes")
	public void check() {
		if(!child.isSpawned() || !villager.isBaby())
			cc = false;
		
		List villagers = villager.world.a(EntityVillager.class, villager.boundingBox.grow(6, 3, 6));
		Iterator it = villagers.iterator();
		double distance = Double.MAX_VALUE;
		
		while(it.hasNext()) {
			EntityVillager v = (EntityVillager)it.next();
			
			if(v != villager && !v.p() && v.isBaby()) {
				double d = v.e(villager);
				if(d <= distance) {
					distance = d;
					friend = v;
				}
			}
		}
	}
	
	public boolean isSafe() {
		return friend != null;
	}

	@Override
	public String getType() {
		return "Play";
	}

	@Override
	public boolean canContinue() {
		return cc;
	}
}
