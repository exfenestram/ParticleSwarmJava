package com.simularity.test;


import com.simularity.optimizers.*;

public class XORTest {

	
	
	static private Evaluator eval = (particle) -> {
		// Create an XOR processor from the particle
		XOR xor = new XOR(particle, 0.1);
		return xor.evaluate(20);
	};
	
	public static void main(String [] args) {
		int order = 4 * 5; // Particle with a hidden layer of 3 neurons
		double [] mins = new double[order];
		double [] maxs = new double[order];
		for (int i = 0; i < order; i++) {
			mins[i] = -100.0;
			maxs[i] = 100.0;
		}

		PSO pso = new PSO(mins, maxs);
		PSO.Swarm swarm = pso.new Swarm(20, eval, null, 0.3, 0.3, 0.3);

		for (int i = 0; i < 1000; i++) {
			System.out.printf("Iteration %d - Error: %f\n", i, swarm.iteration());
		}

		XOR last = new XOR(swarm.getBest(), 0.2);
		last.show();
		
	}
}
