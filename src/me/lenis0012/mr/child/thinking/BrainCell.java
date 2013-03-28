package me.lenis0012.mr.child.thinking;

public interface BrainCell {
	
	/**
	 * Called when creating the cell
	 */
	public void onCreate();
	
	/**
	 * Called when updating the cell (main thread)
	 */
	public void onUpdate();
	
	/**
	 * Called when removing the cell
	 */
	public void onRemove();
	
	/**
	 * Can the cell execute?
	 * 
	 * @return Execute
	 */
	public boolean canExecute();
	
	/**
	 * Get the cell type
	 * 
	 * @return Type
	 */
	public String getType();
}