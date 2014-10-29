package generic;

import java.util.ArrayList;

import elements.Neuron;

/**
 * @deprecated
 * 
 * @author Andrew W.E. McDonald
 *
 */
public class TraceTree {
	
	public int neuron_id; 
	public int synapse_id; //relative to neuron (each neuron has "x" pre-synapses)
	public int synapse_pair_id; // relative to each synapse (each pre-synapse can be connected to a number of different post-synapses)
	public TraceTree parent;
	public TraceTree child;
	//public ArrayList<TraceTree> children;
	public static Boolean toString_running = false;
	
	public TraceTree(TraceTree tracer){
		this.neuron_id = tracer.neuron_id;
		this.synapse_id = tracer.synapse_id;
		this.synapse_pair_id = tracer.synapse_pair_id;
		TraceTree temp_p = tracer.parent;
		TraceTree temp = this;
		while (temp_p != null){
			temp.parent = temp_p;
			temp = temp.parent;
			temp_p = temp_p.parent;
		}
		TraceTree temp_c = tracer.child;
		temp = this;
		while (temp_c != null){
			temp.child = temp_c;
			temp = temp.child;
			temp_c = temp_c.child;
		}
		/*
		if (tracer.parent != null){
			this.parent = new TraceTree(tracer.parent,true, false);
		}
		if (tracer.child != null){
			this.child = new TraceTree(tracer.child, false, true);
		}
		*/
	}

	/*
	private TraceTree(TraceTree tracer, Boolean go_up, Boolean go_down){
		this.neuron_id = tracer.neuron_id;
		this.synapse_id = tracer.synapse_id;
		this.synapse_pair_id = tracer.synapse_pair_id;
		if (tracer.parent != null && go_up){
			this.parent = new TraceTree(tracer.parent,true, false);
		}
		if (tracer.child != null && go_down){
			this.child = new TraceTree(tracer.child, false, true);
		}
	}
	*/
	
	public TraceTree(){
		neuron_id = -1;
		synapse_id = -1;
		synapse_pair_id = -1;
		parent = null;
		child = null;
	}
	
	public TraceTree(int neuron_id){
		parent = null;
		synapse_id = -1;
		synapse_pair_id = -1;
		child = null;
		this.neuron_id = neuron_id;
		//children = new ArrayList<TraceTree>(100);
	}
	
	public TraceTree(TraceTree parent, int synapse_id){
		this.parent = parent;
		this.synapse_id = synapse_id;
		this.synapse_pair_id = -1;
		child = null;
		//children = new ArrayList<TraceTree>(100);
	}
			
	public TraceTree(int neuron_id, int synapse_id){
		this.synapse_id = synapse_id;
		parent = null;
		this.neuron_id = neuron_id;
		this.synapse_pair_id = -1;
		child = null;
		//children = new ArrayList<TraceTree>(100);
	}
			
	/**
	 * neurons should use this to add themselves
	 * @param neuron_id
	 * @return
	 */
	public void add_child(int neuron_id, int synapse_id, int synapse_pair_id){
		TraceTree temp = new TraceTree();
		temp.parent = this;
		temp.neuron_id= neuron_id;
		temp.synapse_id = synapse_id;
		temp.synapse_pair_id = synapse_pair_id;
		this.child = temp;
	}
	
	public TraceTree get_child(){
		return this.child;
	}
	
	public TraceTree deep_copy_and_get_child(){
		if (this.child == null){
			System.out.println("Childless TraceTree asked to return child! -- "+this.toString());
			return null;
		}
		TraceTree copy = new TraceTree(this);
		//copy.parent = this;
		return copy.child;
	}
	
	/**
	 * to be used by Synapse *after* the appropriate neuron has already added itself as a child,
	 * just as the synapse fires (i.e. tells another synapse to accept its fire)
	 * 
	 * this adds the synapse_id to the new child (the one created by the neuron). 
	 * 
	 * it also copies the original object to prevent data corruption.
	 * @param synapse_id
	 * @return
	 */
	public TraceTree update_synapse(int synapse_id){
		TraceTree temp = new TraceTree(this);
		if (temp.child == null){
			System.out.println("wtf");
			//temp.child = new TraceTree();
		}
		temp.child.synapse_id = synapse_id;
		return temp.child;
	}
	
	/**
	 * same as above, but for the GapJunctionGroup objects
	 * 
	 * note we *do not* operate on the child here, because the child was returned when "update_synapse" from above was just (or should have been) called.
	 * @param synapse_pair_id
	 * @return
	 */
	public TraceTree update_synapse_pair(int synapse_pair_id){
		TraceTree temp = new TraceTree(this);
		temp.synapse_pair_id = synapse_pair_id;
		return temp;
	}
	
	public String  toString(){
		
		String res = "";
		if (parent == null && child == null){
			return "START => (Neuron# "+neuron_id+", Synapse# "+synapse_id+") => END";
		}
		else if (child == null) {
			res += "(Neuron# "+neuron_id+", Synapse# "+synapse_id+") => END";
		}
		else if (toString_running == true){
			return "(Neuron# "+neuron_id+", Synapse# "+synapse_id+") => ";
		}
		else{
			toString_running = true;
			res = "(Neuron# "+neuron_id+", Synapse# "+synapse_id+") => ";
			TraceTree temp_p = this.parent;
			while (temp_p != null){
				res = temp_p.toString() + res;
				temp_p = temp_p.parent;
			}
			TraceTree temp_c = this.child;
			while (temp_c != null){
				res = res + temp_c.toString();
				temp_c = temp_c.child;
			}
			res += "(Neuron# "+neuron_id+", Synapse# "+synapse_id+") => ";//+child.toString();
			toString_running = false;
		}
		return res;
	}
}
