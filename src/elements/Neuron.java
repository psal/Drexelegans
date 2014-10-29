package elements;

import generic.Location;
import generic.Log;
import generic.Utils;
import helpers.GapJunctionGroup;

import java.util.ArrayList;

import controller.Worm;


/**
 * A model of a neuron. 
 * @author Andrew W.E. McDonald
 *
 */
public class Neuron extends Thread{//implements Runnable {


	public int neuron_number;
	/**
	 * the total number of sleep cycles this neuron has experienced.
	 * 
	 * as of now, used for the  objects as a clock to determine whether or not
	 * they should desensitize (i.e., if they recieve fire more frequently than some threshold,
	 * it desenstizes.)
	 */
	public int sleep_cycles = 0;
	public String name;
	public String descripton;
	public ArrayList<PreGapJunction> pregaps;
	public ArrayList<PreChemicalSynapse> prechems;// new
	// post_synapses are in the Dendrite class
	public ArrayList<Dendrite> dendrites;
	Location center; // center of the cell body

	/**
	 * firing threshold
	 */
	double v_thresh = -50.0; // mV
	double v_reset = -70.0; // mV (voltage we reset to after firing)
	static public int leak_time_constant = 10;//  ms 
	static public double v_leak = .6; // was at .6 // *100 for percent leaked (so, v_leak = .1 == 10%) of current potential difference from resting (v_delta)

	/**
	 * firing voltage -- i.e. amplitude
	 */
	double v_fire = 2.0;//1.6;// mv -- the amplitude of neuronal fire (before strength_multiplier gets involved)
	double v_current = -70; // mv -- the voltage level of the neuron right now
	double v_delta = 0; // mv -- the current difference in mv from resting potential (-70 mv)
	private boolean alive = true; 

	static double synapse_strength_increment = 0;//.02; // amount by which the strength_multiplier gets incremented when a synapse is strengthed.. this should be less constant, among other things.

	/**
	 * Constructor
	 * @param name
	 */
	public Neuron( String name, int id){
		this.name = name;
		this.neuron_number = id;
		pregaps = new ArrayList<PreGapJunction>(20);
		prechems = new ArrayList<PreChemicalSynapse>(20);
	}

	/**
	 * constructor for neuron class
	 * @param x
	 * @param y
	 * @param z
	 */
	public Neuron(double x, double y, double z){
		center = new Location(x,y,z);
		pregaps = new ArrayList<PreGapJunction>(20);
		prechems = new ArrayList<PreChemicalSynapse>(20);
	}

	/**
	 * constructor for neuron class
	 * @param center
	 */
	public Neuron(Location center){
		this.center = center;
	}


	/**
	 * kill the neuron
	 */
	public void die(){
		alive = false;
	}


	// NOTE need to implement chemical synapse functionality
	public void run(){
		while(alive){
			// check to see if any of this neuron's pre-synapses have been notified of a post-neuron firing.
			// if so, strengthen the connection between the relevant pre-synapse and post-synapse
			for (PreGapJunction pre_s: pregaps){
				for (GapJunctionGroup sg: pre_s.synapse_groups){
					for (PostSynapse post_s: sg.post_synapses){
						if (post_s.neuron_just_fired == true && post_s.current_window_size > 0){
							post_s.strength_multiplier += synapse_strength_increment;
							post_s.current_window_size = 0;
							post_s.neuron_just_fired = false;
						}
						else if (post_s.neuron_just_fired == false && post_s.current_window_size > 0){
							if (post_s.current_window_size >= Neuron.leak_time_constant){
								post_s.current_window_size -= Neuron.leak_time_constant;
							}
							else{
								post_s.current_window_size = 0;
							}
						}
					}
				}
			}
			if (v_current >= v_thresh){
				fire();
				v_current = v_reset;
				v_delta = 0;
			}
			if (v_current > v_reset){
                // instead of subtracting some constant, now we subtract some (contant) percent of v_current from itself.
				v_delta = v_delta*(1-v_leak);
				v_current = v_delta + v_reset; // could probably just use v_delta
				/*
				if ((v_current-v_leak) > v_reset){
					v_current -= v_leak;
				}
				else{
					v_current = v_reset;
				}
				*/
			}
			try {
				Thread.sleep(leak_time_constant);
			} catch (InterruptedException e) {
				System.err.printf("Neuron %d's nap interrupted...\n",neuron_number);
			}
			sleep_cycles++;
		}


	}




	public void add_pregap(int index, PreGapJunction s){
		pregaps.add(s);
	}

	/**
	 * causes 
	 * @param hz
	 */
	public void fire(){
		//for (String s:Worm.touch_neurons){
		for (String s:Worm.motor_neurons){
			if (Utils.starts_with(name, s)){
				//Log.err(name+" (id: "+neuron_number+") is firing (v_current == "+v_current+", v_delta == "+v_delta+")");
				System.out.println(name);
				Worm.utils.write_file(System.currentTimeMillis(), name);
				break;
			}
		}
		
		/*
		 * here we need to run though our list of dendrites,
		 * and run through each dendrite's list of PostSynapses,
		 * and tell each PreGapJunction that is associated with each PostSynapse,
		 * that this neuron just fired. (this code is directly below the comment)
		 * 
		 * Once we do that, the Neuron that owns the PreGapJunctions that were notified will look at the time window
		 * between when they fired and when this neuron fired (or, notified them of its' firing).
		 * If that window is smaller than PreGapJunction.timeout, then the PreGapJunction
		 * will strengthen its connection to the relevant PostSynapse. (this code is up in "run")
		 */
		for (Dendrite d: dendrites){ 
			for (PostSynapse ps: d.post_synapses){
				ps.neuron_just_fired = true;
				ps.current_window_size = ps.timeout;
			}
		}

		int count = 0;
		for (PreGapJunction s:pregaps){
			//System.out.println("Neuron "+neuron_number+" firing; parent: "+tracer.parent);
			s.fire(v_fire);
		}


	}

	/**
	 * increases the current voltage level
	 * @param v
	 */
	public void add_v(double v){
		v_delta += v;
		v_current = v_delta + v_reset;
		/*
		for (String s:Worm.touch_neurons){
			if (Utils.starts_with(name,s)){
				//Log.out("neuron '"+name+"' getting '"+v+"'mv added to v_current... v_current = "+v_current);
				break;
			}
		}
		*/
	}

}
