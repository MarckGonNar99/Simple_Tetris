
package tetris;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        
        
        //Crear frame de juego
        JFrame frame = new JFrame();
        
        //Añadir Panel del PanelGame
        PanelGame panel = new PanelGame();
        frame.add(panel);
        frame.pack();
                
                
                
        frame.setResizable(false);
        frame.setTitle("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
        
        panel.launchGame();
    }
    
}
