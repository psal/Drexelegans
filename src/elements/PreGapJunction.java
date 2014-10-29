/**
 * 
 */
package elements;
import java.util.ArrayList;
import generic.Location;
import helpers.GapJunctionGroup;

/**
 * A presynaptic gap junction. Fires when neuron fires; sends signals to PostSynapse's
 * @author Andrew W.E. McDonald
 *
 */
public class PreGapJunction extends GapJunction {

	Location location;
	
	public ArrayList<GapJunctionGroup> synapse_groups;
	
	//public ArrayList<Neuron> outgoing_neurons;
	public int pre_synapse_num; // this number is *relative* to the neuron; not absolute.
	public Neuron associated_neuron; // this *is* absolute.

	public int type;
	public int mon_di_tri;

	/**
	 * 
	 * @param pre_synapse_num -- this presynapse's number in its associated neuron's list of presynpases 
	 * @param type -- Worm.CS or Worm.GJ (for chemical or electrical synapse, repsectively)
	 * @param mon_di_tri -- Worm.MONADIC, Worm.DIADIC, Worm.TRIADIC for monadic, diadic, triadic. not sure if this is necessary for the synapse to know... might remove it at some point.
	 */
	public PreGapJunction(int pre_synapse_num, int type, int mon_di_tri){
		super();
		this.type = type;
		this.mon_di_tri = mon_di_tri;
		this.pre_synapse_num = pre_synapse_num;
		synapse_groups = new ArrayList<GapJunctionGroup>(100);
	}

	/*
	public PreSynapse(Location l, int pre_synapse_num){
		location = l;
		this.pre_synapse_num = pre_synapse_num;
		synapse_pairs = new ArrayList<GapJunctionGroup>(100);
	}
	*/
	
	/* 
	 * fires the synapse
	 */
	public void fire(double v_fire){
		int count = 0;
		for (GapJunctionGroup sp:synapse_groups){
			for (PostSynapse ps: sp.post_synapses){
				/* NOTE ERROR HORRIBLE: this only deals with a gap junction... */
				ps.receive_fire(v_fire);
				//System.out.println(sp.toString());
				count++;
			}
		}
		
	}
	
	/*
	public void add_synapse_pair(int index, PostSynapse post_synapse, double strength_multiplier){
		synapse_pairs.add(index, new GapJunctionGroup(this,post_synapse, strength_multiplier));
	}
	*/
	
	public String toString(){
		return "[PreSynapse #"+pre_synapse_num+", neuron #"+associated_neuron.neuron_number+"]";
	}
	
}

