# Introduction #

In the [previous example](ClassificationCrossValidationExample.md), we saw the basic set up for testing a classifier on a data set. When a default classifier is not enough to solve the problem, one of the ways to fix the situation is to apply a transformation to the data set. A transformation could be used to aid a particular algorithm, make one applicable, or make the problem tractable.

In this example, we use a data transform the visualize our data set. While visualizations are not always useful, they can give us an intuitive understanding about the data, especially if you understand what the visualization means in the context of the method used to reduce the dimensionality. This example uses a method known as [Principal Component Analysis](http://en.wikipedia.org/wiki/Principal_component_analysis).


# Code #

```
import java.io.File;
import javax.swing.JFrame;
import jsat.ARFFLoader;
import jsat.DataSet;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.bayesian.NaiveBayes;
import jsat.datatransform.DataTransform;
import jsat.datatransform.PCA;
import jsat.datatransform.ZeroMeanTransform;
import jsat.graphing.CategoryPlot;

/**
 * For a number of reasons, it may be beneficial to apply some sort of transformation to a data set. 
 * This could be to improve accuracy, reduce computation time, or other reasons. 
 * Here we show an example on how to apply PCA for visualization. 
 * 
 * @author Edward Raff
 */
public class DataTransformsExample
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        String nominalPath = "uci" + File.separator + "nominal" + File.separator;
        File file = new File(nominalPath + "iris.arff");
        DataSet dataSet = ARFFLoader.loadArffFile(file);
        //We specify '0' as the class we would like to make the target class. 
        ClassificationDataSet cDataSet = new ClassificationDataSet(dataSet, 0);

        //The IRIS data set has 4 numerical attributes, unfortunately humans are not good at visualizing 4 dimensional things.
        //Instead, we can reduce the dimensionality down to two. 

        //PCA needs the data samples to have a mean of ZERO, so we need a transform to ensue this property as well
        DataTransform zeroMean = new ZeroMeanTransform(cDataSet);
        cDataSet.applyTransform(zeroMean);
        
        //PCA is a transform that attempts to reduce the dimensionality while maintaining all the variance in the data. 
        //PCA also allows us to specify the exact number of dimensions we would like 
        DataTransform pca = new PCA(cDataSet, 2);
        
        //We can now apply the transformations to our data set
        cDataSet.applyTransform(pca);
        
        //We can now visualize our 2 dimensional data set!
        CategoryPlot plot = new CategoryPlot(cDataSet);
        
        JFrame jFrame = new JFrame("2D Visualization");
        jFrame.add(plot);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

    }

}
```

# Review #
If you visualize the default Iris data set in the example, and compare it the the output of the cross validation of the last example you'll see some intuitive connections. You may have noticed that the 'setosa' class had no false negatives or false positives. Using PCA it becomes clear that the setosa class is very well separated from the rest of the data. While there is some separation between the two other classes, versicolor and virginica, there is still a little bit of over lap. That overlap is where the error comes from.

Be careful though! PCA may show a lot of overlap even though the problem could be solved with high accuracy. This is because PCA attempts to maintain the overall variance when creating a smaller dimension. What distinguishes two classes may not be a function of the over all variance.