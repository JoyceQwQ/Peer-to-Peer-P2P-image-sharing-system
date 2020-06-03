import java.awt.image.BufferedImage;
import java.util.*;

/**
 * The class NumberedBlock
 * Add a number to each block
 * To facilitate the arrangement of the blocks sent to a new peer
 * @author hp1
 */
public class NumberedBlock {
    /**
     * The number of the block in the ArrayList subImages,
     * i.e. in the list of normal separated bufferedImage blocks
     */
    int number;
    /**
     * The buffeedImage of the separated 70*70 pixels' block
     */
    BufferedImage subImage;
 	
    /**
     * The constructor of the NumberedBlock
     * @param number the number of this block
     * @param subImage the bufferedImage of the separated block
     */
    public NumberedBlock(int number, BufferedImage subImage) {
    	this.number = number;
   		this.subImage = subImage;
    }
    
}

/**
 * The class for sorting the list of Numbered Blocks
 * by the number of each block
 * Implements the interface Comparator <NumberedBlock>
 * @author hp1
 */
class Sortbynumber implements Comparator<NumberedBlock> 
{ 
    /**
     * The compare method
     * To compare two NumberedBlock with the number
     * @param NumberedBlock a the numbered block a
     * @param NumberedBlock b the numbered block b
     * return a.number - b.number
     */
    public int compare(NumberedBlock a, NumberedBlock b) 
    { 
        return a.number - b.number; 
    } 
} 