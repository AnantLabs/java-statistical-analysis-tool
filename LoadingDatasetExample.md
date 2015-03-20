# Introduction #

This is the first example code for using the JSAT library. The code examples that follow will give you a quick introduction for using JSAT.

This example shows how to load up a data set, and access some information about it. Examples provided assume that a copy of the UCI data set's ARFF files are in the same directory. You can grab them from [here](http://www.cs.waikato.ac.nz/ml/weka/index_datasets.html). You may need to change the path accordingly. The other examples will use this data as well, so keep it around! They can also be useful for playing with different classifiers.

# Code #

```
import java.io.File;
import jsat.ARFFLoader;
import jsat.DataSet;
import jsat.classifiers.DataPoint;

/**
 * A simple example on loading up a data set. 
 * 
 * @author Edward Raff
 */
public class LoadingDataSetExample
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        String nominalPath = "uci" + File.separator + "nominal" + File.separator;
        File file = new File(nominalPath + "iris.arff");
        DataSet dataSet = ARFFLoader.loadArffFile(file);
        System.out.println("There are " + dataSet.getNumFeatures() + " features for this data set.");
        System.out.println(dataSet.getNumCategoricalVars() + " categorical features");
        System.out.println("They are:");
        for(int i = 0; i <  dataSet.getNumCategoricalVars(); i++)
            System.out.println("\t" + dataSet.getCategoryName(i));
        System.out.println(dataSet.getNumNumericalVars() + " numerical features");
        System.out.println("They are:");
        for(int i = 0; i <  dataSet.getNumNumericalVars(); i++)
            System.out.println("\t" + dataSet.getNumericName(i));
        
        System.out.println("\nThe whole data set");
        for(int i = 0; i < dataSet.getSampleSize(); i++)
        {
            DataPoint dataPoint = dataSet.getDataPoint(i);
            System.out.println(dataPoint);
        }
        
    }
}
```

## Next Example ##

Next, we look at taking a data set and setting up a classification task [ClassificationExample](ClassificationExample.md)