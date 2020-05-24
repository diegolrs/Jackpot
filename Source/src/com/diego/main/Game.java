package com.diego.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import com.diego.graphics.Spritesheet;
import com.diego.ui.PlayAgain;

public class Game extends Canvas implements Runnable,MouseListener {
	
	// FPS control
	private boolean isRunning;
	private int frames;
	private long timer, lastTime, now;
	private static final int FPS = 60;
	private static final long ns = 1000000000/FPS;
	
	// Project configuration
	public static final int WIDTH = 160;
	public static final int HEIGHT = 120;
	public static final int SCALE = 4;
	private final int quantOfItems = 3;
	private final int RIGHT_ITEM = 2;
	private static final long serialVersionUID = 1L;
	
	// Canvas configuration
	public JFrame frame;
	public Graphics g;
	public BufferedImage img;
	public BufferStrategy bs;
	public Spritesheet sprites;
	public Random rand;
	private Color bgColor;
	
	// State machine
	public PlayAgain playAgain;
	public static String state;
	private boolean playerWon; // When player wins
	public static boolean restart; // Restart game
	
	// Time control
	private int timePast;
	private int speed;
	private int curTime, timeToChangeOption, initialTimeToChangeOption;
	private int timeToStopItem1, timeToStopItem2, timeToStopItem3;
	
	// Items's variables
	private int optionItem1, optionItem2, optionItem3; // Current chosen item
	private boolean item1Stopped, item2Stopped, item3Stopped; // Stop the animation
	private boolean blinkWinScreen; // Blink the winning scene 
	private int curFramesBlink, maxFramesBlink; // Frames to change blink
	private BufferedImage[] items; // An array with every item
	private BufferedImage cherry, watermelon, coin; // Sprites of items

	// Machine render setup
	private int margin = 20;
	private int newWidth = WIDTH - margin*2;
	private int newHeight = HEIGHT - margin*2;
	
	Game(){
		// Instantiating objects
		frame = new JFrame("Jackpot");
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		sprites = new Spritesheet("/spritesheet.png");
		items = new BufferedImage[9];
		rand = new Random();
		playAgain = new PlayAgain();
				
		// Getting sprites
		cherry = sprites.getSprite(0, 0, 16, 16);
		watermelon = sprites.getSprite(16, 0, 16, 16);
		coin = sprites.getSprite(32, 0, 16, 16);
		
		// Default background color
		bgColor = new Color(89, 0, 107);
		
		// Setup/Restart the logic of game
		restart();
		
		// Local Configurations
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		this.addMouseListener(this);
		
		// Configurations of JFrame
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		// Initializing variables
		optionItem1 = optionItem2 = optionItem3 = 0;
		frames = 0; // FPS counter
		curFramesBlink = 0;
		maxFramesBlink = 25;
		state = "NORMAL"; // Set default state
		isRunning = true; // Start game
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		new Thread(game).start();
	}
	
	public void restart() {	
		timePast = 0;
		curTime = 0;
		speed = 0;
		initialTimeToChangeOption = 2;
		
		timeToChangeOption = initialTimeToChangeOption;
		item1Stopped = item2Stopped = item3Stopped = false;
		
		timeToStopItem1 = rand.nextInt(120) + 60;
		timeToStopItem2 = rand.nextInt(120) + 240;
		timeToStopItem3 = rand.nextInt(120) + 420;
		
		playerWon = false;
		state = "NORMAL";
		restart = false;
	}
	
	public void drawMachine() {
		g.setColor(new Color(250, 237, 252));
		g.fillRect(margin, margin, newWidth, newHeight);
	}
	
	public void drawItems() {	
		int maskx = 4;
		
		for(int i = 0; i < 3; i++) {
			g.drawImage(items[i], margin + maskx, 9 + i*36, 32, 32, null); // First column
			g.drawImage(items[i + 3], margin + maskx + 40, 9 + i*36, 32, 32, null); // Second column
			g.drawImage(items[i+6], margin + maskx + 80, 9 + i*36, 32, 32, null); // Third column
		}	
			
	}
	
	public void coverItems() {
		// Grid lines
		for(int i = 0; i < 3; i++) {
			g.setColor(new Color(0,0,0));
			g.drawRect(margin + i*(int)(newWidth/quantOfItems), margin, (int)(newWidth/quantOfItems), newHeight);
		}
		
		// Background	
		g.setColor(bgColor);
		g.fillRect(0, 0, WIDTH, margin);
		g.fillRect(0, HEIGHT - margin + 1, WIDTH, HEIGHT);
	}
	
	public void drawYouWonScene() {
		curFramesBlink++;
		maxFramesBlink = 25;
		
		if(curFramesBlink >= maxFramesBlink) {
			blinkWinScreen = !blinkWinScreen;
			curFramesBlink = 0;
		}
		
		if(blinkWinScreen) {
			g.setColor(Color.green);
			g.setFont(new Font("arial", Font.BOLD, 15));
			g.drawString("$$$$$$$$", (int)(WIDTH/2) - 30, 17);
		}
		
	}
	
	public void drawValidations() {
		g.setFont(new Font("arial", Font.BOLD, 15));
		
		if(item1Stopped) {
			if(optionItem1 == RIGHT_ITEM) {
				g.setColor(Color.green);
				g.drawString("V", (int)(WIDTH/2) - 45, HEIGHT - 5);
			}else{
				g.setColor(Color.red);
				g.drawString("X", (int)(WIDTH/2) - 45, HEIGHT - 5);
			}
		}
		
		if(item2Stopped) {
			if(optionItem2 == RIGHT_ITEM) {
				g.setColor(Color.green);
				g.drawString("V", (int)(WIDTH/2) - 5, HEIGHT - 5);
			}else{
				g.setColor(Color.red);
				g.drawString("X", (int)(WIDTH/2) -5, HEIGHT - 5);
			}
		}
		
		if(item3Stopped) {
			if(optionItem3 == RIGHT_ITEM) {
				g.setColor(Color.green);
				g.drawString("V", (int)(WIDTH/2) + 35, HEIGHT - 5);
			}else{
				g.setColor(Color.red);
				g.drawString("X", (int)(WIDTH/2) + 35, HEIGHT - 5);
			}
		}
		
	}
	
	public void run() {		
		lastTime = System.nanoTime();
		timer = System.currentTimeMillis();
		
		while(isRunning) {
			now = System.nanoTime();
			
			if(now - lastTime >= ns) {
				tick();
				render();
				frames++;
				lastTime = now;
			}
			
			if(System.currentTimeMillis() - timer >= 1000){		
				System.out.println("Frames: " + frames);
				frames = 0;
				timer += 1000;
			}		
		}
		
	}
	
	public void tick() {
		// Waiting click to play again
		if(state == "WAITING") {	
			if(optionItem1 == RIGHT_ITEM && optionItem2 == RIGHT_ITEM && optionItem3 == RIGHT_ITEM)
				playerWon = true;
			
			if(restart)
				restart();
			
			playAgain.tick();
			
		} else if(state == "NORMAL") {
			timePast++;
			curTime++;
			
			speed = (int) (timePast/FPS * 2);
			timeToChangeOption = initialTimeToChangeOption + speed;
				
			if(curTime >= timeToChangeOption) {
				if(timePast < timeToStopItem1)
					optionItem1++;
				else
					item1Stopped = true;
				
				if(timePast < timeToStopItem2)
					optionItem2++;
				else
					item2Stopped = true;
				
				if(timePast < timeToStopItem3)
					optionItem3++;
				else
					item3Stopped = true;
				
				curTime = 0;
			}
			
			//Reorder
			if(optionItem1 >= quantOfItems) {
				optionItem1 = 0;
			}
			
			if(optionItem2 >= quantOfItems) {
				optionItem2 = 0;
			}
			
			if(optionItem3 >= quantOfItems) {
				optionItem3 = 0;
			}
			
			switch(optionItem1) {
				case 0:
					items[0] = cherry;
					items[1] = watermelon;
					items[2] = coin;
					break;
				case 1:
					items[1] = cherry;
					items[2] = watermelon;
					items[0] = coin;
					break;
				case 2:
					items[2] = cherry;
					items[0] = watermelon;
					items[1] = coin;	
			}
			
			switch(optionItem2) {
				case 0:
					items[3] = cherry;
					items[4] = watermelon;
					items[5] = coin;
					
					break;
				case 1:
					items[4] = cherry;
					items[5] = watermelon;
					items[3] = coin;
					break;
				case 2:
					items[5] = cherry;
					items[3] = watermelon;
					items[4] = coin;
					break;			
			}
			
			switch(optionItem3) {
				case 0:
					items[6] = cherry;
					items[7] = watermelon;
					items[8] = coin;
					break;
				case 1:
					items[7] = cherry;
					items[8] = watermelon;
					items[6] = coin;
					break;
				case 2:
					items[8] = cherry;
					items[6] = watermelon;
					items[7] = coin;
					break;			
			}
			
			if(item1Stopped && item2Stopped && item3Stopped)
				state = "WAITING";			
		}
		
	}
	
	public void render() {
		bs = this.getBufferStrategy();
		
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}	
		
		g = img.getGraphics();
		
		// Background
		g.setColor(bgColor);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		// Draw Jackpot Machine
		drawMachine();
		drawItems();
		coverItems();
		drawValidations();
		
		if(playerWon)
			drawYouWonScene();
		
		if(state == "WAITING")
			playAgain.render(g);
		
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 8));
		String word = " DIEGO REIS 2020";
		
		for(int i = 0; i < word.length(); i++) {
			String singleChar = word.charAt(i) + "";
			g.drawString(singleChar, WIDTH-10, 7 + 7*i);
		}
		
		g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		bs.show();	
	}

	public void mouseClicked(MouseEvent e) {
		if(state == "WAITING") {
			playAgain.setClicked(true);
			playAgain.setXMouse(e.getX());
			playAgain.setYMouse(e.getY());
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
}
