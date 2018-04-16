package rbadia.voidspace.main;

import java.awt.event.ActionEvent;



import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import rbadia.voidspace.model.*;
import rbadia.voidspace.sounds.SoundManager;


/**
 * Handles general game logic and status.
 */
public class GameLogic {
	private GameScreen gameScreen;
	private GameStatus status;
	private SoundManager soundMan;

	private MegaMan megaMan;
	private Met met;
	
	private List<Asteroid> asteroids;
	private List<Bullet> bullets;
	private List<Bullet> ebullets;

	private List<BigBullet> bigBullets;

	private Platform[] numPlatforms;
	private Floor[] floor;

	/**
	 * Create a new game logic handler
	 * @param gameScreen the game screen
	 */
	public GameLogic(GameScreen gameScreen){
		this.gameScreen = gameScreen;

		// initialize game status information
		status = new GameStatus();

		// initialize the sound manager
		soundMan = new SoundManager();

		// init some variables
		asteroids = new ArrayList<>();
		bullets = new ArrayList<>();
		ebullets = new ArrayList<>();
		
		bigBullets = new ArrayList<>();
	}

	/**
	 * Returns the game status
	 * @return the game status 
	 */
	public GameStatus getStatus() {
		return status;
	}

	public SoundManager getSoundMan() {
		return soundMan;
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}

	/**
	 * Prepare for a new game.
	 */
	public void newGame(){
		status.setGameStarting(true);

		// init game variables
		asteroids = new ArrayList<>();
		
		bullets = new ArrayList<>();
		ebullets = new ArrayList<>();
		bigBullets = new ArrayList<>();

		status.setShipsLeft(3);
		status.setLevel(1);
		status.setGameOver(false);
		status.setAsteroidsDestroyed(0);
		
// 		init the ship and the asteroid
		newMegaMan(gameScreen);
		newFloor(gameScreen, 9);
		newMet(gameScreen);

		newNumPlatforms(gameScreen, 8);

		newAsteroids(gameScreen);
		status.setIsNewAsteroids();
		
		// prepare game screen
		gameScreen.doNewGame();


		// delay to display "Get Ready" message for 1.5 seconds
		Timer timer = new Timer(1500, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameStarting(false);
				status.setGameStarted(true);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	/**
	 * Check game or level ending conditions.
	 */
	public void checkConditions(){
		// check game over conditions
		if(!status.isGameOver() && status.isGameStarted()){
			if(status.getShipsLeft() == 0){
				gameOver();
			}
		}
		if(!status.isGameWon()){
			if(gameScreen.getBoom() == 2)
				gameWon();
		}
	}

	/**
	 * Actions to take when the game is over.
	 */
	@SuppressWarnings("Duplicates")
    public void gameOver(){
		status.setGameStarted(false);
		status.setGameOver(true);
		gameScreen.doGameOver();

		// delay to display "Game Over" message for 3 seconds
		Timer timer = new Timer(5000, e -> status.setGameOver(false));
		timer.setRepeats(false);
		timer.start();

		//Change music back to menu screen music
		VoidSpaceMain.audioClip.close();
		VoidSpaceMain.audioFile = new File("audio/menuScreen.wav");
		try {
			VoidSpaceMain.audioStream = AudioSystem.getAudioInputStream(VoidSpaceMain.audioFile);
			VoidSpaceMain.audioClip.open(VoidSpaceMain.audioStream);
			VoidSpaceMain.audioClip.start();
			VoidSpaceMain.audioClip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}

    }

	/**
	 * Actions to take if game is won.
	 */

	//GAME LOOPS ON THE FIRST GAMESCREEN AND RESETS ALL VARIABLE COUNTERS
	@SuppressWarnings("Duplicates")
    public void gameWon(){
		//status.setGameStarted(false);  //SENDS TO MAIN SCREEN/ IF COMMENTED OUT LOOPS THE GAME
		status.setGameWon(true);
		gameScreen.doGameOver();

		// delay to display "Game Won" message for 3 seconds
		Timer timer = new Timer(3000, e -> status.setGameWon(false));
		timer.setRepeats(false);
		timer.start();

		//Change music back to menu screen music
		VoidSpaceMain.audioClip.close();
		VoidSpaceMain.audioFile = new File("audio/menuScreen.wav");
		try {
			VoidSpaceMain.audioStream = AudioSystem.getAudioInputStream(VoidSpaceMain.audioFile);
			VoidSpaceMain.audioClip.open(VoidSpaceMain.audioStream);
			VoidSpaceMain.audioClip.start();
			VoidSpaceMain.audioClip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}

    }


	/**
	 * Fire a bullet from ship.
	 */
	public void fireBullet(){
		Bullet bullet = new Bullet(megaMan);
		bullets.add(bullet);
		soundMan.playBulletSound();
	}

	public void enemyfireBullet(){
		System.out.println("Shhot");
		Bullet bullet = new Bullet(met);
		ebullets.add(bullet);
		soundMan.playBulletSound();
	}
	/**
	 * Fire the "Power Shot" bullet
	 */
	public void fireBigBullet(){
		BigBullet bigBullet = new BigBullet(megaMan);
		bigBullets.add(bigBullet);
		soundMan.playBulletSound();
	}

	/**
	 * Move a bullet once fired from the ship.
	 * @param bullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBullet(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() >= 0){
			bullet.translate(bullet.getSpeed(), 0);
			return false;
		}
		else{
			return true;
		}
	}

	public boolean moveEBullet(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() >= 0){
			bullet.translate(-bullet.getSpeed(), 0);
			return false;
		}
		else{
			return true;
		}
	}

	/** Move a "Power Shot" bullet once fired from the ship.
	 * @param bigBullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBigBullet(BigBullet bigBullet){
		if(bigBullet.getY() - bigBullet.getBigSpeed() >= 0){
			bigBullet.translate(bigBullet.getBigSpeed(), 0);
			return false;
		}
		else{
			return true;
		}
	}

	/**
	 * Create a new ship (and replace current one).
	 */
	public MegaMan newMegaMan(GameScreen screen){
		this.megaMan = new MegaMan(screen);
		return megaMan;
	}

	public Met newMet(GameScreen screen){
		this.met = new Met(screen);
		return met;
	}


	public Floor[] newFloor(GameScreen screen, int n){
		floor = new Floor[n];
		for(int i=0; i<n; i++){
			this.floor[i] = new Floor(screen, i);
		}

		return floor;
	}

	public Platform[] newNumPlatforms(GameScreen screen, int n){
		numPlatforms = new Platform[n];
		for(int i=0; i<n; i++){
			this.numPlatforms[i] = new Platform(screen, i);
		}
		return numPlatforms;

	}

	/**
	 * Create a new asteroid.
	 */
	public List<Asteroid> newAsteroids(GameScreen screen){
		for(int i = 0; i < 4; i++) {
			this.asteroids.add(i, new Asteroid(gameScreen));
		}
		return asteroids;
	}
	
	/**
	 * Returns the ship.
	 * @return the ship
	 */
	public MegaMan getMegaMan() {
		return megaMan;
	}

	public Met getMet() {
		return met;
	}


	public Floor[] getFloor(){
		return floor;	
	}

	public Platform[] getNumPlatforms(){
		return numPlatforms;
	}

	/**
	 * Returns the list of bullets.
	 * @return the list of bullets
	 */
	public List<Bullet> getBullets() {
		return bullets;
	}

	public List<Bullet> getEBullets() {
		return ebullets;
	}

	/**
	 * Returns the list of "Power Shot" bullets.
	 * @return the list of "Power Shot" bullets
	 */
	public List<BigBullet> getBigBullets(){
		return bigBullets;
	}

	public List<Asteroid> getAsteroids() {
		return asteroids;
	}
}
