package jsat.classifiers.evaluation;

import jsat.classifiers.evaluation.Kappa;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.CategoricalResults;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edward Raff
 */
public class KappaTest
{
    
    public KappaTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getScore method, of class Kappa.
     */
    @Test
    public void testGetScore()
    {
        System.out.println("getScore");
        Kappa scorer = new Kappa();
        
        scorer.prepare(new CategoricalData(4));
        //from "On Using and Computing the Kappa Statistic"
        //correct
        scorer.addResult(new CategoricalResults(new double[]{1.0, 0.0, 0.0, 0.0}), 0, 317.0);
        scorer.addResult(new CategoricalResults(new double[]{0.0, 1.0, 0.0, 0.0}), 1, 120.0);
        scorer.addResult(new CategoricalResults(new double[]{0.0, 0.0, 1.0, 0.0}), 2, 60.0);
        scorer.addResult(new CategoricalResults(new double[]{0.0, 0.0, 0.0, 1.0}), 3, 8.0);
        //wrong
        scorer.addResult(new CategoricalResults(new double[]{0.0, 1.0, 0.0, 0.0}), 0, 23.0);
        scorer.addResult(new CategoricalResults(new double[]{1.0, 0.0, 0.0, 0.0}), 1, 61.0);
        scorer.addResult(new CategoricalResults(new double[]{0.0, 1.0, 0.0, 0.0}), 2, 4.0);
        scorer.addResult(new CategoricalResults(new double[]{1.0, 0.0, 0.0, 0.0}), 2, 2.0);
        scorer.addResult(new CategoricalResults(new double[]{0.0, 1.0, 0.0, 0.0}), 3, 29.0);
        scorer.addResult(new CategoricalResults(new double[]{1.0, 0.0, 0.0, 0.0}), 3, 35.0);
        
        assertEquals(0.605, scorer.getScore(), 1e-3);
    }
    
}
