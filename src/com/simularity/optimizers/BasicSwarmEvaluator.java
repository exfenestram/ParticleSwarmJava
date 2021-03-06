// BasicSwarmEvaluator.java - Linear evaluation of all the particles in a Swarn
// Copyright 2018, Simularity, Inc.

/*
MIT License

Copyright (c) 2018 Simularity, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.simularity.optimizers;

import java.util.Map;
import java.util.HashMap;

/**
 * Evaluate all particles an return a map of Particle ID -> Value
 */


public class BasicSwarmEvaluator implements SwarmEvaluator{
	private PSO.Swarm swarm;
	/**
	 */
	public BasicSwarmEvaluator(PSO.Swarm swarm) {
		this.swarm = swarm;
	}
	
	public Map<Integer, Double>  evaluate(Iterable<Integer> particles) {
		Map<Integer, Double> res = new HashMap<Integer, Double>();
		for (Integer particle : particles) {
			int p = particle.intValue();
			double [] pos = swarm.getParticle(p);
			if (pos == null) {
				throw new IllegalArgumentException(String.format("BasicSwarmEvaluator: position %d not valid", p));
			}				   
			Double val = new Double(swarm.evaluate(pos));
			res.put(particle, val);
		}
		return res;
	}
}
