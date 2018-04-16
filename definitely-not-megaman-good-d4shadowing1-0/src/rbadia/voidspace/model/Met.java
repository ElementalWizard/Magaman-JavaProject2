package rbadia.voidspace.model;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.main.GameScreen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by Elemental on 6/13/2017.
 */
public class Met  extends Rectangle {
    public static final int DEFAULT_SPEED = 3;
    private static final int Y_OFFSET = 0; // initial y distance of the ship from the bottom of the screen

    private int metHealth=3;

    int px=0;
    int py=0;
    private boolean just_fired = false;

    private int metWidth = 20;
    private int metHeight = 20;
    private int speed = DEFAULT_SPEED;
    public boolean ll=false,lr=true;
    public static int xEMove=0;
    private int timer = 0;
    public BufferedImage state = GraphicsManager.EnemyWalk[0];

    private GameScreen screen;



    /**
     * Creates a new ship at the default initial location.
     * @param screens the game screen
     */
    public Met(GameScreen screens){
        this.setLocation(screens.getWidth()/2 +200, (screens.getHeight()-33)+(height/2-8));
        this.setSize(metWidth, metHeight);
        screen=screens;
    }

    public void tick(){

        Random random = new Random();
        if(getMetHealth()==0){
            GameScreen.isMetSpawned = false;
        }

        if(timer==0){
           px =random.nextInt(2);
           System.out.println(px);
           py = random.nextInt(10)+1;
           just_fired=false;
        }
        timer++;
        if(timer >=120){
            timer = 0;
        }

        if(px==0){
            state=GraphicsManager.EnemyWalk[0];
        }else if(px == 1){
            state=GraphicsManager.EnemyHide[2];
        }
        if(py >3 && py <7 && !just_fired && px==0){
            just_fired=true;
            screen.gameLogic.enemyfireBullet();

        }


    }

    /**
     * Get the default ship width
     * @return the default ship width
     */
    public int getMetWidth() {
        return metWidth;
    }

    /**
     * Get the default ship height
     * @return the default ship height
     */
    public int getMetHeight() {
        return metHeight;
    }

    /**
     * Returns the current ship speed
     * @return the current ship speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set the current ship speed
     * @param speed the speed to set
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Returns the default ship speed.
     * @return the default ship speed
     */
    public int getDefaultSpeed(){
        return DEFAULT_SPEED;
    }

    public boolean isLl() {
        return ll;
    }

    public void setLl(boolean ll) {
        this.ll = ll;
    }

    public boolean isLr() {
        return lr;
    }

    public void setLr(boolean lr) {
        this.lr = lr;
    }
    public int getxMove() {
        return xEMove;
    }

    public void setxMove(int xMove) {
        this.xEMove = xMove;
    }

    public int getMetHealth() {
        return metHealth;
    }

    public void setMetHealth(int metHealth) {
        this.metHealth = metHealth;
    }
}
