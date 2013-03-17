package me.lenis0012.mr.children.thinking;

import java.util.Random;

import org.bukkit.Location;

import net.minecraft.server.v1_5_R1.EntityCreature;
import net.minecraft.server.v1_5_R1.EntityLiving;
import net.minecraft.server.v1_5_R1.MathHelper;
import net.minecraft.server.v1_5_R1.Vec3D;
import me.lenis0012.mr.children.Child;
import me.lenis0012.mr.util.PositionUtil;

public class AvoidSunCell implements BrainCell {
	private Child child;
	private EntityLiving entity;
	private double x, y, z;
	private long lastCheck = System.currentTimeMillis();
	private boolean cc = true;
	
	public AvoidSunCell(Child child) {
		this.child = child;
	}

	@Override
	public void onUpdate() {
		if(!this.isExploring()) {
			cc = false;
			return;
		}
		
		long t = System.currentTimeMillis() - lastCheck;
		if(!this.isInShadow() && t > 5000) {
			if(this.isOnLand() && this.hasShadow()) {
				PositionUtil.move(entity, new Location(child.getBukkitEnitity().getWorld(), x, y ,z), child.getSpeed());
			}
		}
	}

	@Override
	public void onRemove() {}

	@Override
	public void onCreate() {
		if(!this.isExploring()) {
			cc = false;
			return;
		}
		
		if(this.isOnLand() && this.hasShadow()) {
			PositionUtil.move(entity, new Location(child.getBukkitEnitity().getWorld(), x, y ,z), child.getSpeed());
		}
	}
	
	private boolean isExploring() {
		for(BrainCell cell : child.getBrain().getCells()) {
			if(cell.getType().equalsIgnoreCase("follow"))
				return false;
		}
		
		if(child.isStaying())
			return false;
		
		return true;
	}
	
	private boolean isInShadow() {
		int x = MathHelper.floor(entity.locX);
		int y = (int)entity.locY;
		int z = MathHelper.floor(entity.locZ);
		
		if(entity instanceof EntityCreature) {
			EntityCreature ec = (EntityCreature) entity;
			return !entity.world.l(x, y, z) && ec.a(x, y, z) < 0.0F;
		} else {
			return !entity.world.l(x, y, z) && (0.5F - entity.world.q(x, y, z)) < 0.0F;
		}
	}
	
	private boolean hasShadow() {
		Vec3D shadow = this.searchShadow();
		
		if(shadow == null)
			return false;
		
		x = shadow.c;
		y = shadow.d;
		z = shadow.e;
		return true;
	}
	
	public Vec3D searchShadow() {
		try {
			Random random = entity.aE();
			for(int i = 0; i < 10; i++) {
				int x = MathHelper.floor(entity.locX + random.nextInt(20) - 10);
				int y = MathHelper.floor(entity.boundingBox.b + random.nextInt(6) - 3);
				int z = MathHelper.floor(entity.locZ + random.nextInt(20) - 10);
				
				if(entity instanceof EntityCreature) {
					EntityCreature ec = (EntityCreature) entity;
					if(!entity.world.l(x, y, z) && ec.a(x, y, z) < 0.0F)
						return entity.world.getVec3DPool().create(x, y, z);
				} else {
					if(!entity.world.l(x, y, z) && (0.5F - entity.world.q(x, y, z)) < 0.0F)
						return entity.world.getVec3DPool().create(x, y, z);
				}
			}
		} catch(Exception e) {
			return null;
		}
		
		return null;
	}
	
	private boolean isOnLand() {
		return !entity.getNavigation().f();
	}

	@Override
	public String getType() {
		return "AvoidSun";
	}

	@Override
	public boolean canContinue() {
		return cc;
	}
}
