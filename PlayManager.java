/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import mino.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;
public class PlayManager {

    // 25 *12
    // 25 * 20
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int right_x;
    public static int left_x;
    public static int top_y;
    public static int bottom_y;

    // MINO
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();
    
    //FPS frames to 60
    public static int dropInterval = 60;
    boolean gameOver;
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();
    //Score
    int level =1;
    int lines;
    int score;

    public PlayManager() {
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2); 
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;
        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y =  top_y + 500;
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X,MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }
        private Mino pickMino() {
        Mino mino = null;
        int i = new Random().nextInt(7); // used random class to generate mino (0-6)

        switch (i) {
            case 0:
                mino = new Mino_L1();
                break;
            case 1:
                mino = new Mino_L2();
                break;
            case 2:
                mino = new Mino_Square();
                break;
            case 3:
                mino = new Mino_Bar();
                break;
            case 4:
                mino = new Mino_T();
                break;
            case 5:
                mino = new Mino_Z1();
                break;
            case 6:
                mino = new Mino_Z2();
                break;
        }
        return mino;
    }

    public void update(){
        if(currentMino.active == false){
           staticBlocks.add(currentMino.b[0]);
           staticBlocks.add(currentMino.b[1]);
           staticBlocks.add(currentMino.b[2]);
           staticBlocks.add(currentMino.b[3]);
      if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
          gameOver = true;
      }
           
           currentMino.deactivating = false;
           
           currentMino = nextMino;
           currentMino.setXY(MINO_START_X,MINO_START_Y);
           nextMino = pickMino();
           nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
           checkDelete();
        }
        else{
        currentMino.update();
        }
    }
    private void checkDelete(){
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;
        while(x < right_x && y < bottom_y){
            for(int i =0; i<staticBlocks.size(); i++){
    if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                blockCount++;
            }
        }
            x+= Block.SIZE;
            if(x == right_x){
                if(blockCount == 12){
                    effectCounterOn  = true;
                    effectY.add(y);
                    for(int i = staticBlocks.size() - 1; i > - 1; i--){
                        if(staticBlocks.get(i).y == y){
                        staticBlocks.remove(i);
                      }
                    }
                    
                    lineCount++;
                    lines++;
                    if (lines % 10 == 0 && dropInterval > 1) {
                        level++;
                        if(dropInterval>10){
                            dropInterval -= 10;
                        }
                        else{
                            dropInterval -= 1;
                        }
                    }
                    
                   for(int i = 0; i < staticBlocks.size(); i++){
                        if(staticBlocks.get(i).y < y){
                        staticBlocks.get(i).y += Block.SIZE;
                        }
                      }
                    }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }
        //Add score
        if(lineCount>0){
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        // draw play area frame
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f)); // Set the stroke width
        g2.drawRect(left_x - 4, top_y - 2, WIDTH + 8, HEIGHT + 8);
        
        //Draw next mino frame square
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("Next", x + 60, y + 60);
        //Draw score
        g2.drawRect(x,top_y,250,300);
        x += 40;
        y = top_y + 90;
        g2.drawString("Level: "+ level, x, y); y+= 70;
        g2.drawString("Lines: "+ lines, x, y); y+= 70;
        g2.drawString("Score: "+ score, x, y); 
        
        // draw the current Mino
        if (currentMino != null) {
            currentMino.draw(g2);
        }
        //draw the next mino
        nextMino.draw(g2);
        //Draw static blocks 
        for(int i = 0; i < staticBlocks.size(); i++){
        staticBlocks.get(i).draw(g2);
    }
        //DRAW METHOD
        if(effectCounterOn){
            effectCounter++;
            g2.setColor(Color.white);
            for(int i = 0; i< effectY.size();i++){
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }
            if(effectCounter == 10){
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }
        
        // draw pause
        g2.setColor(Color.YELLOW);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x =  left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        else if (KeyHandler.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("Paused", x, y);
        }
        x = 35;
        y = top_y +320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 40));
        g2.drawString("TETRIS", x + 20, y);
    }
}