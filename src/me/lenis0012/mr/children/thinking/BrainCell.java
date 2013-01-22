package me.lenis0012.mr.children.thinking;

public interface BrainCell {
	public void onUpdate();
	public void onRemove();
	public void onCreate();
	public String getType();
}
