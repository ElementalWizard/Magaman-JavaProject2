package rbadia.voidspace.main;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.*;
import rbadia.voidspace.sounds.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * Main game screen. Handles all game graphics updates and some of the game logic.
 */
public class GameScreen extends BaseScreen{
    private static final long serialVersionUID = 1L;

    private BufferedImage backBuffer;
    private Graphics2D g2d;

    private static final int NEW_SHIP_DELAY = 500;
    private static final int NEW_ASTEROID_DELAY = 500;
   
    //	private long lastShipTime;
    private long lastAsteroidTime;

    private Rectangle asteroidExplosion;
    //	private Rectangle bigAsteroidExplosion;
    //	private Rectangle shipExplosion;
    //	private Rectangle bossExplosion;

    private JLabel shipsValueLabel;
    private JLabel destroyedValueLabel;
    private JLabel levelValueLabel;

    private Random rand;

    private Font originalFont;
    private Font bigFont;
    private Font biggestFont;

    public static boolean isMetSpawned = false;

    private GameStatus status;
    private SoundManager soundMan;
    public GraphicsManager graphicsMan;
    public GameLogic gameLogic;

    private int boom=0;
    private int level=1;


    /**
     * This method initializes
     *
     */
    public GameScreen() {
        super();
        // initialize random number generator
        rand = new Random();

        initialize();

        // init graphics manager
        graphicsMan = new GraphicsManager();

        // init back buffer image
        backBuffer = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
        g2d = backBuffer.createGraphics();
    }

    /**
     * Initialization method (for VE compatibility).
     */
    protected void initialize() {
        // set panel properties
        this.setSize(new Dimension(500, 400));
        this.setPreferredSize(new Dimension(500, 400));
        this.setBackground(Color.BLACK);
    }

    /**
     * Update the game screen.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw current backbuffer to the actual game screen
        g.drawImage(backBuffer, 0, 0, this);
    }

    /**
     * Update the game screen's backbuffer image.
     */
    public void updateScreen(){
        graphicsMan.tick();

        MegaMan megaMan = gameLogic.getMegaMan();
        Met met = gameLogic.getMet();
        Floor[] floor = gameLogic.getFloor();
        Platform[] numPlatforms = gameLogic.getNumPlatforms();
        List<Bullet> bullets = gameLogic.getBullets();
        List<Bullet> ebullets = gameLogic.getEBullets();
        List<Asteroid> asteroids = gameLogic.getAsteroids();
        List<BigBullet> bigBullets = gameLogic.getBigBullets();
        
        // set orignal font - for later use
        if(this.originalFont == null){
            this.originalFont = g2d.getFont();
            this.bigFont = originalFont;
        }

        // erase screen
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, getSize().width, getSize().height);

        // draw 50 random stars
        drawStars(50);

        // if the game is starting, draw "Get Ready" message
        if(status.isGameStarting()){
            drawGetReady();
            return;
        }

        // if the game is over, draw the "Game Over" message
        if(status.isGameOver()){
            // draw the message
            drawGameOver();

            long currentTime = System.currentTimeMillis();
            // draw the explosions until their time passes
            if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
                graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
            }
            //			if((currentTime - lastShipTime) < NEW_SHIP_DELAY){
            //				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
            //			}
            return;
        }

        //if the game is won, draw the "You Win!!!" message
        if(status.isGameWon()){
            // draw the message
            drawYouWin();

            long currentTime = System.currentTimeMillis();
            // draw the explosions until their time passes
            if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
                graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
            }
            return;
        }

        // the game has not started yet
        if(!status.isGameStarted()){
            // draw game title screen
            initialMessage();
            return;
        }

        //draw Floor



        //sets the background by level for choice if wanted
        if(status.getLevel()==1){
            g2d.drawImage(graphicsMan.getBackground(),0,0,getWidth(),getHeight(),null);
        }else if (status.getLevel()==2){
            g2d.drawImage(graphicsMan.getBackground(),0,0,getWidth(),getHeight(),null);
        }else if (status.getLevel()==3){
            g2d.drawImage(graphicsMan.getBackground(),0,0,getWidth(),getHeight(),null);
        }else if (status.getLevel()==4){
            g2d.drawImage(graphicsMan.getBackground(),0,0,getWidth(),getHeight(),null);
        }

        //draw Platform
        for(int i=0; i < gameLogic.getNumPlatforms().length; i++){
            graphicsMan.drawPlatform(numPlatforms[i], g2d, this, i);
        }

        //draws floor
        for(int i=0; i<gameLogic.getFloor().length; i++){
            graphicsMan.drawFloor(floor[i], g2d, this, i);
        }

        //draw MegaMan
        if(!status.isNewMegaMan()){
            if((Gravity()) || ((Gravity()) && (Fire() || Fire2()))){
                graphicsMan.drawMegaFallR(megaMan, g2d, this);
            }
        }

        if((Fire() || Fire2()) && (!Gravity())){
            graphicsMan.drawMegaFireR(megaMan, g2d, this);
        }

        if((!Gravity()) && (!Fire()) && (!Fire2())){
            graphicsMan.drawMegaMan(megaMan, g2d, this);
        }

        if(isMetSpawned){
            met.tick();
            graphicsMan.drawMet(met,g2d,null);

        }

        switch(status.getLevel()) {
        
        	case 1:
        		spawnMeteor(asteroids, bullets, floor, megaMan, bigBullets, 2, 0);
        		break;
            case 2:
                spawnMeteor(asteroids, bullets, floor, megaMan, bigBullets, 3, 1);
                break;
            default:
                spawnMeteor(asteroids, bullets, floor, megaMan, bigBullets, 4, 2);
                break;
        }

        // draw bullets
        for(int i=0; i<bullets.size(); i++){
            Bullet bullet = bullets.get(i);
            graphicsMan.drawBullet(bullet, g2d, this);

            boolean remove =   gameLogic.moveBullet(bullet);
            if(remove){
                bullets.remove(i);
                i--;
            }
        }
        for(int i=0; i<ebullets.size(); i++){
            Bullet ebullet = ebullets.get(i);
            graphicsMan.drawBullet(ebullet, g2d, this);

            boolean remove = gameLogic.moveEBullet(ebullet);
            if(remove){
                ebullets.remove(i);
                i--;
            }
        }

        // draw big bullets
        for(int i=0; i<bigBullets.size(); i++){
            BigBullet bigBullet = bigBullets.get(i);
            graphicsMan.drawBigBullet(bigBullet, g2d, this);

            boolean remove = gameLogic.moveBigBullet(bigBullet);
            if(remove){
                bigBullets.remove(i);
                i--;
            }
        }

        // check bullet-met collisions
        for(int i=0; i<bullets.size(); i++){
            Bullet bullet = bullets.get(i);
            if(isMetSpawned && met.intersects(bullet)){
                if(met.state == GraphicsManager.EnemyWalk[0]){
                    System.out.println(met.getMetHealth());
                    met.setMetHealth(met.getMetHealth()-1);
                    // remove bullet
                    bullets.remove(i);
                    if(met.getMetHealth()==0){

                        status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1000);
                        status.setGameStarted(false);
                        doNewGame();
                        gameLogic.gameWon();

                    }
                    break;
                }
            }
        }

        // check enemy bullet-megaman collisions
        for(int i=0; i<ebullets.size(); i++){
            Bullet ebullet = ebullets.get(i);
            if(megaMan.intersects(ebullet)){

                status.setShipsLeft(status.getShipsLeft()-1);
                // remove bullet
                ebullets.remove(i);
                break;
            }
        }
      
        if(boom == 2)
            restructureLv2();

        status.getAsteroidsDestroyed();
        status.getShipsLeft();
        status.getLevel();

        // update asteroids destroyed label
        destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));

        // update ships left label
        shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));

        //update level label
        levelValueLabel.setText(Long.toString(status.getLevel()));
    }

    private void spawnMeteor(List<Asteroid> asteroids, List<Bullet> bullets, Floor[] floor, MegaMan megaMan, List<BigBullet> bigBullets, int w, int speed) {
        for(int i = 0; i <= w-1; i++) {
            if(!status.isNewAsteroid(i)) {
                if(asteroids.get(i).getX() + asteroids.get(i).getAsteroidWidth() >  0){
                    asteroids.get(i).translate(-(asteroids.get(i).getSpeed() + speed), new Random().nextInt(5));
                    graphicsMan.drawAsteroid(asteroids.get(i), g2d, this);
                }
                else if (boom <= 5){
                    asteroids.get(i).setLocation(this.getWidth() - asteroids.get(i).getAsteroidWidth(),
                            rand.nextInt(this.getHeight() - asteroids.get(i).getAsteroidHeight() - 32));
                }
            }
            else{
                long currentTime = System.currentTimeMillis();
                if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
                    // draw a new asteroid
                    lastAsteroidTime = currentTime;
                    status.setNewAsteroidBool(false, i);
                    asteroids.get(i).setLocation(this.getWidth() - asteroids.get(i).getAsteroidWidth(),
                            rand.nextInt(this.getHeight() - asteroids.get(i).getAsteroidHeight() - 32));
                }

                else{
                    // draw explosion
                    graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
                }
            }
            checkCollisions(asteroids, bullets, floor, megaMan, bigBullets, w);
        }
    }

    private void checkCollisions(List<Asteroid> asteroids, List<Bullet> bullets, Floor[] floor, MegaMan megaMan, List<BigBullet> bigBullets, int w) {
    	for(int i = 0; i < w; i++) {
			//check bullet-asteroid collisions
	        for(int x = 0; x < bullets.size(); x++){
	            Bullet bullet = bullets.get(x);
	            if(asteroids.get(i).intersects(bullet)){
	                // increase asteroids destroyed count
	                status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);

	                removeAsteroid(asteroids, i);



	                if(boom != 5 && boom != 15){
	                    boom=boom + 1;
	                }
	                damage=0;
	                // remove bullet
	                bullets.remove(x);
	                break;
	            }
	        }


	        // check big bullet-asteroid collisions
	        for(int j = 0; j < bigBullets.size(); j++){
	            BigBullet bigBullet = bigBullets.get(j);
	            if(asteroids.get(i).intersects(bigBullet)){
	                // increase asteroids destroyed count
	                status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);

	                removeAsteroid(asteroids, i);



	                if(boom != 5 && boom != 15){
	                    boom=boom + 1;
	                }
	                damage=0;
	            }
	        }
	        
	        //MM-Asteroid collision
	        if(asteroids.get(i).intersects(megaMan)){
	            status.setShipsLeft(status.getShipsLeft() - 1);
	            removeAsteroid(asteroids, i);
	        }

	        //Asteroid-Floor collision
	        for(int k = 0; k < floor.length; k++){
	            if(asteroids.get(i).intersects(floor[k])){
	                removeAsteroid(asteroids, i);

	            }
	        }
    	}
	}

	/**
     * Draws the "Game Over" message.
     */
    protected void drawGameOver() {
        String gameOverStr = "GAME OVER";

        Font currentFont = biggestFont == null? bigFont : biggestFont;
        float fontSize = currentFont.getSize2D();
        bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
        FontMetrics fm = g2d.getFontMetrics(bigFont);
        int strWidth = fm.stringWidth(gameOverStr);
        if(strWidth > this.getWidth() - 10){
            biggestFont = currentFont;
            bigFont = biggestFont;
            fm = g2d.getFontMetrics(bigFont);
            strWidth = fm.stringWidth(gameOverStr);
        }
        int ascent = fm.getAscent();
        int strX = (this.getWidth() - strWidth)/2;
        int strY = (this.getHeight() + ascent)/2;
        g2d.setFont(bigFont);
        g2d.setPaint(Color.WHITE);
        g2d.drawString(gameOverStr, strX, strY);

        boomReset();
        healthReset();
        delayReset();
    }

    protected void drawYouWin() {
        String youWinStr = "You Pass";

        Font currentFont = biggestFont == null? bigFont : biggestFont;
        float fontSize = currentFont.getSize2D();
        bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
        FontMetrics fm = g2d.getFontMetrics(bigFont);
        int strWidth = fm.stringWidth(youWinStr);
        if(strWidth > this.getWidth() - 10){
            biggestFont = currentFont;
            bigFont = biggestFont;
            fm = g2d.getFontMetrics(bigFont);
            strWidth = fm.stringWidth(youWinStr);
        }
        int ascent = fm.getAscent();
        int strX = (this.getWidth() - strWidth)/2;
        int strY = (this.getHeight() + ascent)/2;
        g2d.setFont(bigFont);
        g2d.setPaint(Color.YELLOW);
        g2d.drawString(youWinStr, strX, strY);

        g2d.setFont(originalFont);
        fm = g2d.getFontMetrics();
        String newGameStr = "Next level starting soon";
        strWidth = fm.stringWidth(newGameStr);
        strX = (this.getWidth() - strWidth)/2;
        strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
        g2d.setPaint(Color.YELLOW);
        g2d.drawString(newGameStr, strX, strY);

        boom=3;	//Change value in order for the next level to start

        //		boomReset();
        //		healthReset();
        //		delayReset();
    }

    /**
     * Draws the initial "Get Ready!" message.
     */
    protected void drawGetReady() {
        String readyStr = "Get Ready";
        g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
        FontMetrics fm = g2d.getFontMetrics();
        int ascent = fm.getAscent();
        int strWidth = fm.stringWidth(readyStr);
        int strX = (this.getWidth() - strWidth)/2;
        int strY = (this.getHeight() + ascent)/2;
        g2d.setPaint(Color.WHITE);
        g2d.drawString(readyStr, strX, strY);
    }

    /**
     * Draws the specified number of stars randomly on the game screen.
     * @param numberOfStars the number of stars to draw
     */
    protected void drawStars(int numberOfStars) {
        g2d.setColor(Color.WHITE);
        for(int i=0; i<numberOfStars; i++){
            int x = (int)(Math.random() * this.getWidth());
            int y = (int)(Math.random() * this.getHeight());
            g2d.drawLine(x, y, x, y);
        }
    }

    /**
     * Display initial game title screen.
     */
    protected void initialMessage() {
        String gameTitleStr = "Definitely Not MegaMan";

        Font currentFont = biggestFont == null? bigFont : biggestFont;
        float fontSize = currentFont.getSize2D();
        bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
        FontMetrics fm = g2d.getFontMetrics(bigFont);
        int strWidth = fm.stringWidth(gameTitleStr);
        if(strWidth > this.getWidth() - 10){
            bigFont = currentFont;
            biggestFont = currentFont;
            fm = g2d.getFontMetrics(currentFont);
            strWidth = fm.stringWidth(gameTitleStr);
        }
        g2d.setFont(bigFont);
        int ascent = fm.getAscent();
        int strX = (this.getWidth() - strWidth)/2;
        int strY = (this.getHeight() + ascent)/2 - ascent;
        g2d.setPaint(Color.YELLOW);
        g2d.drawString(gameTitleStr, strX, strY);

        g2d.setFont(originalFont);
        fm = g2d.getFontMetrics();
        String newGameStr = "Press <Space> to Start a New Game.";
        strWidth = fm.stringWidth(newGameStr);
        strX = (this.getWidth() - strWidth)/2;
        strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
        g2d.setPaint(Color.WHITE);
        g2d.drawString(newGameStr, strX, strY);


        fm = g2d.getFontMetrics();
        String itemGameStr = "Press <I> for Item Menu.";
        strWidth = fm.stringWidth(itemGameStr);
        strX = (this.getWidth() - strWidth)/2;
        strY = strY + 16;
        g2d.drawString(itemGameStr, strX, strY);

        fm = g2d.getFontMetrics();
        String shopGameStr = "Press <S> for Shop Menu.";
        strWidth = fm.stringWidth(shopGameStr);
        strX = (this.getWidth() - strWidth)/2;
        strY = strY + 16;
        g2d.drawString(shopGameStr, strX, strY);

        fm = g2d.getFontMetrics();
        String exitGameStr = "Press <Esc> to Exit the Game.";
        strWidth = fm.stringWidth(exitGameStr);
        strX = (this.getWidth() - strWidth)/2;
        strY = strY + 16;
        g2d.drawString(exitGameStr, strX, strY);
    }

    /**
     * Prepare screen for game over.
     */
    public void doGameOver(){
        shipsValueLabel.setForeground(new Color(128, 0, 0));
    }

    /**
     * Prepare screen for a new game.
     */
    public void doNewGame(){
        lastAsteroidTime = -NEW_ASTEROID_DELAY;
        //lastBigAsteroidTime = -NEW_BIG_ASTEROID_DELAY;
        lastShipTime = -NEW_SHIP_DELAY;

        bigFont = originalFont;
        biggestFont = null;

        isMetSpawned=false;

        // set labels' text
        shipsValueLabel.setForeground(Color.BLACK);
        shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
        destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
        levelValueLabel.setText(Long.toString(status.getLevel()));
    }

    /**
     * Sets the game graphics manager.
     * @param graphicsMan the graphics manager
     */
    public void setGraphicsMan(GraphicsManager graphicsMan) {
        this.graphicsMan = graphicsMan;
    }

    /**
     * Sets the game logic handler
     * @param gameLogic the game logic handler
     */
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.status = gameLogic.getStatus();
        this.soundMan = gameLogic.getSoundMan();
    }

    /**
     * Sets the label that displays the value for asteroids destroyed.
     * @param destroyedValueLabel the label to set
     */
    public void setDestroyedValueLabel(JLabel destroyedValueLabel) {
        this.destroyedValueLabel = destroyedValueLabel;
    }

    /**
     * Sets the label that displays the value for ship (lives) left
     * @param shipsValueLabel the label to set
     */
    public void setShipsValueLabel(JLabel shipsValueLabel) {
        this.shipsValueLabel = shipsValueLabel;
    }

    public void setLevelValueLabel(JLabel levelValueLabel){
        this.levelValueLabel = levelValueLabel;
    }

    public int getBoom(){
        return boom;
    }
    public int boomReset(){
        boom= 0;
        return boom;
    }
    public long healthReset(){
        boom= 0;
        return boom;
    }
    public long delayReset(){
        boom= 0;
        return boom;
    }

    protected boolean Gravity(){
        MegaMan megaMan = gameLogic.getMegaMan();
        Met met = gameLogic.getMet();

        Floor[] floor = gameLogic.getFloor();

        for(int i=0; i<floor.length; i++){
            if((megaMan.getY() + megaMan.getMegaManHeight() -17 < this.getHeight() - floor[i].getFloorHeight()/2)
                    && Fall() ){
                if(isMetSpawned){
                    if(!megaMan.intersects(met)){
                        megaMan.translate(0 , 2);
                        return true;
                    }else if(megaMan.intersects(met)){
                        megaMan.translate(-(met.x-megaMan.x) , -2);
                        return true;
                    }
                }else{
                    megaMan.translate(0 , 2);
                    return true;
                }
            }
        }
        return false;
    }
    //Bullet fire pose
    protected boolean Fire(){
        MegaMan megaMan = gameLogic.getMegaMan();
        List<Bullet> bullets = gameLogic.getBullets();
        for(int i=0; i<bullets.size(); i++){
            Bullet bullet = bullets.get(i);
            if((bullet.getX() > megaMan.getX() + megaMan.getMegaManWidth()) &&
                    (bullet.getX() <= megaMan.getX() + megaMan.getMegaManWidth() + 60)){
                return true;
            }
        }
        return false;
    }

    //BigBullet fire pose
    protected boolean Fire2(){
        MegaMan megaMan = gameLogic.getMegaMan();
        List<BigBullet> bigBullets = gameLogic.getBigBullets();
        for(int i=0; i<bigBullets.size(); i++){
            BigBullet bigBullet = bigBullets.get(i);
            if((bigBullet.getX() > megaMan.getX() + megaMan.getMegaManWidth()) &&
                    (bigBullet.getX() <= megaMan.getX() + megaMan.getMegaManWidth() + 60)){
                return true;
            }
        }
        return false;
    }

    //Platform Gravity
    public boolean Fall(){
        MegaMan megaMan = gameLogic.getMegaMan();
        Platform[] platform = gameLogic.getNumPlatforms();
        for(int i=0; i< platform.length; i++){
            if((((platform[i].getX() < megaMan.getX()) && (megaMan.getX()< platform[i].getX() + platform[i].getPlatformWidth()))
                    || ((platform[i].getX() < megaMan.getX() + megaMan.getMegaManWidth())
                    && (megaMan.getX() + megaMan.getMegaManWidth()< platform[i].getX() + platform[i].getPlatformWidth())))
                    && megaMan.getY() + megaMan.getMegaManHeight() + 6 == platform[i].getY()
                    ){
                return false;
            }
        }
        return true;
    }

    public void restructureLv2(){
        Platform[] platform = gameLogic.newNumPlatforms(this,8);
        for(int i=0; i<platform.length; i++){
            if(i<4)	platform[i].setLocation(50+ i*50, getHeight()/2 + 140 - i*40);
            if(i==4) platform[i].setLocation(50 +i*50, getHeight()/2 + 140 - 3*40);
            if(i>4){
                platform[i].setLocation(50 + i*50, getHeight()/2 + 20 + (i-4)*40 );
            }
        }
        status.setLevel(2);
    }

    public void restructureLv3(){

        Platform[] platform = gameLogic.newNumPlatforms(this,9);
        for(int i=0; i<platform.length; i++){
            if(i>4)	platform[i].setLocation(45+ i*45, (getHeight()/2 + 260 - i*30));
            if(i==4) platform[i].setLocation(45 +i*45, (getHeight()/2 + 260 - 4*30));
            if(i<4){
                platform[i].setLocation(45 + i*45, (getHeight()/2 + 130 + (i-4)*30) );
            }
        }
        status.setLevel(3);
    }

    public void restructureLv4(){

        Platform[] platform = gameLogic.newNumPlatforms(this,6);
        for(int i=0; i<platform.length; i++){
            //bottom
            if(i==0) platform[i].setLocation(getWidth()/3 +platform[i].width+5, (getHeight()/2 + 140 - i*30));
            //left
            if(i==1) platform[i].setLocation((getWidth()/3) , (getHeight()/2 + 140 + 50*i - 4*30));

            if(i==2)platform[i].setLocation((getWidth()/3) , (getHeight()/2 + 140 + 50*i - 4*30));
            //right
            if(i==3) platform[i].setLocation(getWidth()/3+(2*platform[i].width)+5, (getHeight()/2 + 140 + 50*(i-1) - 4*30));

            if(i==4)platform[i].setLocation(getWidth()/3+(2*platform[i].width)+5 , (getHeight()/2 + 140 + 50*(i-3) - 4*30));
            //top
            if(i==5) platform[i].setLocation(getWidth()/3 +platform[i].width+5, (getHeight()/2 + 20 + (i-4)*30) );

        }
        isMetSpawned=true;
        status.setLevel(4);
    }

    public void removeAsteroid(List<Asteroid> asteroid, int i){
        // "remove" asteroid
        asteroidExplosion = new Rectangle(
                asteroid.get(i).x,
                asteroid.get(i).y,
                asteroid.get(i).width,
                asteroid.get(i).height);
        asteroid.get(i).setLocation(-asteroid.get(i).width, -asteroid.get(i).height);
        status.setNewAsteroidBool(true, i);
        lastAsteroidTime = System.currentTimeMillis();

        // play asteroid explosion sound
        soundMan.playAsteroidExplosionSound();
    }
}
