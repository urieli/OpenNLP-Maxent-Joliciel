package opennlp.maxent;

import java.io.StringReader;
import junit.framework.TestCase;

import opennlp.maxent.GIS;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.RealBasicEventStream;
import opennlp.model.EventStream;
import opennlp.model.MaxentModel;
import opennlp.model.OnePassRealValueDataIndexer;
import opennlp.model.RealValueFileEventStream;


public class ScaleDoesntMatterTest extends TestCase {

	/**
	 * This test sets out to prove that the scale you use on real valued predicates
	 * doesn't matter when it comes the probability assigned to each outcome.
	 * Strangely, if we use (1,2) and (10,20) there's no difference.
	 * If we use (0.1,0.2) and (10,20) there is a difference.
	 * @throws Exception
	 */
	public void testScaleResults() throws Exception {
		String smallValues = "predA=0.1 predB=0.2 A\n" +
				"predB=0.3 predA=0.1 B\n";
		
		String smallTest = "predA=0.2 predB=0.2";
		
		String largeValues = "predA=10 predB=20 A\n" +
				"predB=30 predA=10 B\n";
		
		String largeTest = "predA=20 predB=20";
		
		StringReader smallReader = new StringReader(smallValues);
		EventStream smallEventStream = new RealBasicEventStream(new PlainTextByLineDataStream(smallReader));

		MaxentModel smallModel = GIS.trainModel(100, new OnePassRealValueDataIndexer(smallEventStream,0), false);
		String[] contexts = smallTest.split(" ");
		float[] values = RealValueFileEventStream.parseContexts(contexts);
		double[] smallResults = smallModel.eval(contexts, values);
		
		String smallResultString = smallModel.getAllOutcomes(smallResults);
		System.out.println("smallResults: " + smallResultString);
		
		StringReader largeReader = new StringReader(largeValues);
		EventStream largeEventStream = new RealBasicEventStream(new PlainTextByLineDataStream(largeReader));

		MaxentModel largeModel = GIS.trainModel(100, new OnePassRealValueDataIndexer(largeEventStream,0), false);
		contexts = largeTest.split(" ");
		values = RealValueFileEventStream.parseContexts(contexts);
		double[] largeResults = largeModel.eval(contexts, values);
		
		String largeResultString = smallModel.getAllOutcomes(largeResults);
		System.out.println("largeResults: " + largeResultString);
		
	    assertEquals(smallResults.length, largeResults.length);
	    for(int i=0; i<smallResults.length; i++) {
	      System.out.println(String.format("classifiy with smallModel: %1$s = %2$f", smallModel.getOutcome(i), smallResults[i]));
	      System.out.println(String.format("classifiy with largeModel: %1$s = %2$f", largeModel.getOutcome(i), largeResults[i]));
	      assertEquals(smallResults[i], largeResults[i], 0.01f);      
	    }
		
	}
}