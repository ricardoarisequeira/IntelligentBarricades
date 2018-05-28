package barricades;

import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Environment
 * @author Rui Henriques
 */
public class City {

	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;

	/** A: Environment */

	public List<Car> Cars;
	public List<Police> Polices;
	public Map map;
	public GraphicalInterface GUI;
	public int nX, nY;
	
	public City( int nX, int nY) {
		this.nX = nX;
		this.nY = nY;
		initialize();
	}

	private void initialize() {
		map = new Map( nX, nY);
		Cars = new ArrayList<Car>();
		Polices = new ArrayList<Police>();
		Cars.add(new Car(new Point(22,6)));
		Cars.add(new Car(new Point(23,9)));
		Cars.add(new Car(new Point(16,8)));
		Cars.add(new Car(new Point(31,10)));
		Cars.add(new Car(new Point(28,4)));
		Cars.add(new Car(new Point(1,29)));
		Cars.add(new Car(new Point(9,27)));
		Cars.add(new Car(new Point(11,21)));
		Cars.add(new Car(new Point(17,25)));
		Cars.add(new Car(new Point(14,29)));
		Cars.add(new Car(new Point(23,23)));
		Cars.add(new Car(new Point(29,25)));
		Cars.add(new Car(new Point(3,9)));
		Cars.add(new Car(new Point(8,5)));
		Cars.add(new Car(new Point(9,11)));
		Cars.add(new Car(new Point(5,16)));
		Cars.add(new Car(new Point(20,16)));
		Cars.add(new Car(new Point(8,17)));
		Cars.add(new Car(new Point(17,17)));
		Polices.add(new Police(new Point(30,16)));
		Polices.add(new Police(new Point(3,21)));
		Cars.add(new Thief(new Point(1,3)));
	}

	
	/** B: Elicit agent actions */
	
	RunThread runThread;

	public class RunThread extends Thread {
		
		int time;
		private volatile boolean running = true;
		
		public RunThread(int time){
			this.time = time;

		}

		public void terminate(){
			running = false;
		}
		
	    public void run() {

	    	while(running){

		    	removeCars();
				for(Car a : Cars) a.go(map);
				for(Police p: Polices) p.go(map);
				substituteCars();
				displayCars();
				try {
					sleep(time*10);
				} catch (InterruptedException e) {
					this.interrupt();
				}
	    	}
	    }
	}

	public void substituteCars() {

		int deletedCars = 0;
		int deletedPolices = 0;

		Iterator<Car> itr = Cars.iterator();
		Iterator<Police> itrP = Polices.iterator();

		while (itr.hasNext()) {
			Car car = itr.next();
			if (!car.inCity(map)) {
				itr.remove();
				deletedCars++;
			}
		}
		while (itrP.hasNext()) {
			Police police = itrP.next();
			if (!police.inCity(map)) {
				itrP.remove();
				deletedPolices++;
			}
		}
		for (int p = 0; p < deletedPolices; p++)
			insertPolice();

		for (int c = 0; c < deletedCars; c++)
			insertCar();

	}

	public void insertCar() {
		List<Cell> entryCells = map.getEntryCells();
		Random generator = new Random();
		Cell randomEntryCell = entryCells.get(generator.nextInt(entryCells.size()));
		Cars.add(new Car(new Point(randomEntryCell.getCoordinates())));
	}

	public void insertPolice() {
		List<Cell> entryCells = map.getEntryCells();
		Random generator = new Random();
		Cell randomEntryCell = entryCells.get(generator.nextInt(entryCells.size()));
		Polices.add(new Police(new Point(randomEntryCell.getCoordinates())));
	}	

	public void run(int time) {
		runThread = new RunThread(time);
		runThread.start();
		displayCars();
	}

	public void reset() {
		initialize();
		displayCity();
		displayCars();	
	}

	public void step() {
		removeCars();
		for(Car a : Cars) a.go(map);
		displayCars();
	}

	public void stop() {
		runThread.interrupt();
		runThread.terminate();
		displayCars();
	}

	public void displayCity(){
		GUI.displayCity(this);
	}

	public void displayCars(){
		GUI.displayCars(this);
	}
	
	public void removeCars(){
		GUI.removeCars(this);
	}
}