package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class Controller {
	
	@FXML
	AnchorPane anchorPane;
	@FXML
	Canvas canvas;
	@FXML
	Button buttonStart, buttonStop, buttonPause;
	@FXML
	TextField txField;
	@FXML
	Label label;
	@FXML
	Label genLab;
	
	private Thread t = new Thread();
	
	Affine affine;
	Simulation sim; 
	
	//for canvas
	int width = 800;
    int height = 480;
	
    
	@FXML
	public void initialize() {
		//grid
		this.affine = new Affine();

		this.affine.appendScale(200/40f, 200/40f);// size of 'grid'
		this.sim = new Simulation(width, height);
		
		sim.step();
		draw();
	}
	
	public synchronized void draw() {
		
		GraphicsContext grap = this.canvas.getGraphicsContext2D();
		grap.setTransform(this.affine);
		
		grap.setFill(Color.LIGHTGRAY);	
		grap.fillRect(0, 0, 800, 480);
		
		grap.setFill(Color.BLACK);
		for (int x = 0; x < this.sim.width; x++) {
			for (int y = 0; y < this.sim.height; y++) {
				if (this.sim.getState(x, y) == 1) {
					grap.fillRect(x, y, 1, 1);
	            }
	        }
	    }
		grap.setStroke(Color.BLACK);
		grap.setLineWidth(0.04);
		
	    for (int i = 0; i<this.sim.width; i++) {
	    	grap.strokeLine(i,0, i, 480);
		}
	    for (int i = 0; i<this.sim.height; i++) {
	    	grap.strokeLine(0,i,800,i);
		}
	}
	
	public void getTxtFeed(ActionEvent e) {
		int numberOfSeeds = Integer.parseInt(txField.textProperty().getValue());
		
		sim.setSeeds(numberOfSeeds);
		sim.generateRandomSeedsForSimulation(width, height);
		draw();
	}
	
	public synchronized void setButtonAction(MouseEvent e) {
		if(!t.isAlive()) { 
			
			buttonStart.setVisible(false);
			buttonPause.setVisible(true);
			buttonStop.setVisible(true);
			
			simulateGenerations();
		} //else t.notify();
		
	}
	
	public void pauseSim(MouseEvent e) {
		if(buttonPause.isVisible()) {
			Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	            	buttonStart.setVisible(true);
	            }
	          });
		}
	}
	public void stopSim(MouseEvent e) {
		if(buttonStop.isVisible()) {
			buttonPause.setVisible(false);
			buttonStop.setVisible(false);
			buttonStart.setVisible(true);
					
			this.sim = new Simulation(width, height);
			sim.step();
			draw();
		}
	}
	public synchronized void simulateGenerations() {
		t = new Thread (()->{
			for(int i=0; ; i++) {
				try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				Platform.runLater(new Runnable() {
		            @SuppressWarnings("deprecation")
					@Override
		            public void run() {
		            	genLab.setText("GENERATIONS\n"+sim.numberOfGenerations);
		            	sim.incrementGenerations();
		            	sim.step();
		            	draw();
		            	if(buttonPause.isPressed()) {
		        			synchronized (t) {
								t.stop();
							}
		        		}
		            }
		          });
				if(buttonStop.isPressed()) {
					break;
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
}
