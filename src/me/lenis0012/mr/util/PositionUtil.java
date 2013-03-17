package me.lenis0012.mr.util;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import me.lenis0012.mr.children.thinking.CustomPath;
import net.minecraft.server.v1_5_R1.EntityCreature;
import net.minecraft.server.v1_5_R1.EntityLiving;
import net.minecraft.server.v1_5_R1.MathHelper;
import net.minecraft.server.v1_5_R1.PathEntity;
import net.minecraft.server.v1_5_R1.Vec3D;

public class PositionUtil {

	public static Vec3D getRandom(EntityLiving entity, int x, int z) {
		return getVector(entity, x, z, (Vec3D) null);
	}

	public static EntityLiving getEntity(LivingEntity entity) {
		return ((CraftLivingEntity)entity).getHandle();
	}

	public static boolean move(EntityLiving entity, Location loc, float speed) {
		int x = MathHelper.floor(loc.getX());
		int y = (int) loc.getY();
		int z = MathHelper.floor(loc.getZ());

		if(!entity.getNavigation().a(x, y, z, speed)) {
			PathEntity path = CustomPath.createPath(entity, x, y, z);
			return moveTo(entity, path, speed);
		}
		return true;
	}

	public static boolean move(EntityLiving entity, EntityLiving to, float speed) {
		if(!entity.getNavigation().a(to, speed)) {
			PathEntity path = CustomPath.createPath(entity, to);
			return moveTo(entity, path, speed);
		}
		return true;
	}

	private static boolean moveTo(EntityLiving entity, PathEntity path, float speed) {
		if(entity instanceof EntityCreature)
			((EntityCreature)entity).setPathEntity(path);

		return entity.getNavigation().a(path, speed);
	}

	private static Vec3D getVector(EntityLiving entity, int x, int z, Vec3D vec) {
		Random random = entity.aE();
        boolean flag = false;
        int k = 0;
        int l = 0;
        int i1 = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entity.aP())
        {
            double d0 = (double) (entity.aM().e(MathHelper.floor(entity.locX), MathHelper.floor(entity.locY), MathHelper.floor(entity.locZ)) + 4.0F);
            double d1 = (double) (entity.aN() + (float) x);

            flag1 = d0 < d1 * d1;
        }
        else
            flag1 = false;

        for (int j1 = 0; j1 < 10; ++j1)
        {
            int k1 = random.nextInt(2 * x) - x;
            int l1 = random.nextInt(2 * z) - z;
            int i2 = random.nextInt(2 * x) - x;

            if (vec == null || (double) k1 * vec.c + (double) i2 * vec.c >= 0.0D)
            {
                k1 += MathHelper.floor(entity.locX);
                l1 += MathHelper.floor(entity.locY);
                i2 += MathHelper.floor(entity.locZ);
                if (!flag1 || entity.d(k1, l1, i2))
                {
                    float f1 = 0.5F - entity.world.q(k1, l1, i2);

                    if (f1 > f)
                    {
                        f = f1;
                        k = k1;
                        l = l1;
                        i1 = i2;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
            return entity.world.getVec3DPool().create((double) k, (double) l, (double) i1);
        else
            return null;
	}
}