
package com.simularity.test;

import com.simularity.optimizers.*;

public class LocTest {
	static Evaluator eval = (particle) -> {
		
		double v1 = (particle[0] + Math.random() * 2.0 - 1.0) - 15.0;
		double v2 = (particle[1] + Math.random() * 2.0 - 1.0) - 15.0;
		v1 *= v1;
		v2 *= v2;
		return v1 + v2;
	};

	public static void main(String [] args) {
		double [] mins = {-10000.0, -10000.0};
		double [] maxs = {10000.0, 10000.0};
		PSO pso = new PSO(mins, maxs, 0.7, 1.8, 1.8);
		PSO.Swarm swarm = pso.new Swarm(20, eval);
		for (int i = 0; i < 100000; i++) {
			System.out.printf("Iteration %d = %f\n", i, swarm.iteration());
		}
		double [] res = swarm.getBest();
		System.out.printf("Results: %f %f\n", res[0], res[1]);
	}
	

}
