package my.demo.test.game;

import java.util.Scanner;

public class TestGreedy {
	public static void main(String[] args) {
		int n = 0; 
		
		System.out.println("Enter a num:");
		Scanner scaner = new Scanner(System.in);
		
		n = scaner.nextInt();
		scaner.close();
		
		TestGreedy test = new TestGreedy();
		
		test.test(n);
	}
	
	void test(int n) {
//		test1(n);
		test2(n);
	}
	
	void test1(int money) {
		int[] coins = new int[] {25, 10, 5, 1}; 
		GreedyGame game = new GreedyGame(money, coins); 
		game.calc();
	}
	
	void test2(int n) {
		EmpressGame game = new EmpressGame(n);
		game.calc();
	}
}

class GreedyGame {
	int dim;
	int money;
	int[] coins;
	
	public GreedyGame(int money, int[] coins) {
		dim = coins.length;
		this.coins = coins;
		this.money = money;
	}
	
	void calc() {
		int[][] mark = new int[money + 1][dim];
		int[] result = new int[money + 1];
		
		for (int i=1; i<=money; ++i) {
			int r = 0;
			int d = 0; 
			int min = i + 1;
			
			for (int j=0; j<dim; ++j) {
				if (coins[j] <= i) {
					int k = i - coins[j]; 
					int val = result[k] + 1;
					
					if (val < min) {
						min = val; 
						r = k;
						d = j;
					}
				}
			}
			
			result[i] = min;
			System.arraycopy(mark[r], 0, mark[i], 0, dim);
			++mark[i][d];
		}
		
		System.out.println("min=" + result[money]);
		for (int i: mark[money]) {
			System.out.print(" " + i);
		}
	}
}

class EmpressGame {
	private int cnt;
	private int n;
	private int[] x;
	
	EmpressGame(int n) {
		this.n = n;
		x = new int[n];
	}
	
	boolean canPlace(int i) {
		for (int j=0; j<i; ++j) {
			if (x[i] == x[j] || i + x[i] == j + x[j]
					|| i - x[i] == j - x[j]) {
				return false;
			}
		}
		
		return true;
	}
	
	void tryRow(int i) {
		if (i < n) {
			for (int j=0; j<n; ++j) {
				x[i] = j;
				
				if (canPlace(i)) {
					tryRow(i+1);
				} 
			}
		} else {
			++cnt;
			
			System.out.printf("[%d]positions:\n", cnt);
			for (int r=0; r<n; ++r) {
				for (int c=0; c<n; ++c) {
					if ( c != x[r]) {
						System.out.printf(" %c", 'O');
					} else {
						System.out.printf(" %c", '@'); 
					}
				}
				
				System.out.println(); 
			}
			
			System.out.println(); 
		}
	}
	
	void calc() {
		cnt = 0;
		
		tryRow(0);
	}
}

