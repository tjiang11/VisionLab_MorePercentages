package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 * @author Tony Jiang
 * 6-25-2015
 * 
 * Classes Related To:
 * (DotsGameController) > DotsPairGenerator > DotsPair > DotSet > Coordinate
 *                                          > Ratio
 *                                             
 * Generates 'DotsPair's with selected numbers of dots.
 * 
 * The quantity of dots in DotsPairs and consequently DotSets
 * are determined by selected a predefined ratio (Ratio)
 * and scaling that ratio until it is within a desirable range
 * (between MIN_DOTS and MAX_DOTS).
 * 
 * The predefined ratios are put into 'ratiosBucket'. For the next trial,
 * a ratio is choes at random from the ratiosBucket and removed. Once
 * the ratiosBucket is empty, the ratiosBucket is refilled with Ratios
 * based on the blockMode, which "block" the subject is currently in.
 * Whenever, a block is complete (based on number of rounds complete)
 * the ratiosBucket is immediately emptied.
 * 
 * A "DotsPair" in this assessment is the two sets of dots,
 * each of a different color, that is shown in the same space
 * in one trial.
 *
 */
public class DotsPairGenerator {
    
    /** Logger */
    private static Logger logger = Logger.getLogger("mylog");
    
    /** Maximum number of total dots to be shown in one trial. */
    static final int MAX_DOTS = 60;
    /** Minimum number of total dots to be shown in one trial. */
    static final int MIN_DOTS = 20;
    
    /** Max number of times the same relative size (or control type) may be the correct choice. */
    static final int MAX_TIMES_SAME_SIZE_CORRECT = 3;
    
    /** Map from each block to an integer representation. */
    public static final int MORE_THAN_FIFTY_BLOCK = 0;
    public static final int MORE_THAN_SIXTY_BLOCK = 1;
    public static final int MORE_THAN_SEVENTYFIVE_BLOCK = 2;
    public static final int MORE_THAN_TWENTYFIVE_BLOCK = 3;
    public static final int MORE_THAN_FORTY_BLOCK = 4;
    
    /** Random number generator. */
    Random randomGenerator = new Random();

    /** The most recent DotsPair produced by DotsPairGenerator. */
    private DotsPair dotsPair; 
    
    /** The current block setting */
    private int blockMode;
    
    /** Each 'section' contains four blocks. */
    private int numSections;
    
    /** List containing the blocks. */
    private ArrayList<Integer> blockSet;
    
    /** List containing Ratios for the current block. */
    private ArrayList<ArrayList<Ratio>> ratiosBucket;
    
    /** A measure of how many times the same 'size' has been correct, meaning
     how many times has the cluster with bigger/smaller individual dots been correct.
     Depends on area control type --> 
         Equal Areas <--> Smaller Correct  
         Inverse Areas <--> Bigger Correct 
       See ControlType.java.*/
    private int sameSizeCorrect;
    
    /** True if the last correct choice was the cluster with bigger individual dots. */
    private boolean lastWasBig;
    
    /**
     * Constructor. 
     */
    public DotsPairGenerator() {
        this.initLogger();
        this.setLastWasBig(false);
        this.blockSet = new ArrayList<Integer>();
        this.initRatiosBucket();
        this.setNumSections(0);
        this.fillBlockSet();
    }
    
    private void initRatiosBucket() {
        this.ratiosBucket = new ArrayList<ArrayList<Ratio>>();
        this.ratiosBucket.add(0, null);
        this.ratiosBucket.add(0, null);
        this.ratiosBucket.add(0, null);
        this.ratiosBucket.add(0, null);
        this.ratiosBucket.add(0, null);
		this.ratiosBucket.set(MORE_THAN_TWENTYFIVE_BLOCK, new ArrayList<Ratio>());
		this.ratiosBucket.set(MORE_THAN_FORTY_BLOCK, new ArrayList<Ratio>());
		this.ratiosBucket.set(MORE_THAN_FIFTY_BLOCK, new ArrayList<Ratio>());
		this.ratiosBucket.set(MORE_THAN_SIXTY_BLOCK, new ArrayList<Ratio>());
		this.ratiosBucket.set(MORE_THAN_SEVENTYFIVE_BLOCK, new ArrayList<Ratio>());
	}

	private void initLogger() {
        FileHandler fh;  
        try {  
            fh = new FileHandler("myLog.log");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
    }
    
    /**
     * Fill the block set in a random manner.
     */
    private void fillBlockSet() {
        ArrayList<Integer> tempSet = new ArrayList<Integer>();
        tempSet.add(MORE_THAN_TWENTYFIVE_BLOCK);
        tempSet.add(MORE_THAN_FORTY_BLOCK);
        tempSet.add(MORE_THAN_FIFTY_BLOCK);
        tempSet.add(MORE_THAN_SIXTY_BLOCK);
        tempSet.add(MORE_THAN_SEVENTYFIVE_BLOCK);
        int size = tempSet.size();
        for (int i = 0; i < size; i++) {
            this.blockSet.add((Integer) tempSet.remove(randomGenerator.nextInt(tempSet.size())));
            logger.log(Level.INFO, this.blockSet.toString());
        }
    }
    
//    /** 
//     * Get a new pair based on current mode. 
//     */
//    public void getNewModePair() {
//        Ratio ratio = this.decideRatio();
//        this.getNewPair(ratio);
//    }
    
    /**
     * Get a new pair based on block.
     */
    public void getNewBlockPair() {
    	int block = this.decideBlock();
    	Ratio ratio = this.decideRatio(block);
    	this.getNewPair(ratio);
    }
    
    /**
     * Empty the ratio bucket. 
     */
    public void clearRatios() {
        this.ratiosBucket.clear();
    }
    
    /**
     * Pick a ratio at random from the ratiosBucket. If the ratiosBucket is empty, then refill it.
     * @return Ratio
     */
    private Ratio decideRatio(int block) {
        if (this.ratiosBucket.get(block).isEmpty()) {
            fillRatiosBucket(block);
        }
        Ratio ratio = this.ratiosBucket.get(block).remove(randomGenerator.nextInt(this.ratiosBucket.get(block).size()));
        return ratio;
    }
    
    /**
     * Pick a block at random from the blockSet. 
     * @return block
     */
    private int decideBlock() {
    	if (this.blockSet.isEmpty()) {
    		this.fillBlockSet();
    	}
    	int block = this.blockSet.remove(randomGenerator.nextInt(this.blockSet.size()));
    	this.blockMode = block;
    	return block;
    }
    
    /**
     * Refill the ratiosBucket based on the current block.
     */
    private void fillRatiosBucket(int block) {
        switch (block) {
        case MORE_THAN_TWENTYFIVE_BLOCK:
        		// Need to update
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(9,16));
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(2,3));
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(11,14));
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(12,13));
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(13,12));
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(14,11));
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(3,2));
	            this.ratiosBucket.get(MORE_THAN_TWENTYFIVE_BLOCK).add(new Ratio(16,9));
	            break;
        case MORE_THAN_FORTY_BLOCK:
        		// Need to update
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(9,16));
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(2,3));
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(11,14));
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(12,13));
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(13,12));
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(14,11));
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(3,2));
	            this.ratiosBucket.get(MORE_THAN_FORTY_BLOCK).add(new Ratio(16,9));
	            break;
        case MORE_THAN_FIFTY_BLOCK:
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(9,16));
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(2,3));
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(11,14));
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(12,13));
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(13,12));
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(14,11));
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(3,2));
                this.ratiosBucket.get(MORE_THAN_FIFTY_BLOCK).add(new Ratio(16,9));
            break;
        case MORE_THAN_SIXTY_BLOCK:
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(11,14));
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(12,13));
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(13,12));
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(4,3));
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(5,3));
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(13,7));
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(18,7));
                this.ratiosBucket.get(MORE_THAN_SIXTY_BLOCK).add(new Ratio(19,6));
            break;
        case MORE_THAN_SEVENTYFIVE_BLOCK:
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(7,6));
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(3,2));
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(2,1));
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(18,7));
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(18,5));
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(21,4));
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(9,1));
                this.ratiosBucket.get(MORE_THAN_SEVENTYFIVE_BLOCK).add(new Ratio(24,1));
            break;
        }
        logger.log(Level.INFO, this.ratiosBucket.toString());
    }
    
    /**
     * Get a new pair with a specified ratio of numbers of dots.
     * Scale the total number of dots to a range between MIN_DOTS and MAX_DOTS.
     * @param ratio
     */
    private void getNewPair(Ratio ratio) {
        int ratioNumOne = ratio.getNumOne();
        int ratioNumTwo = ratio.getNumTwo();
        int numDotsOne = ratio.getNumOne();
        int numDotsTwo = ratio.getNumTwo();
        while (numDotsOne + numDotsTwo < MIN_DOTS) {
            numDotsOne += ratioNumOne;
            numDotsTwo += ratioNumTwo;
        }
        int max = (MAX_DOTS - (numDotsOne + numDotsTwo)) / (ratioNumOne + ratioNumTwo);
        int randMax = randomGenerator.nextInt(max);
        for (int i = 0; i < randMax; i++) {
            numDotsOne += ratioNumOne;
            numDotsTwo += ratioNumTwo;
        }
        this.checkAndSet(numDotsOne, numDotsTwo);
        logger.log(Level.INFO, numDotsOne + " " + numDotsTwo);
        logger.log(Level.INFO, Double.toString((double) numDotsOne / (numDotsOne + numDotsTwo)));
        logger.log(Level.INFO, Double.toString((double) numDotsTwo / (numDotsOne + numDotsTwo)));
    }
    
    /**
     * Set the dots pair.
     * @param dotSetOne number of dots in dot set one.
     * @param dotSetTwo number of dots in dot set two.
     */
    private void checkAndSet(int dotSetOne, int dotSetTwo) {  
        ControlType controlTypeCandidate = generateAreaControlType(dotSetOne, dotSetTwo);
        this.setDotsPair(new DotsPair(dotSetOne, dotSetTwo, controlTypeCandidate));
    }
    
    /**
     * Perform checks.
     */
    private void performChecks(int dotSetOne, int dotSetTwo, ControlType controlTypeCandidate) {
        this.checkSameSize(controlTypeCandidate);
    }

    /** 
     * Generate the next ControlType.
     * @param dotSetOne
     * @param dotSetTwo
     * @return The next ControlType.
     */
    private ControlType generateAreaControlType(int dotSetOne, int dotSetTwo) {
        ControlType controlTypeCandidate = generateRandomAreaControlType();
        this.performChecks(dotSetOne, dotSetTwo, controlTypeCandidate);
        if (this.getSameSizeCorrect() >= MAX_TIMES_SAME_SIZE_CORRECT) {
            controlTypeCandidate = changeControlType(controlTypeCandidate);
        }
        return controlTypeCandidate;
    }
    
    /**
     * Generates a random control type. Either EQUAL_AREAS or INVERSE_AREAS.
     * @return random control type.
     */
    private ControlType generateRandomAreaControlType() {
        if (randomGenerator.nextBoolean()) {
            return ControlType.EQUAL_AREAS;
        } else {
            return ControlType.INVERSE_AREAS;
        }
    }
    
    /**
     * Swap the area of the controlTypeCandidate between EQUAL_AREAS and INVERSE_AREAS.
     * @param controlTypeCandidate
     * @return the opposite control type.
     */
    private ControlType changeControlType(ControlType controlTypeCandidate) {
        this.setSameSizeCorrect(0);
        this.toggleLastWasBig();
        if (controlTypeCandidate == ControlType.EQUAL_AREAS) {
            return ControlType.INVERSE_AREAS;
        } else if (controlTypeCandidate == ControlType.INVERSE_AREAS) {
            return ControlType.EQUAL_AREAS;
        }
        return ControlType.NONE;
    }
        
    /**
     * Check if the same relative size (or control type) is correct
     * as the last round. Inverse areas <--> Big correct. Equal areas <--> Small correct.
     * 
     * See ControlType.java
     * 
     * @param controlType the ControlType to be evaluated.
     */
    private void checkSameSize(ControlType controlType) {
        if (controlType == ControlType.INVERSE_AREAS) {
            if (this.lastWasBig) {
                this.incrementSameSizeCorrect();
            } else {
                this.setSameSizeCorrect(0);
            }
            this.lastWasBig = true;
        } else if (controlType == ControlType.EQUAL_AREAS) {
            if (!this.lastWasBig) {
                this.incrementSameSizeCorrect();
            } else {
                this.setSameSizeCorrect(0);
            }
            this.lastWasBig = false;
        }
    }
    
    /**
     * Toggles which of the last relative sizes was correct.
     */
    private void toggleLastWasBig() {
        if (this.lastWasBig) {
            this.lastWasBig = false;
        } else {
            this.lastWasBig = true;
        }   
    }
        
    /** 
     * Change to the next block. Clear the ratiosBucket.
     */
//    public void changeBlock() {
//        this.blockSet.remove(0);
//        if (!this.blockSet.isEmpty()) {
//            this.blockMode = this.blockSet.get(0);
//        } else {
//            this.fillBlockSet();
//            this.blockMode = this.blockSet.get(0);
//        }
//        this.ratiosBucket.clear();
//    }

    public DotsPair getDotsPair() {
        return this.dotsPair;
    }

    public void setDotsPair(DotsPair dotsPair) {
        this.dotsPair = dotsPair;
    }
    
    public boolean isLastWasBig() {
        return lastWasBig;
    }

    public void setLastWasBig(boolean lastWasBig) {
        this.lastWasBig = lastWasBig;
    }

    public int getSameSizeCorrect() {
        return sameSizeCorrect;
    }

    public void setSameSizeCorrect(int sameSizeCorrect) {
        this.sameSizeCorrect = sameSizeCorrect;
    }
    
    public void incrementSameSizeCorrect() {
        this.sameSizeCorrect++;
    }

    public int getBlockMode() {
        return blockMode;
    }

    public void setBlockMode(int blockMode) {
        this.blockMode = blockMode;
    }

    public int getNumSections() {
        return numSections;
    }

    public void setNumSections(int numSections) {
        this.numSections = numSections;
    }
}
