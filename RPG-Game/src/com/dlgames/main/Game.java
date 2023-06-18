package com.dlgames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.dlgames.entities.Entity;
import com.dlgames.entities.Player;
import com.dlgames.graphics.Spritesheet;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 400;
	private final int HEIGTH = 300;
	private final int SCALE = 2;
	
	private BufferedImage image;
	
	public List<Entity> entities;
	public Spritesheet spritesheet;
	

	
	//Método responsável pelo dimensionamento da janela
	
	public Game() {
		setPreferredSize(new Dimension (WIDTH*SCALE,HEIGTH*SCALE));
		initFrame();
		
		// Iniciando objetos
		image = new BufferedImage(WIDTH,HEIGTH,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		
		Player player = new Player(0, 0, 32, 32, spritesheet.getSprite(0, 0, 32, 32));
		entities.add(player);
	}
	
	public void initFrame() {
		frame= new JFrame("LeD Games");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);	
	}
	
	public synchronized void start(){
		thread= new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop(){
	isRunning= false;
	try {
		thread.join();
	}catch(InterruptedException e) {
		e.printStackTrace();
	}
	
	}
	
	public static void main(String args[]){
		Game game = new Game();
		game.start();
	}
	
	public void tick(){
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.tick();
		}
	}	
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs== null) {
			this.createBufferStrategy(3);
			return;
		}
		
		 Graphics g = image.getGraphics();
		 
		 g.setColor(new Color(19,19,19));
		 g.fillRect(0,0,WIDTH,HEIGTH);
		
		 for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.render(g);
			}
		 
		 
		 g.dispose();
		 g = bs.getDrawGraphics(); 
		 g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGTH*SCALE,null);
		 bs.show();
		
	}
	
	// Looping responsável por manter o jogo rodar
	
	public void run() {
		long lastTime= System.nanoTime();
		double amountOfTicks= 60.0;
		double ns= 1000000000/amountOfTicks;
		double delta= 0;
		int frames= 0;
		double timer= System.currentTimeMillis();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime)/ns;
			lastTime= now;
			if(delta >=1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if (System.currentTimeMillis()-timer >= 1000) {
				System.out.println("FPS: "+ frames);
				frames= 0;
				timer+= 1000;
				
			}
		}
		
		stop();
	}

	
}