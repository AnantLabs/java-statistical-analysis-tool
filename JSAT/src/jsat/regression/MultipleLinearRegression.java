
package jsat.regression;

import java.util.concurrent.ExecutorService;
import jsat.classifiers.DataPoint;
import jsat.classifiers.DataPointPair;
import jsat.linear.DenseMatrix;
import jsat.linear.DenseVector;
import jsat.linear.LUPDecomposition;
import jsat.linear.Matrix;
import jsat.linear.QRDecomposition;
import jsat.linear.Vec;
import jsat.utils.FakeExecutor;

/**
 *
 * @author Edward Raff
 */
public class MultipleLinearRegression implements Regressor
{
    /**
     * The vector B such that Y = X * B is the least squares solution. Will be stored as Y = X * B + a
     */
    private Vec B;
    /**
     * The offset value that is not multiplied by any variable 
     */
    private double a;
    
    
    public double regress(DataPoint data)
    {
        return B.dot(data.getNumericalValues())+a;
    }

    public void train(RegressionDataSet dataSet, ExecutorService threadPool)
    {
        if(dataSet.getNumCategoricalVars() > 0)
            throw new RuntimeException("Multiple Linear Regression only works with numerical values");
        int sda = dataSet.getSampleSize();
        DenseMatrix X = new DenseMatrix(dataSet.getSampleSize(), dataSet.getNumNumericalVars()+1);
        DenseVector Y = new DenseVector(dataSet.getSampleSize());
        
        
        //Construct matrix and vector, Y = X * B, we will solve for B or its least squares solution
        for(int i = 0; i < dataSet.getSampleSize(); i++)
        {
            DataPointPair<Double> dpp = dataSet.getDataPointPair(i);
            
            Y.set(i, dpp.getPair());
            X.set(i, 0, 1.0);//First column is all ones
            Vec vals = dpp.getVector();
            for(int j = 0; j < vals.length(); j++)
                X.set(i, j+1, vals.get(j));
        }
        
        Matrix[] QR = X.qr(threadPool);
        
        QRDecomposition qrDecomp = new QRDecomposition(QR[0], QR[1]);
        
        Vec tmp = qrDecomp.solve(Y);
        a = tmp.get(0);
        B = new DenseVector(dataSet.getNumNumericalVars());
        for(int i = 1; i < tmp.length(); i++)
            B.set(i-1, tmp.get(i));
        
    }

    public void train(RegressionDataSet dataSet)
    {
        train(dataSet, new FakeExecutor());
    }

    public boolean supportsWeightedData()
    {
        return false;
    }

    public MultipleLinearRegression clone()
    {
        MultipleLinearRegression copy = new MultipleLinearRegression();
        copy.B = this.B.clone();
        
        return copy;
    }
    
}
