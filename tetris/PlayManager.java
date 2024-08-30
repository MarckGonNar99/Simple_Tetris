
package tetris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;
import mino.Block;
import mino.Mino;
import mino.Mino_4;
import mino.Mino_I;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;


public class PlayManager {
    
    //Dibujar el area de juego
    //Controlar las piezas
    //Acciones de Gameplay (Borrar lineas, puntuación)
    
    
    //Tamaño del area de juego
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;
    
    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    //Siguiente Mino
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();
    
    //Caida de Bloques (Frames)
    public static int dropInterval = 60;
    
    //Efectos
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();
    boolean gameOver;
    
    
    //Puntuación
    int level = 1;
    int lines;
    int score;
    
    
    public PlayManager(){
        left_x = (PanelGame.WIDTH/2)-(WIDTH/2);
        right_x= left_x + WIDTH;
        top_y =50;
        bottom_y= top_y + HEIGHT;
        
        MINO_START_X = left_x + (WIDTH/2)- Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        
        NEXTMINO_X = right_x+175;
        NEXTMINO_Y = top_y+500;
        
        //Seleccionar el primer Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        //Selección del siguiente Mino
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
        
        

        
    }
    
    private Mino pickMino(){
        Mino mino = null;
        int i = new Random().nextInt(7);
        
        switch(i){
            case 0: mino= new Mino_L1();break;
            case 1: mino= new Mino_L2();break;
            case 2: mino= new Mino_4();break;
            case 3: mino= new Mino_I();break;
            case 4: mino= new Mino_T();break;
            case 5: mino= new Mino_Z1();break;
            case 6: mino= new Mino_Z2();break;
        }
        
        return mino;    
    }

    public void update() {
        //Comprobamos que el Mino actual esté descativado
        if(currentMino.active == false){
            //Si está desactivado lo añadiamos al staticBlock
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);
            
            //GAME OVER MECANICA
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                gameOver = true;
            }
            
            currentMino.deactivating=false;
            
            //Dibujamos el siguiente Mino
            currentMino = nextMino;
            currentMino.setXY( MINO_START_X,MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY( NEXTMINO_X, NEXTMINO_Y);
            
            //En este proceso chequeamos que se pueda borrar linea
            checkDelete();
            
        }else{
            currentMino.update();
        }
        
    }
    
    private void checkDelete(){
        
        //El másximo de bloques en este juego es de 12 bloques
        int x = left_x;
        int y= top_y;
        int blockCount =0;
        int lineCounter =0;
        
        while(x < right_x && y < bottom_y){
           
            for(int i = 0; i< staticBlocks.size(); i++){
                    if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                        blockCount++;
                    }
            }
            
            
            x += Block.SIZE;
            
            if(x == right_x){
                if(blockCount == 12){
                    
                    effectCounterOn = true;
                    effectY.add(y);
                    
                    
                    //Borrar los bloques en la linea seleccionada
                    for(int j = staticBlocks.size()-1; j > -1; j--){
                        if(staticBlocks.get(j).y == y){
                            staticBlocks.remove(j);
                        }
                    }
                    
                    lineCounter++;
                    lines++;
                    //Control de velocidad con respecto al nivel
                    if(lines % 10 ==0 && dropInterval > 1){
                        level++;
                        if(dropInterval>10){
                            dropInterval-=10;
                        }else{
                            dropInterval -= 1;
                        }
                    }
                    
                    
                    //La linea de arriba ocupa el lugar de la eliminada
                    for(int k = 0; k< staticBlocks.size(); k++){
                        if(staticBlocks.get(k).y < y){
                            staticBlocks.get(k).y += Block.SIZE;
                        }
                    }
                }
                
                blockCount=0;
                x =left_x;
                y += Block.SIZE;
            }
            // Añadir puntuación
            if(lineCounter >0){
                int singleLineScore = 10* level;
                score += singleLineScore * lineCounter;
            }
            
        }
    }
    
    
    
    

    public void draw(Graphics2D g2) {
        //Area limitrofe del juego
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);
        
        //frame de "siguiente figura"
        int x = right_x +100;
        int y = bottom_y -200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); NO FUNCIONA
        g2.drawString("NEXT", x+60, y+60);
        
        
        //Dibujar marco de puntuación
        g2.drawRect(x, top_y,250,300);
        x += 40;
        y = top_y +90;
        g2.drawString("LEVEL: "+level, x, y); y +=60;
        g2.drawString("LINES: "+lines, x, y); y +=60;
        g2.drawString("SCORE: "+score, x, y);
        
        
        
        //Dibujar el Mino actual
        if(currentMino != null){
            currentMino.draw(g2);
        }
        nextMino.draw(g2);
        
        //Dibujar los bloques estáticos
        for(int i=0; i<staticBlocks.size();i++){
            staticBlocks.get(i).draw(g2);
        }
        
        
        //Dibujar Game Over
        g2.setColor(Color.red);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x =left_x +25;
            y = top_y +320;
            g2.drawString("GAME OVER", x, y);
        }
        
        
        //Dibujar efectos
        if(effectCounterOn){
            effectCounter++;
            
            g2.setColor(Color.darkGray);
            for(int i =0; i< effectY.size();i++){
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }
            
            if(effectCounter == 10){
                effectCounterOn = false;
                effectCounter =0;
                effectY.clear();
            }
        }
        
    }
    
}
