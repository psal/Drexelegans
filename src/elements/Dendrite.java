package elements;

import generic.Location;

import java.util.ArrayList;

import controller.Worm;

/**
 * A horrible model of a dendrite. 
 * @author Andrew W.E. McDonald
 *
 */
public class Dendrite {
	
	/**
	 * specifies the location of the tip of the dentrite. This is obviously hideously inaccurate, 
	 * but for our initial purposes, should be fine. 
	 */
	Location tip;

	int dendrite_num; // relative to neuron


	public ArrayList<PostSynapse> post_synapses;
	public Neuron associated_neuron;
	
	public Dendrite(int dendrite_num){
		tip = null;
		this.dendrite_num = dendrite_num;
		post_synapses = new ArrayList<PostSynapse>(Worm.MAX_SYNAPSES_PER_DENDRITE);
	}
	
	
	public Dendrite(Location l, int dendrite_num){
		tip = l;
		this.dendrite_num = dendrite_num;
		post_synapses = new ArrayList<PostSynapse>(Worm.MAX_SYNAPSES_PER_DENDRITE);
	}
	
	public void add_post_synapse(PostSynapse s){
		// for now, we have 1 post-synapse per dendrite
		post_synapses.add(s);
		post_synapses.get(0).associated_dendrite = this;
	}
	
}
