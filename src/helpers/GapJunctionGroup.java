package helpers;

import java.util.ArrayList;

import controller.Worm;
import elements.PostGapJunction;
import elements.PostSynapse;
import elements.PreGapJunction;
import elements.PreSynapse;
import generic.Utils;

/**
 * group between this synapse, and some other synapses (1, 2, or 3 other synapses)
 * 
 * @author Andrew W.E. McDonald
 *
 */
public class GapJunctionGroup {

 
	/**
	 * "this" synapse
	 */
	public PreGapJunction pregap;
	/**
	 * "some other synapses"
	 */
	public PostGapJunction[] postgaps;
	public int type; // Worm.CS or Worm.GJ
	//public int mon_di_tri; // Worm.MONADIC, Worm.DIADIC, Worm.TRIADIC for monadic, diadic, triadic respectively
	public int polyad_status;// 1 if 1 pre to 1 post, 2 if 1 pre to 2 post, and so on.

	/**
	 * For a monadic synapse
	 * @param pre_synapse
	 * @param post_synapse
	 * @param type
	 * @param strength_multiplier
	 * @param timeout
	 */
	public GapJunctionGroup(PreGapJunction pregap, ArrayList<PostGapJunction> postgaps, int type){
		this.polyad_status = postgaps.size();
		this.type = type;
		this.pregap = pregap;
		this.postgaps = new PostGapJunction[this.polyad_status];
		for (int i = 0; i < this.polyad_status; i++){
			this.postgaps[i] = postgaps.get(i);
		}
	}
	
	public String toString(){
		String pre = pregap.toString();
		
		if (mon_di_tri == Worm.MONADIC){
			String pre = pre_synapse.toString();
			String post = post_synapses[0].toString();
			return Utils.timestamp(true)+"GapJunctionGroup :: "+pre+" => "+post;//+
					//" :: strength multiplier = "+strength_multiplier+", timeout = "+timeout+
					//", current window size = "+current_window_size+";";
		}
		if (mon_di_tri == Worm.DIADIC){
			String pre = pre_synapse.toString();
			String post_1 = post_synapses[0].toString();
			String post_2= post_synapses[1].toString();
			return Utils.timestamp(true)+"GapJunctionGroup :: "+pre+" => "+post_1+", "+post_2;//+
					//" :: strength multiplier = "+strength_multiplier+", timeout = "+timeout+
					//", current window size = "+current_window_size+";";
		}
		if (mon_di_tri == Worm.TRIADIC){
			String pre = pre_synapse.toString();
			String post_1 = post_synapses[0].toString();
			String post_2= post_synapses[1].toString();
			String post_3= post_synapses[2].toString();
			return Utils.timestamp(true)+"GapJunctionGroup :: "+pre+" => "+post_1+", "+post_2+", "+post_3;//+
					//" :: strength multiplier = "+strength_multiplier+", timeout = "+timeout+
					//", current window size = "+current_window_size+";";
		}
		return "ERROR: GapJunctionGroup unable to be converted to string.";
	}


}
