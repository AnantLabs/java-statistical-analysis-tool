# Introduction #

In the [previous example](DataTransformsExample.md) we look at data transforms, which is one tool to improving accuracy when we have a more difficult classification problem. For the braver souls, it may be advantages to create a custom classifier. This could be especially true if there is some expert knowledge about the data you could incorporate into the decision process.

In this example, we look at generating a custom data set. While testing a new classifier on existing data sets is a good idea, sometimes you want to create a data set that has certain known properties, and problems, that you would like to test your classifier against. This example creates a 4 class problem with simple Gaussian distributions. This is for simplicity, by combining Gaussians, you can create many arbitrarily complex data sets.

# Code #
```
import java.util.Random;
import javax.swing.JFrame;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.distributions.multivariate.NormalM;
import jsat.graphing.CategoryPlot;
import jsat.linear.DenseMatrix;
import jsat.linear.DenseVector;
import jsat.linear.Matrix;
import jsat.linear.Vec;

/**
 * It can often be useful to generate synthetic data sets to test out classifiers on, and get a feel for how they work. 
 * 
 * @author Edward Raff
 */
public class GeneratingDataExample
{
    public static void main(String[] args)
    {
        //We create a new data set. This data set will have 2 dimensions so we can visualize it, and 4 target class values
        ClassificationDataSet dataSet = new ClassificationDataSet(2, new CategoricalData[0], new CategoricalData(4));

        //We can generate data from a multivarete normal distribution. The 'M' at the end stands for Multivariate 
        NormalM normal;

        //The normal is specifed by a mean and covariance matrix. The covariance matrix must be symmetric. 
        //We use a simple covariance matrix for each data point for simplicity
        Matrix covariance = new DenseMatrix(new double[][]
        {
            {1.0, 0.0}, //Try altering these values to see the change!
            {0.0, 1.0} //Just make sure its still symetric! 
        });

        //And we create 4 different means
        Vec mean0 = DenseVector.toDenseVec(0.0, 0.0);
        Vec mean1 = DenseVector.toDenseVec(0.0, 4.0);
        Vec mean2 = DenseVector.toDenseVec(4.0, 0.0);
        Vec mean3 = DenseVector.toDenseVec(4.0, 4.0);

        Vec[] means = new Vec[] {mean0, mean1, mean2, mean3};

        //We now generate out data
        for(int i = 0; i < means.length; i++)
        {
            normal = new NormalM(means[i], covariance);
            for(Vec sample : normal.sample(300, new Random()))
                dataSet.addDataPoint(sample, new int[0], i);
        }
        
        CategoryPlot plot = new CategoryPlot(dataSet);
        
        JFrame jFrame = new JFrame("2D Visualization");
        jFrame.add(plot);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true); 
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
```