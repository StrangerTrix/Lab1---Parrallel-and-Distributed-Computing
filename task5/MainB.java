import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainB {

	public static class Philosopher implements Runnable {
		private final int id;
		private final Lock leftChopStick;
		private final Lock rightChopStick;

		public Philosopher(int id, Lock leftFork, Lock rightFork) {
			this.id = id;
			this.leftChopStick = leftFork;
			this.rightChopStick = rightFork;
		}

		@Override
		public void run() {
			while (true) {
				try {
					this.think();
					this.eat();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		private void think() throws InterruptedException {
			System.out.println("Philosopher n°"+this.id+" is thinking.");
			Thread.sleep(1);
		}

		private void eat() throws InterruptedException {

			if (this.waitingForCutlery()) {
				System.out.println("Philosopher n°" + this.id + " is eating.");
				Thread.sleep(1);
				this.putDownCutlery();
			}
		}

		private boolean waitingForCutlery() {
			if (this.leftChopStick.tryLock()) {
				System.out.println("Philosopher n°" + this.id + " picked up left chopstick.");
				if (this.rightChopStick.tryLock()) {
					System.out.println("Philosopher n°" + this.id + " picked up right chopstick.");
					return true;
				} else {
					this.leftChopStick.unlock();
					System.out.println("Philosopher n°" + this.id + "  put down left chopstick.");
				}
			}
			return false;
		}


		private void putDownCutlery() {
			this.leftChopStick.unlock();
			System.out.println("Philosopher n°" + id + " put down left chopstick.");
			this.rightChopStick.unlock();
			System.out.println("Philosopher n°" + id + " put down right chopstick.");
		}

	}
	public static void run(int numberOfPhilosophers) {
		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		Lock[] chopsticks = new Lock[numberOfPhilosophers];

		/*Initialize the chopsticks*/
		for (int i = 0; i < numberOfPhilosophers; i++) {
			chopsticks[i] = new ReentrantLock();
		}

		/*Initialize the philosophers*/
		for (int i = 0; i < numberOfPhilosophers; i++) {
			Lock leftChopstick = chopsticks[i];
			Lock rightChopstick = chopsticks[(i + 1) % numberOfPhilosophers];
			philosophers[i] = new Philosopher(i+1, leftChopstick, rightChopstick);
		}

		/*Launch the threads*/
		for (int i = 0; i < numberOfPhilosophers; i++) {
			new Thread(philosophers[i]).start();
		}
	}
	public static void main(String [] args) {
		run(5);
	}
}

