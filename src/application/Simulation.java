package application;

import java.util.HashMap;

public class Simulation {
	
	public int width;
   	public int height;
   	
    int[][] board;
 
    public boolean oneCycleOfSeeds = false;
    public int numberOfSeeds;
    public int numberOfGenerations = 0;
   
    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new int[width][height];
    }

    public void setAlive(int x, int y) {
        this.board[x][y] = 1;
    }

    public void setDead(int x, int y) {
        this.board[x][y] = 0;
    }

    public int countAliveNeighbours(int x, int y) {
        int count = 0;

        count += getState(x - 1, y - 1);
        count += getState(x, y - 1);
        count += getState(x + 1, y - 1);

        count += getState(x - 1, y);
        count += getState(x + 1, y);

        count += getState(x - 1, y + 1);
        count += getState(x, y + 1);
        count += getState(x + 1, y + 1);

        return count;
    }

    public int getState(int x, int y) {
        if (x < 0 || x >= width) {
            return 0;
        }

        if (y < 0 || y >= height) {
            return 0;
        }

        return this.board[x][y];
    }

    public synchronized void step() {
        int[][] newBoard = new int[width][height];

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                int aliveNeighbours = countAliveNeighbours(x, y);

                if(getState(x, y) == 1) {
                	if(aliveNeighbours < 2) {
                        newBoard[x][y] = 0;
                    } else if(aliveNeighbours == 2 || aliveNeighbours == 3) {
                        newBoard[x][y] = 1;
                    } else if (aliveNeighbours > 3) {
                        newBoard[x][y] = 0;
                    }
                } else {
                    if (aliveNeighbours == 3) {
                        newBoard[x][y] = 1;
                    } 																				
                }

            }
        }

        this.board = newBoard;
    }
    public void incrementGenerations() {
    	this.numberOfGenerations++;
    }
    
    public void generateRandomSeedsForSimulation(int w, int h) {
    	//no duplicate x,y value
    	HashMap <int[], int[]> noDup = new HashMap<>();
    	
    	for(int i=0; i < this.numberOfSeeds; ++i) {
    		int x = (int)(Math.random()*w-1)+0;
			int y = (int)(Math.random()*h-1)+0;
			int[] set = {x,y};
			
			if(!noDup.containsKey(set)) {
				setAlive(x, y);
				noDup.put(set, null);
			}
			else { 
				--i;
			}
		}
    }
  
    public void setSeeds(int s) {
    	this.numberOfSeeds = s;
    }
    
}
