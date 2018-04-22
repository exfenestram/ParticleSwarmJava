// PSO.java - General Purpose Particle Swarm Optimization
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

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * PSO performs the Particle Swarm Optimization. Some number of "particles"
 * (double [] of a fixed length, are deployed, where each particle encodes a 
 * possible solution. Particles can be thought of as encoding a location in
 * K-dimensional space, where K is the fixed length of a particle. The 
 * algorithm moves the particles through space by evaluating the particle. 
 * Evaluating the particle turns the location into a score which is (typically)
 * minimized
 */

public class PSO {
	/**
	 * The dimensionality of this Map
	 */
	
	private int order;
	/**
	 * The minimum boundary for each particle dimension
	 */
	protected final double [] mins;
	/**
	 * The maximum boundary for each particle dimension
	 */
	protected final double [] maxs;


	/**
	 * Construct a PSO field. This generates the Map and initialized the parameters
	 * but does not actually execute the algorithm
	 * @param mins The minimum map coordinates for each dimension of the search
	 * @param maxs The maximum map coordinates for each dimension of the search
	 */
	
	public PSO(double [] mins, double [] maxs) {

		this.mins = mins;
		this.maxs = maxs;

		if (this.mins.length != this.maxs.length) {
			throw new IllegalArgumentException("PSO - mins and maxs must be the same length");
		}

		this.order = this.mins.length;
		
	}

	/**
	 * This class actually implements the Particle Swarm. The Containing class is primarily
	 * for containing the map parameters (extents, algorithm tuning, etc.) while this class
	 * contains the swarm information (Particle locations, velocities, etc.) and manages 
	 * the evaluation of the position of each parameter
	 */
	public class Swarm {
		/**
		 * The number of particles in this search
		 */
		
		private int swarm_order;

		/**
		 * Array of positions of the particle
		 * The particle is IDed by index of this array
		 */
		private double [][] positions;
		/**
		 * Velocity of particles - uses same particle index as positions
		 */
		private double [][] velocity;

		/**
		 * Best Position of this particle
		 */
		private double [][] best;

		/**
		 * Current Score of this particle
		 */
		private double [] curr_score;

		/**
		 * Best score of this particle
		 */
		private double [] best_score;

		/**
		 * The evaluator of this swarm
		 */
		private Evaluator evaluator;
		/** 
		 * The Current index of the leader
		 */
		private int leader;

		/**
		 * The best known position
		 */

		private double [] best_pos;

		/**
		 * The score of the best known pos
		 */

		private double top_score;

		/** 
		 * The Omega parameter
		 */
		protected double omega;
		
		/**
		 * The Phi p parameter
		 */
		protected double phi_p;
		
		/**
		 * The Phi g parameter
		 */
		protected double phi_g;
		
		
		/**
		 * Construct a Swarm
		 * @param n_particles The number of particles in this swarm
		 * @param Evaluator The evaluator for this swarm
		 * @param omega The PSO Omega parameter
		 * @param phi_p The PSO Phi P parameter
		 * @param phi_g The PSO Phi G parameter
		 
		 */

		public Swarm(int n_particles, Evaluator evaluator,
			     double omega, double phi_p, double phi_g) {
			// We need at least 2 particles
			if (n_particles <= 1) {
				throw new IllegalArgumentException("PSO.Swarm - Particle count must be greater than 1");
			}
			swarm_order = n_particles;

			this.omega = omega;
			this.phi_p = phi_p;
			this.phi_g = phi_g;
			
			positions = new double[n_particles][];
			velocity = new double[n_particles][];
			best = new double[n_particles][];
			curr_score = new double[n_particles];
			best_score = new double[n_particles];
			for (int i = 0; i < swarm_order; i++) {
				positions[i] = init_position();
				velocity[i] = init_velocity();
				setBest(i);
			}
			this.evaluator = evaluator;
			// Do an initial evaluation. This populates leader
			evaluate(true);
		}

		/** 
		 * Set the Best position
		 */
		private void setBest(int particle) {
			if (best[particle] == null) {
				best[particle] = new double[order];
			}
			for (int i = 0; i < order; i++) {
				best[particle][i] = positions[particle][i];
			}
			best_score[particle] = curr_score[particle];
		}

		/**
		 * Set a position randomly to a place in the map
		 */

		private double [] init_position() {
			double [] result = new double[order];
			for (int i = 0; i < order; i++) {
				result[i] = mins[i] + Math.random() * (maxs[i] - mins[i]);
			}
			return result;
		}

		/**
		 * This currently sets the velocity to zero
		 * Other values could be legit. 
		 */
		private double [] init_velocity() {
			double [] result = new double[order];
			for (int i = 0; i < order; i++) {
				result[i] = 0.0;
			}

			return result;
		}

		/**
		 * Evaluate all particles and set leader to the max or min value
		 */
		
		private void evaluate(boolean first) {
			BiPredicate<Double, Double>  replace;
			if (evaluator.minimize()) {
				replace = (a, b) ->  {
					return a.compareTo(b) < 0;
				};
			}
			else {
				replace = (a, b) -> {
					return a.compareTo(b) > 0;
				};
			}
		
			// Initialize our results to zero
			curr_score[0] = evaluator.evaluate(positions[0]);
			if (first || replace.test(new Double(curr_score[0]), new Double(best_score[0]))) {
				setBest(0);
			}
			leader = 0;

			for (int i = 1; i < swarm_order; i++) {
				double val = evaluator.evaluate(positions[i]);
				Double aval = new Double(val);
				if (first || replace.test(aval, new Double(best_score[i]))) {
					setBest(i);
				}
				if (first || replace.test(aval, new Double(best_score[leader]))) {
					leader = i;
					best_score[i] = val;
				}
			}
			
			if (first || replace.test(new Double(best_score[leader]), new Double(top_score))){
				top_score = best_score[leader];
				best_pos = positions[leader];
			}
					
		}
		
		private void update_velocity(int particle) {
			double [] vel = velocity[particle];
			double rp = Math.random();
			double rg = Math.random();
			
			
			for (int i = 0; i < vel.length; i++) {
				double best_dim = best[particle][i];
				double part = positions[particle][i];
				double lead = best_pos[i];
				vel[i] = omega * vel[i] + phi_p * rp * (best_dim -  part) + phi_g * rg * (lead - part);
			}
		}
		private void update_position(int particle) {
			double [] pos = positions[particle];
			double [] vel = velocity[particle];
			for (int i = 0; i < pos.length; i++) {
				pos[i] += vel[i];
			}
		}
		public double iteration() {
		
			for (int i = 0; i < swarm_order; i++) {
				update_velocity(i);
			}
			for (int i = 0; i < swarm_order; i++) {
				update_position(i);
			}
			evaluate(false);
			return top_score;
		}

		public double [] getBest() {
			return best[leader];
		}
	}
}
			
		
