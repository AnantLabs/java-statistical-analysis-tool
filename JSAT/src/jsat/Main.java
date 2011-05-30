
package jsat;

import java.util.List;
import java.io.File;
import java.util.Collections;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.classifiers.NaiveBayes;
import jsat.distributions.Gamma;
import jsat.distributions.Kolmogorov;
import jsat.math.rootFinding.Zeroin;
import jsat.math.rootFinding.Secant;
import jsat.distributions.Normal;
import jsat.distributions.Weibull;
import jsat.linear.SparceVector;
import jsat.math.ContinuedFraction;
import jsat.math.Function;
import jsat.math.SpecialMath;
import jsat.math.integration.Romberg;
import jsat.math.integration.Trapezoidal;
import jsat.math.rootFinding.Bisection;
import jsat.math.rootFinding.RiddersMethod;
import static java.lang.Math.*;
import static jsat.math.SpecialMath.*;

/**
 *
 * @author Edward Raff
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
//        String sFile = "/Users/eman7613/Desktop/datasets-UCI/UCI/iris.arff";
//        String sFile = "/Users/eman7613/Desktop/datasets-UCI/UCI/vehicle.arff";
//        String sFile = "/Users/eman7613/Desktop/datasets-UCI/UCI/balance-scale.arff";
//        String sFile = "/Users/eman7613/Desktop/datasets-UCI/UCI/glass.arff";
        String sFile = "/Users/eman7613/Desktop/datasets-UCI/UCI/waveform-5000.arff";
        
//        String sFile = "/Users/eman7613/Desktop/datasets-UCI/UCI/vote.arff";

        File f = new File(sFile);
        
        List<DataPoint> dataPoints = ARFFLoader.loadArffFile(f);
        
        ClassificationDataSet cds = new ClassificationDataSet(dataPoints, dataPoints.get(0).numCategoricalValues()-1); 
        
        List<ClassificationDataSet> lcds = cds.cvSet(5);
        
        
        Classifier classifier = new NaiveBayes();
        
        int wrong = 0, right = 0;
        
        for(int i = 0; i < lcds.size(); i++)
        {
            ClassificationDataSet trainSet = ClassificationDataSet.comineAllBut(lcds, i);
            ClassificationDataSet testSet = lcds.get(i);
            
            
            classifier.trainC(trainSet);
            
            for(int j = 0; j < testSet.getPredicting().getNumOfCategories(); j++)
            {
                for (DataPoint dp : testSet.getSamples(j))
                    if (classifier.classify(dp).mostLikely() == j)
                        right++;
                    else
                        wrong++;
            }
            
            
        }
        
        System.out.println("right = " + right + "\twrong = " + wrong + "\t%correct = " + ((double)right)/(wrong+right));
        System.out.println("Yo");
        
//        Gamma gam = new Gamma(188.80827627270023, 0.026570162948900487);
//        
//        for(double x = 3; x < 7; x+=0.10)
//            System.out.print(x + ", ");
//        System.out.println();
//        for(double x = 3; x < 7; x+=0.10)
//            System.out.print(gam.pdf(x) + ", "); 
        
        
        
    }

}