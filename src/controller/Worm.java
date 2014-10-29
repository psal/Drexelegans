/**
 * 
 */
package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import elements.Dendrite;
import elements.Neuron;
import elements.PostSynapse;
import elements.PreSynapse;
import generic.Log;
import generic.NeuralDatabaseManager;
import generic.Utils;
import generic.WormwiringDatabaseManager;
import helpers.GapJunctionGroup;

/**
 * The worm. 
 * 
 * @author Andrew W.E. McDonald
 *
 */
public class Worm {

	public ArrayList<Neuron> neurons = new ArrayList<Neuron>(500); // there aren't 500 neurons.. but i hate growing ArrayLists.. so what the hell.
	
	// sensory: ALMR, ALML, PLMR, PLML, AVM, PVM 
	// inter: AVA, AVB, PVC, AVD, LUA
	// motor: VA, DA, VB, DB, VD, DD
	public static String[] touch_neurons = new String[]{"AVD","LUA", "PLM", "AVA", "PVC","AVE", "AVB", "ALM", "AVM", "VA", "DA","VB","DB","VD","VC","DD"};
	public static String[] motor_neurons = new String[]{"VA", "DA", "VB", "DB", "VD", "DD"};
	public static final int POSTSYNAPTIC_DESENSITIZATION_FREQUENCY_THRESHOLD = 5; // just guessing
    public static final double DESENSITIZATION_PERCENT = .01; // just guessing
	
	/**
	 * parameters for new neurons
	 */
	public static final int MAX_POST_SYNAPSES = 20; // (on a single neuron) just a guess at the moment
	public static final int MAX_DENDRITES = MAX_POST_SYNAPSES; // for now, let's just say one post synapse per dendrite.. probably wrong.
	public static final int MAX_SYNAPSES_PER_DENDRITE = 1;
	public static final int CS = 0; // chemical synapse
	public static final int GJ = 1; // gap junction
	public static final int TRIADIC = 3;
	public static final int DIADIC = 2;
	public static final int MONADIC = 1;
	public static Utils utils;


	public static void main( String[] args){
		utils = new Utils();
		Worm worm = new Worm();
		// build the circuit
		worm.create_touch_withdrawal_circuit();
		// start neurons
		worm.start_neurons();
		// touch a sensory neuron
		//int neuron_index = worm.get_neuron_index("ALML");
		int neuron_index = worm.get_neuron_index("PLML");
		//System.out.println("ALML at: "+ALML_index);
		Neuron to_fire = worm.neurons.get(neuron_index);
		int count = 0;
		boolean keep_going = true;
		Scanner in = new Scanner(System.in);
		while (keep_going){
			Log.out("*---=[ STARTING ROUND "+count+" ]=---*");
			// now we need to "stimulate" the neuron via some signal
			for (int i = 0; i < 20; i++){
				to_fire.fire();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					Log.err("I was sleeping... what happened??");
				}
			}

			// then we let it do it's thing, and start over after the target has set "OldMain.restart" to true.
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				Log.err("I was sleeping... what happened??");
			}
			utils.flush_writer();
			Log.out("Finished round "+count);
			count++;	
			Log.out("Again?");
			String answer = in.nextLine();
			if (!answer.equals("")){
				keep_going = false;
			}

		}



	}


	public void create_touch_withdrawal_circuit(){
		double ltp_time_window = Neuron.leak_time_constant*3; // at the moment, Neurons sleep for 10 ms before checking for fire, so, give it 3x that.
		
		try {
			WormwiringDatabaseManager wdm = new WormwiringDatabaseManager();
			for (String s : touch_neurons){
				ResultSet current = wdm.select_records(s);
				/*
				 * NOTE: 'post_1' is not the same as 'post1'!!!!!!!!!!!!!!!
				 * post_1 is the *name* of the first postsynaptic neuron!
				 * post1 is the Neuron object, with name 'post_1'.
				 * 
				 * "Why did you do this, Andrew?", I can feel either future Andrew or someone else thinking...
				 * 
				 * The answer: post1 was the second most reasonable variable name for the Neuron object (post_1 was first). 
				 * As post_1 was taken, and as I didn't want to rename post_1 to post_1_name, I left this note.
				 * 
				 * And of course, now you think, "In the time it took you to type this out, you could have just changed the damn variable names...".
				 * 
				 * Yes, I could have. I didn't plan on typing out such a long comment. And now I'm sure as hell not spending more time changing variable names.
				 */
				while (current.next()){
					boolean diadic = false;
					boolean triadic = false;
					String name = current.getString(2);
					String description = current.getString(3);
					String post_1 = current.getString(4);
					String post_2 = current.getString(5);
					String post_3 = current.getString(6);
					if (post_2.length() != 0){
						diadic = true;
					}
					if (post_3.length() != 0){
						diadic = false;
						triadic = true;
					}
					int num_synapses = current.getInt(7);
					int num_sections = current.getInt(8);
					/*
					 * NOTE: Definitely not the right approach. going to leave the num_sections out of it until I introduce nerotransmitters.
					 * 
					 * start everything off with neutral connection strength, 
					 * but take into account synapses with more sections.
					 * 
					 * because i have no idea about this, im assuming that a synapse that requires 2 EM sections,
					 * as opposed to one that requires only 1 EM section to see, won't double the synapse strength...
					 * rather, it will increase it by some amount... im making that amount .15
					 * 
					 * so, a synapse with 1 section, will start off with a strength multiplier of 1 + (1 - 1)*.15 = 1,
					 * and a synapse with 3 sections will start off with a strength multiplier of 1 + (3 - 1)*.15 = 1.3
					 * 
					 * i subtract 1 from the number of synapses so that we don't count the initial section,
					 * which all synapses have (obviously two synapses that use 1 section may not be the same size, 
					 * as one may use less of a section than another, but, what can we do?)
					 */
					//double synapse_strength_multiplier = 1 + num_sections*.15; // NOTE: definitely not the right approach
					double synapse_strength_multiplier = 4; // NOTE: definitely not the right approach
					int synapse_type_num = current.getInt(9);
					int syn_type = -1;
					String s_type = "";
					if (synapse_type_num == 1){
						syn_type = Worm.CS;
						s_type = "chemical synapse";
					} else {
						syn_type = Worm.GJ;
						s_type = "gap junction";
					}
					boolean is_relevant = false;
					// initially, we don't want to have too many neurons, so we are limiting the worm to those in the touch-withdrawal circuit
					for (String n:touch_neurons){
						if (Utils.starts_with(post_1,n) || Utils.starts_with(post_2,n) || Utils.starts_with(post_3,n)){
							is_relevant = true;
							break;
						}
					}
					if (! is_relevant){
						continue;// so if none of the postsynaptic neurons are in our list, ignore this result. 
					}
					//System.out.println(name+"->"+post_1+", "+post_2+","+post_3);
			
					// find out if we already have neurons for the 
					int pre_index = get_neuron_index(name);
					int post_1_index = -2;
					int post_2_index = -2; 
					int post_3_index = -2;
					if (triadic){
						post_1_index = get_neuron_index(post_1);
						post_2_index = get_neuron_index(post_2);
						post_3_index = get_neuron_index(post_3);
					}
					else if (diadic){
						post_1_index = get_neuron_index(post_1);
						post_2_index = get_neuron_index(post_2);
					}
					else {
						post_1_index = get_neuron_index(post_1);
					}
					// begin temp printing code
					/*
					if (triadic){
						System.out.println(name+" ("+get_neuron_index(name)+") -> "+post_1+", "+post_2+", "+post_3+" -- "+s_type);
					} else if (diadic){
						System.out.println(name+" ("+get_neuron_index(name)+") -> "+post_1+", "+post_2+" -- "+s_type);
					} else {
						System.out.println(name+" ("+get_neuron_index(name)+") -> "+post_1+" -- "+s_type);
					}
					*/
					// end temp printing code
					Neuron pre = null;
					Neuron post1 = null;
					Neuron post2 = null;
					Neuron post3 = null;
					// the presynapstic neuron
					if (pre_index == -1){ // create a new neuron
						int num_neurons = neurons.size();
						pre = new Neuron(name, num_neurons);
						neurons.add(pre);
						pre.dendrites = new ArrayList<Dendrite>(MAX_DENDRITES);

					}
					else{
						pre = neurons.get(pre_index);
					}
					// first postsynaptic neuron
					if (post_1_index == -1){ // create a new neuron
						int num_neurons = neurons.size();
						//System.out.println("Creating neuron named '"+post_1+"'...");
						post1 = new Neuron(post_1, num_neurons);
						neurons.add(post1);
						post1.dendrites = new ArrayList<Dendrite>(MAX_DENDRITES);
					}
					else if (post_1_index != -2){
						post1 = neurons.get(post_1_index);
					}
					// second postsynaptic neuron
					if (post_2_index == -1){ // create a new neuron
						int num_neurons = neurons.size();
						//System.out.println("Creating neuron named '"+post_2+"'...");
						post2 = new Neuron(post_2, num_neurons);
						neurons.add(post2);
						post2.dendrites = new ArrayList<Dendrite>(MAX_DENDRITES);
					}
					else if (post_2_index != -2){
						post2 = neurons.get(post_2_index);
					}
					// third postsynaptic neuron
					if (post_3_index == -1){ // create a new neuron
						int num_neurons = neurons.size();
						//System.out.println("Creating neuron named '"+post_3+"'...");
						post3 = new Neuron(post_3, num_neurons);
						neurons.add(post3);
						post3.dendrites = new ArrayList<Dendrite>(MAX_DENDRITES);
					}
					else if (post_3_index != -2){
						post3 = neurons.get(post_3_index);
					}
					// now we can make the connection between pre and post
					// note that there is only one postsynapse per dendrite, so we know that any postsynapse will be at index 0 of its dendrite (for the time being).
					for (int i = 0; i < num_synapses; i++){
						if (triadic){
							// post 1
							int p1_dend_ct =  post1.dendrites.size();
							post1.dendrites.add(p1_dend_ct, new Dendrite(p1_dend_ct));
							post1.dendrites.get(p1_dend_ct).associated_neuron = post1;
							post1.dendrites.get(p1_dend_ct).add_post_synapse(new PostSynapse(0, ltp_time_window, synapse_strength_multiplier));// 0 because 1 post synapse per dendrite at the moment
							// post 2
							int p2_dend_ct =  post2.dendrites.size();
							post2.dendrites.add(p2_dend_ct, new Dendrite(p2_dend_ct));
							post2.dendrites.get(p2_dend_ct).associated_neuron = post2;
							post2.dendrites.get(p2_dend_ct).add_post_synapse(new PostSynapse(0, ltp_time_window, synapse_strength_multiplier));// 0 because 1 post synapse per dendrite at the moment
							// post 3
							int p3_dend_ct =  post3.dendrites.size();
							post3.dendrites.add(p3_dend_ct, new Dendrite(p3_dend_ct));
							post3.dendrites.get(p3_dend_ct).associated_neuron = post3;
							post3.dendrites.get(p3_dend_ct).add_post_synapse(new PostSynapse(0, ltp_time_window, synapse_strength_multiplier));// 0 because 1 post synapse per dendrite at the moment
							// now we deal with pregaps
							int pre_syn_ct = pre.pregaps.size();
							pre.pregaps.add(pre_syn_ct,new PreSynapse(pre_syn_ct, syn_type, Worm.TRIADIC));
							pre.pregaps.get(pre_syn_ct).associated_neuron = pre;
							GapJunctionGroup sg = new GapJunctionGroup(pre.pregaps.get(pre_syn_ct), post1.dendrites.get(p1_dend_ct).post_synapses.get(0), post2.dendrites.get(p2_dend_ct).post_synapses.get(0), post3.dendrites.get(p3_dend_ct).post_synapses.get(0), syn_type);
							pre.pregaps.get(pre_syn_ct).synapse_groups.add(sg);
							post1.dendrites.get(p1_dend_ct).post_synapses.get(0).synapse_groups.add(sg);
							post2.dendrites.get(p2_dend_ct).post_synapses.get(0).synapse_groups.add(sg);
							post3.dendrites.get(p3_dend_ct).post_synapses.get(0).synapse_groups.add(sg);
						}
						else if (diadic){
							// post 1
							int p1_dend_ct =  post1.dendrites.size();
							post1.dendrites.add(p1_dend_ct, new Dendrite(p1_dend_ct));
							post1.dendrites.get(p1_dend_ct).associated_neuron = post1;
							post1.dendrites.get(p1_dend_ct).add_post_synapse(new PostSynapse(0, ltp_time_window, synapse_strength_multiplier));// 0 because 1 post synapse per dendrite at the moment
							// post 2
							int p2_dend_ct =  post2.dendrites.size();
							post2.dendrites.add(p2_dend_ct, new Dendrite(p2_dend_ct));
							post2.dendrites.get(p2_dend_ct).associated_neuron = post2;
							post2.dendrites.get(p2_dend_ct).add_post_synapse(new PostSynapse(0, ltp_time_window, synapse_strength_multiplier));// 0 because 1 post synapse per dendrite at the moment
							// now we deal with pre
							int pre_syn_ct = pre.pre_synapses.size();
							pre.pre_synapses.add(pre_syn_ct,new PreSynapse(pre_syn_ct, syn_type, Worm.TRIADIC));
							pre.pre_synapses.get(pre_syn_ct).associated_neuron = pre;
							GapJunctionGroup sg = new GapJunctionGroup(pre.pre_synapses.get(pre_syn_ct), post1.dendrites.get(p1_dend_ct).post_synapses.get(0), post2.dendrites.get(p2_dend_ct).post_synapses.get(0),  syn_type);
							pre.pre_synapses.get(pre_syn_ct).synapse_groups.add(sg);
							post1.dendrites.get(p1_dend_ct).post_synapses.get(0).synapse_groups.add(sg);
							post2.dendrites.get(p2_dend_ct).post_synapses.get(0).synapse_groups.add(sg);
						}
						else {
							// post 1
							int p1_dend_ct =  post1.dendrites.size();
							post1.dendrites.add(p1_dend_ct, new Dendrite(p1_dend_ct));
							post1.dendrites.get(p1_dend_ct).associated_neuron = post1;
							post1.dendrites.get(p1_dend_ct).add_post_synapse(new PostSynapse(0, ltp_time_window, synapse_strength_multiplier));// 0 because 1 post synapse per dendrite at the moment
							// now we deal with pre
							int pre_syn_ct = pre.pre_synapses.size();
							pre.pre_synapses.add(pre_syn_ct,new PreSynapse(pre_syn_ct, syn_type, Worm.TRIADIC));
							pre.pre_synapses.get(pre_syn_ct).associated_neuron = pre;
							GapJunctionGroup sg = new GapJunctionGroup(pre.pre_synapses.get(pre_syn_ct), post1.dendrites.get(p1_dend_ct).post_synapses.get(0),  syn_type);
							pre.pre_synapses.get(pre_syn_ct).synapse_groups.add(sg);
							post1.dendrites.get(p1_dend_ct).post_synapses.get(0).synapse_groups.add(sg);
						}
					}
					// now these neurons are all hooked up
				}
			}
					//System.exit(0);


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the Neurons
	 */
	public void start_neurons(){
		for (Neuron n: neurons){
			n.start();
		}
	}


	public int get_neuron_index(String name){
		int size = neurons.size();
		for (int i = 0; i < size; i++){
			if (neurons.get(i).name.equals(name)){
				return i;
			}
		}
		return -1;
	}	

}
