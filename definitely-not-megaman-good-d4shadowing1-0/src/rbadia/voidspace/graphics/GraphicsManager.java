package rbadia.voidspace.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import rbadia.voidspace.main.Animation;
import rbadia.voidspace.model.*;
//import rbadia.voidspace.model.BigAsteroid;
//import rbadia.voidspace.model.Boss;
//import rbadia.voidspace.model.BulletBoss;
//import rbadia.voidspace.model.BulletBoss2;


/**
 * Manages and draws game graphics and images.
 */
public class GraphicsManager {
	private BufferedImage megaManImg;
	private BufferedImage megaFallRImg;
	private BufferedImage megaFireRImg;
	private BufferedImage floorImg;
	private BufferedImage platformImg;

	public static BufferedImage[] player_right;
	public static BufferedImage[] player_left;

	public static BufferedImage[] EnemyWalk;
	public static BufferedImage[] EnemyHide;

	private Animation animLeft, animRight, animenemyWalk, animenemyHide;


	private BufferedImage background;

	private BufferedImage bulletImg;
	private BufferedImage bigBulletImg;
	private BufferedImage asteroidImg;
	private BufferedImage asteroidExplosionImg;
	private BufferedImage megaManExplosionImg;
	//	private BufferedImage bossImg;
	//	private BufferedImage bossImg2;
	//	private BufferedImage bigAsteroidImg;
	private BufferedImage bigAsteroidExplosionImg;

	/**
	 * Creates a new graphics manager and loads the game images.
	 */
	public GraphicsManager(){
		// load images
		SpriteSheet RightSheet = new SpriteSheet(GraphicsManager.loadImage("/rbadia/voidspace/graphics/MegaRight.png"));
		SpriteSheet LeftSheet = new SpriteSheet(GraphicsManager.loadImage("/rbadia/voidspace/graphics/MegaLeft.png"));
		SpriteSheet enemySheet = new SpriteSheet(GraphicsManager.loadImage("/rbadia/voidspace/graphics/EnemySheet.png"));

		player_left = new BufferedImage[4];
		player_right = new BufferedImage[4];
		EnemyWalk = new BufferedImage[3];
		EnemyHide = new BufferedImage[3];


		try {

			player_left[0]=LeftSheet.crop(192,0,40,48);
			player_left[1]=LeftSheet.crop(122,0,48,48);
			player_left[2]=LeftSheet.crop(68,0,32,48);
			player_left[3]=LeftSheet.crop(0,0,42,48);

			player_right[0]=RightSheet.crop(0,0,40,48);
			player_right[1]=RightSheet.crop(62,0,48,48);
			player_right[2]=RightSheet.crop(132,0,32,48);
			player_right[3]=RightSheet.crop(190,0,42,48);

			EnemyWalk[0]=enemySheet.crop(44,0,18,19);
			EnemyWalk[1]=enemySheet.crop(66,0,20,19);
			EnemyWalk[2]=enemySheet.crop(91,0,19,19);

			EnemyHide[0]=enemySheet.crop(44,0,18,19);
			EnemyHide[1]=enemySheet.crop(22,0,18,19);
			EnemyHide[2]=enemySheet.crop(0,0,18,19);

			animLeft = new Animation(150,player_left);
			animRight = new Animation(150,player_right);
			animenemyHide = new Animation(150,EnemyHide);
			animenemyWalk = new Animation(150,EnemyWalk);



			this.megaManImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/megaMan3.png"));
			this.megaFallRImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/megaFallRight.png"));
			this.megaFireRImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/megaFireRight.png"));
			this.background = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/background.jpg"));
			this.floorImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/megaFloor.png"));
			this.platformImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/platform3.png"));
			//this.bossImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/boss1.png"));
			//this.bossImg2 = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/boss2.png"));
			this.asteroidImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/asteroid.png"));
			this.asteroidExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/asteroidExplosion.png"));
			//this.megaManExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/megaManExplosion.png"));
			this.bulletImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/bullet.png"));
			this.bigBulletImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/bigBullet.png"));
			//this.bigAsteroidImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/BigAsteroid.png"));
			//this.bigAsteroidExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/bigAsteroidExplosion.png"));

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The graphic files are either corrupt or missing.",
					"VoidSpace - Fatal Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(GraphicsManager.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public void tick() {
		animRight.tick();
		animLeft.tick();
		animenemyWalk.tick();
		animenemyHide.tick();
	}

	/**
	 * Draws a ship image to the specified graphics canvas.
	 * @param megaMan the ship to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */

	public void drawMegaMan (MegaMan megaMan, Graphics2D g2d, ImageObserver observer){
		g2d.drawImage(getCurrentAnimationFrame(megaMan, animLeft,animRight,player_left,player_right), megaMan.x, megaMan.y, observer);
	}
	public void drawMet (Met met, Graphics2D g2d, ImageObserver observer){
		g2d.drawImage(met.state, met.x, met.y, observer);
	}

	private BufferedImage getCurrentAnimationFrame(MegaMan megaman, Animation animLeft, Animation animRight, BufferedImage[] player_left, BufferedImage[] player_right) {
		if(megaman.getxMove() < 0){
			megaman.setLl(true);
			megaman.setLr(false);
			return animLeft.getCurrentFrame();
		}else if(megaman.getxMove() > 0) {
			megaman.setLl(false);
			megaman.setLr(true);
			return animRight.getCurrentFrame();
		}else{
			if(megaman.isLl()){
				return player_left[0];
			}else if(megaman.isLr()){
				return player_right[0];
			}else{
				return player_right[0];
			}
		}
	}

	private BufferedImage getCurrentMetAnimationFrame(Met met, Animation animLeft, Animation animRight, BufferedImage[] player_left, BufferedImage[] player_right) {
		if(met.getxMove() < 0){
			met.setLl(true);
			met.setLr(false);
			return animenemyWalk.getCurrentFrame();
		}else if(met.getxMove() > 0) {
			met.setLl(false);
			met.setLr(true);
			return animenemyWalk.getCurrentFrame();
		}else{
			return EnemyWalk[0];
		}
	}


	public void drawMegaFallR (MegaMan megaMan, Graphics2D g2d, ImageObserver observer){
		g2d.drawImage(megaFallRImg, megaMan.x, megaMan.y, observer);	
	}

	public void drawMegaFireR (MegaMan megaMan, Graphics2D g2d, ImageObserver observer){
		g2d.drawImage(megaFireRImg, megaMan.x, megaMan.y, observer);	
	}

	public void drawFloor (Floor floor, Graphics2D g2d, ImageObserver observer, int i){
			g2d.drawImage(floorImg, floor.x, floor.y, observer);				
	}
	public void drawPlatform(Platform platform, Graphics2D g2d, ImageObserver observer, int i){
			g2d.drawImage(platformImg, platform.x , platform.y, observer);	
	}
	
	public void drawPlatform2 (Platform platform, Graphics2D g2d, ImageObserver observer, int i){
		g2d.drawImage(platformImg, platform.x , platform.y, observer);	
}

	/**
	 * Draws a bullet image to the specified graphics canvas.
	 * @param bullet the bullet to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawBullet(Bullet bullet, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bulletImg, bullet.x, bullet.y, observer);
	}

	/**
	 * Draws a bullet image to the specified graphics canvas.
	 * @param bigBullet the bullet to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawBigBullet(BigBullet bigBullet, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bigBulletImg, bigBullet.x, bigBullet.y, observer);
	}

	/**
	 * Draws an asteroid image to the specified graphics canvas.
	 * @param asteroid the asteroid to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawAsteroid(Asteroid asteroid, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(asteroidImg, asteroid.x, asteroid.y, observer);
	}

	/**
	 * Draws a ship explosion image to the specified graphics canvas.
	 * @param megaManExplosion the bounding rectangle of the explosion
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawMegaManExplosion(Rectangle megaManExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(megaManExplosionImg, megaManExplosion.x, megaManExplosion.y, observer);
	}

	/**
	 * Draws an asteroid explosion image to the specified graphics canvas.
	 * @param asteroidExplosion the bounding rectangle of the explosion
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawAsteroidExplosion(Rectangle asteroidExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(asteroidExplosionImg, asteroidExplosion.x, asteroidExplosion.y, observer);
	}

	public void drawBigAsteroidExplosion(Rectangle bigAsteroidExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bigAsteroidExplosionImg, bigAsteroidExplosion.x, bigAsteroidExplosion.y, observer);
	}

	public BufferedImage getBackground() {
		return background;
	}



}
