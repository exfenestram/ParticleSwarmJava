
package com.simularity.test;

import com.simularity.optimizers.*;

public class LocTest {
	static Evaluator eval = (particle) -> {
		
		double v1 = particle[0] - 15.0;
		double v2 = particle[1] - 15.0;
		v1 *= v1;
		v2 *= v2;
		return v1 + v2;
	};

	public static void main(String [] args) {
		double [] mins = {-100000.0, -100000.0};
		double [] maxs = {100000.0, 100000.0};
		PSO pso = new PSO(mins, maxs);
		PSO.Swarm swarm = pso.new Swarm(20, eval, null, 0.7, 1.5, 1.0);
		for (int i = 0; i < 100000; i++) {
			System.out.printf("Iteration %d = %f\n", i, swarm.iteration());
		}
		double [] res = swarm.getBest();
		System.out.printf("Results: %f %f\n", res[0], res[1]);
	}
	

}
