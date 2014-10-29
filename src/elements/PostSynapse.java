package elements;

import generic.Location;
import generic.Log;
import generic.Utils;
import helpers.GapJunctionGroup;

import java.util.ArrayList;

import controller.Worm;

/**
 * A post-synapse. Attached to a Dendrite. Recieves fire from PreSynapse.
 * @author Andrew W.E. McDonald
 *
 */
public class PostSynapse {

	Location location;
	int post_synapse_num; // *relative to associated_dendrite* (so, always 0, while each dendrite only has 1 post synapse)
	public ArrayList<GapJunctionGroup> synapse_groups;
	private int neuron_sleep_cycles_at_previous_firing = 0;
	private int neuron_sleep_cycles_now;
	private static int DESENSITIZATION_FREQUENCY_THRESHOLD = Worm.POSTSYNAPTIC_DESENSITIZATION_FREQUENCY_THRESHOLD;
	

	public Dendrite associated_dendrite;
	
	/**
	 * increases as a result of LTP (long term potentiation)
	 * ... and decreases as a result of LTD (long term depression)
	 */
	public double strength_multiplier;
	
	/**
	 * the desensitization of this synapse 
	 */
	public double desensitization = 0;
	
	/**
	 * whether or not the associated neuron just fired.
	 */
	public boolean neuron_just_fired = false;

	/**
	 * Time window where if a pre-synapse fires, and the post-synaptic neuron fires, 
	 * the pre-synapse and post-synapse pair in question will be strengthened.
	 */
	public double timeout;
	/**
	 * when a pre-synapse fires, this gets set to Synapse.timeout
	 * each Neuron.leak_time_constant amount of time, some value (at this point it's just going to be leak_time_constant)
	 * will be subtracted. if current_window_size gets to 0 before the post-synaptic neuron fires, then the synapse in question 
	 * is not strengthened.
	 */
	public double current_window_size = 0;


	
	/**
	 * 
	 * @param post_synapse_num
	 * @param timeout -- synapse strengthening window of opportunity. seems to be 100-200 ms (at least in rats..)
	 * @param strength_multiplier
	 */
	public PostSynapse(int post_synapse_num, double timeout, double strength_multiplier){
		this.post_synapse_num = post_synapse_num;
		this.timeout = timeout;
		this.strength_multiplier = strength_multiplier;
		synapse_groups = new ArrayList<GapJunctionGroup>(100);
	}

	public PostSynapse(Location l, int post_synapse_num, double timeout, double strength_multiplier){
		location = l;
		this.timeout = timeout;
		this.strength_multiplier = strength_multiplier;
		this.post_synapse_num = post_synapse_num;
		synapse_groups = new ArrayList<GapJunctionGroup>(100);
	}	


	/**
	 * receives fire from a pre-synapse
	 * 
	 * this is a bad method.. right now, the preneuron's thread actually calls this...
	 * 
	 * @param v_fire
	 */
	public synchronized void receive_fire(double v_fire){
		for (String s:Worm.touch_neurons){
			if (Utils.starts_with(this.associated_dendrite.associated_neuron.name, s)){
				//System.out.println("(synaptic event) pre -> post: "+synapse_groups.get(0).pre_synapse.associated_neuron.name+" -> "+this.associated_dendrite.associated_neuron.name+" SM: "+strength_multiplier);
				//Log.out("(synaptic event) pre -> post: "+synapse_groups.get(0).pre_synapse.associated_neuron.name+" -> "+this.associated_dendrite.associated_neuron.name+" SM: "+strength_multiplier);
				Log.err("(receive fire) -- desensitization: "+desensitization+" -- from: "+synapse_groups.get(0).pre_synapse.associated_neuron.name+" -> "+this.associated_dendrite.associated_neuron.name+" SM: "+strength_multiplier);
				break;
			}
		}
		associated_dendrite.associated_neuron.add_v(v_fire*strength_multiplier*(1-desensitization));
		if (should_desensitize()){
			/*
			 * So we start off with 0, and then add Worm.Desensitization_Increment as the first desensitization.
			 * 
			 * Then, for the second, we take what is there, and add to it some percentage of what percent hasn't been desensitized... 
			 * this way, we prevent ourselves from reaching a desensitization of 1, and things flatten out instead of increasing without bound.
			 */
			desensitization = desensitization + (1 - desensitization)*Worm.DESENSITIZATION_PERCENT;
		}
	}
	
	/**
	 * determines whether or not this PostSynapse should desensitize itself to the stimulus that caused the call to 'receive_fire'
	 * 
	 * based upon the fequency of firing
	 * @return
	 */
	public boolean should_desensitize(){
		return false;
		/*
		neuron_sleep_cycles_now = this.associated_dendrite.associated_neuron.sleep_cycles;
		if (( neuron_sleep_cycles_now - neuron_sleep_cycles_at_previous_firing) < DESENSITIZATION_FREQUENCY_THRESHOLD){
			//Log.err("DESENSITIZE: "+neuron_sleep_cycles_now+", "+neuron_sleep_cycles_at_previous_firing+" => "+(neuron_sleep_cycles_now - neuron_sleep_cycles_at_previous_firing));
			neuron_sleep_cycles_at_previous_firing = neuron_sleep_cycles_now;
			return true;
		}
		else {
			//Log.err("LEAVE BE: "+neuron_sleep_cycles_now+", "+neuron_sleep_cycles_at_previous_firing+" => "+(neuron_sleep_cycles_now - neuron_sleep_cycles_at_previous_firing));
			neuron_sleep_cycles_at_previous_firing = neuron_sleep_cycles_now;
			return false;
		}
		*/
	}

	/**
	 * NOTE: this only prints out the FIRST synapse group.. if there is more than one, it isn't printed... 
	 * something to look into modifying later on when this might be a problem.
	 */
	public String toString(){
		return "[PostSynapse #"+post_synapse_num+
				" Neuron: "+associated_dendrite.associated_neuron.name+
				" Presynaptic Neuron: "+synapse_groups.get(0).pre_synapse.associated_neuron.name+
				" :: strength multiplier = "+strength_multiplier+", timeout = "+timeout+", desensitization = "+desensitization+
				", current window size = "+current_window_size+
				", dendrite #"+associated_dendrite.dendrite_num+", neuron #"+associated_dendrite.associated_neuron.neuron_number+"]";
	}

}
