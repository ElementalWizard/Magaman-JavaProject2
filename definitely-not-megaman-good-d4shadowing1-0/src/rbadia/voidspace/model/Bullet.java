package rbadia.voidspace.model;
import java.awt.Rectangle;

/**
 * Represents a bullet fired by a ship.
 */
public class Bullet extends Rectangle {
	private static final long serialVersionUID = 1L;
	
	private int bulletWidth = 8;
	private int bulletHeight = 8;
	private int speed = 12;

	
	/**
	 * Creates a new bullet above the ship, centered on it
	 * @param megaMan
	 */
	public Bullet(MegaMan megaMan) {
        this.setLocation(megaMan.x + megaMan.width - bulletWidth/2,
                megaMan.y + megaMan.width/2 - bulletHeight +2);
        this.setSize(bulletWidth, bulletHeight);
    }
    public Bullet(Met met) {
        this.setLocation(met.x - met.width + bulletWidth/2,
                met.y + met.width/2 - bulletHeight +2);
        this.setSize(bulletWidth, bulletHeight);
    }
	

	/**
	 * Return the bullet's speed.
	 * @return the bullet's speed.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Set the bullet's speed
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public int getBulletWidth(){
		return bulletWidth;
	}
}
