package com.simularity.test;

public class XOR {
	private double [] weights;
	private double noise;
	private int hidden_size;

	private double [][] input = {{1.0, 1.0, 1.0, 0.0},
				   {1.0, 0.0, 1.0, 1.0},
				   {0.0, 1.0, 1.0, 1.0},
				   {0.0, 0.0, 1.0, 0.0}};


	public XOR(double [] weights, double noise) {
		this.weights = weights;
		this.noise = noise;
		this.hidden_size = weights.length / 4;
		if (hidden_size == 0) {
			throw new IllegalArgumentException("XOR - No hidden layer");
		}
	}

	public double operate(double [] in) {
		double [] tmp = new double[hidden_size];
		// curr is the index of the weights array. We just step through it
		int curr = 0;

		// Activate the hidden layer
		for (int i = 0; i < hidden_size; i++) {
			tmp[i] = 0.0;
			for (int j = 0; j < 3; j++) {
				tmp[i] += in[j] * weights[curr++];
			}
		}

		// Now activate the output layer
		double res = 0.0;
		for (int i = 0; i < hidden_size; i++) {
			res += Math.tanh(tmp[i]) * weights[curr++];
		}

		res = Math.tanh(res);
/*
		System.out.printf("[ ");
		for (int i = 0; i < 2; i++) {
			System.out.printf("%f ", in[i]);
		}
		System.out.printf("]: ");
		System.out.printf("%f\n", res);
*/
		return res;
	}

	

	private double adjust(double v) {
		if (v >= 0.5) {
			return 1.0;
		}
		else {
			return 0.0;
		}
	}
	private double compute(double [] in) {
		double v0 = adjust(in[0]);
		double v1 = adjust(in[1]);
		double sum = v0 + v1;
		if (sum < 0.9 || sum > 1.1) {
			return 0.0;
		}
		return 1.0;
	}

	private double [] genInput() {
		double [] res = new double[3];
		for (int i = 0; i < 2; i++) {
			res[i] = adjust(Math.random()) + ((Math.random() * 2.0 - 1.0) * noise);
		}
		// the bias
		res[2] = 1.0;
		return res;
	}
		
	public double evaluate(int n) {
		double res = 0.0;
		/*
		for (int i = 0; i < n; i++) {
			double [] in = genInput();
			double err = compute(in) - operate(in);
			err *= err;
			res += err;
		}
		
		return res / n;
		*/

		for (int i = 0; i < 4; i++) {
			double [] in = input[i];
			double err = in[3] = operate(in);
			err *= err;
			res += err;
		}
		return res / 4.0;
	}

	public void show() {
		for (int i = 0; i < 4; i++) {
			System.out.printf("[%f,%f]:%f\n", input[i][0], input[i][1],
					  operate(input[i]));
		}
	}
}
