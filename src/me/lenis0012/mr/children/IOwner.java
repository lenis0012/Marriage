package me.lenis0012.mr.children;

import me.lenis0012.mr.SimpleMPlayer;
import net.minecraft.server.v1_4_R1.EntityPlayer;

import org.bukkit.craftbukkit.v1_4_R1.CraftServer;

public class IOwner extends SimpleMPlayer implements Owner {
	private ChildManager manager;

	public IOwner(CraftServer server, EntityPlayer entity) {
		super(server, entity);
		this.manager = ChildManager.getInstance();
	}

	@Override
	public boolean hasChild() {
		return this.isMarried() && ChildConfiguration.hasChild(this);
	}

	@Override
	public Child getChild() {
		String name = getName();
		if(this.hasChild()) {
			String partner = this.getPartner();
			if(manager.parents.containsKey(name))
				return manager.parents.get(name);
			else if(manager.parents.containsKey(partner))
				return manager.parents.get(partner);
			else
				return manager.loadChild(ChildConfiguration.getChild(this));
		}
		return null;
	}

	@Override
	public void NotifyChildDeath() {}
}
