
/**
 * Author: Austin Patel
 * Project: Handwritten Recognition
 * File Name: LearningMethod.java
 * Created: 01/01/17
 */

package austinpatel.handwrittenletterrecognition.neural_network;

/**Structure for an artificial neural network learning method.*/
public abstract class LearningMethod {
		
	public abstract double getWeightDelta(double error, int input, int neuronId, int weightId);
	
	public abstract String getName();
	
	public abstract void onLearningCycleStart();
	
	public abstract String getFileName();
	
}
