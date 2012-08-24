
package jsat.linear;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Arrays;
import java.util.List;

/**
 * A vector implementation that is dense, meaning all values are allocated - 
 * even if their values will be implicitly zero. 
 * 
 * @author Edward Raff
 */
public class DenseVector extends Vec
{
    protected  double[] array;
    private Double sumCache = null;
    private Double varianceCache = null;
    private Double minCache = null;
    private Double maxCache = null;
    private int startIndex;
    private int endIndex;

    /**
     * Creates a new Dense Vector of zeros 
     * @param length the length of the vector
     */
    public DenseVector(int length)
    {
        if(length < 0)
            throw new ArithmeticException("You can not have a negative dimension vector");
        array = new double[length];
        startIndex = 0;
        endIndex = array.length;
    }

    /**
     * Creates a new vector of the length of the given list, and values copied
     * over in order. 
     * 
     * @param list the list of values to copy into a new vector
     */
    public DenseVector(List<Double> list)
    {
        this.array = new double[list.size()];
        for(int i = 0; i < list.size(); i++)
            this.array[i] = list.get(i);
        startIndex = 0;
        endIndex = this.array.length;
    }

    /**
     * Creates a new Dense Vector that uses the given array as its values. Its 
     * values will not be copied, and raw access and mutations tot he given 
     * array may occur. 
     * 
     * @param array the backing array to use for a new vector of the same length
     */
    public DenseVector(double[] array)
    {
        this(array, 0, array.length);
    }
    
    /**
     * Creates a new Dense Vector that uses the given array as its values. Its 
     * values will not be copied, and raw access and mutations tot he given 
     * array may occur. 
     * 
     * @param array the backing array to use for a new vector 
     * @param start the first index in the array, inclusive, to mark the start 
     * of the vector. 
     * @param end the last index in the array, exclusive, to mark the end of the
     * vector. 
     */
    public DenseVector(double[] array, int start, int end)
    {
        this.array = array;
        this.startIndex = start;
        this.endIndex = end;
    }
    
    
    /**
     * nulls out the cached summary statistics, should be called every time the data set changes
     */
    private void clearCaches()
    {
        sumCache = null;
        varianceCache = null;
        minCache = null;
        maxCache = null;
    }
    
    @Override
    public int length()
    {
        return (endIndex-startIndex);
    }

    @Override
    public double get(int index)
    {
        return array[index+startIndex];
    }

    @Override
    public void set(int index, double val)
    {
        clearCaches();
        array[index+startIndex] = val;
    }

    @Override
    public double min()
    {
        if(minCache != null)
            return minCache;
        
        double result = array[startIndex];
        for(int i = startIndex+1; i < endIndex; i++)
            result = Math.min(result, array[i]);

        return (minCache = result);
    }

    @Override
    public double max()
    {
        if(maxCache != null)
            return maxCache;
        double result = array[startIndex];
        for(int i = startIndex+1; i < endIndex; i++)
            result = Math.max(result, array[i]);

        return (maxCache = result);
    }

    @Override
    public double sum()
    {
        if(sumCache != null)
            return sumCache;
        /*
         * Uses Kahan summation algorithm, which is more accurate then
         * naively summing the values in floating point. Though it
         * does not guarenty the best possible accuracy
         *
         * See: http://en.wikipedia.org/wiki/Kahan_summation_algorithm
         */

        double sum = 0;
        double c = 0;
        for(int i = startIndex; i < endIndex; i++)
        {
            double y = array[i] - c;
            double t = sum+y;
            c = (t - sum) - y;
            sum = t;
        }

        return (sumCache = sum);
    }

    @Override
    public double median()
    {
        double[] copy = Arrays.copyOfRange(array, startIndex, endIndex);

        Arrays.sort(copy); 

        if(copy.length % 2 == 1)
            return copy[copy.length/2];
        else
            return copy[copy.length/2]/2+copy[copy.length/2+1]/2;//Divisions by 2 then add is more numericaly stable
    }

    @Override
    public double mean()
    {
        return sum()/length();
    }

    @Override
    public double skewness()
    {
        double mean = mean();
        
        double tmp = 0;
        
        for(int i = startIndex; i < endIndex; i++)
            tmp += pow(array[i]-mean, 3);
        
        double s1 = tmp / (pow(standardDeviation(), 3) * (array.length-1) );
        
        if(array.length >= 3)//We can use the bias corrected formula
            return sqrt(array.length*(array.length-1))/(array.length-2)*s1;
        
        return s1;
    }

    @Override
    public double kurtosis()
    {
        double mean = mean();
        
        double tmp = 0;
        
        for(int i = startIndex; i < endIndex; i++)
            tmp += pow(array[i]-mean, 4);
        
        return tmp / (pow(standardDeviation(), 4) * (array.length-1) ) - 3;
    }
    
    @Override
    public double standardDeviation()
    {
        return sqrt(variance());
    }

    @Override
    public DenseVector sortedCopy()
    {
        double[] copy = Arrays.copyOfRange(array, startIndex, endIndex);

        Arrays.sort(copy); 

        return new DenseVector(copy);
    }

    @Override
    public double variance()
    {
        if(varianceCache != null)
            return varianceCache;
        double mu = mean();
        double tmp = 0;

        double N = length();


        for(int i = startIndex; i < endIndex; i++)
            tmp += pow(array[i]-mu, 2)/N;
        
        return (varianceCache = tmp);
    }

    @Override
    public double dot(Vec v)
    {
        if(this.length() != v.length())
            throw new ArithmeticException("Vectors must have the same length");
        
        if(v instanceof SparseVector)//Let sparce do it, same both ways and sparce can do it efficently
            return ((SparseVector) v).dot(this);
        
        double dot = 0;
        for(int i = startIndex; i < endIndex; i++)
            dot += array[i] * v.get(i);
        
        return dot;
    }

    public DenseVector deepCopy()
    {
        return new DenseVector(Arrays.copyOf(array, array.length));
    }

    @Override
    public Vec add(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = startIndex; i < endIndex; i++)
            dv.array[i] += c;
        
        return dv;
    }

    @Override
    public Vec subtract(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = startIndex; i < endIndex; i++)
            dv.array[i] -= c;
        
        return dv;
    }

    @Override
    public Vec multiply(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = startIndex; i < endIndex; i++)
            dv.array[i] *= c;
        
        return dv;
    }
       
    @Override
    public void multiply(Matrix A, Vec b)
    {
        if(this.length() != A.rows())
            throw new ArithmeticException("Vector x Matrix dimensions do not agree [1," + this.length() + "] x [" + A.rows() + ", " + A.cols() + "]");
        if(b.length() != A.cols())
            throw new ArithmeticException("Destination vector is not the right size");
        
        for(int i = 0; i < this.length(); i++)
        {
            double this_i = this.array[i+this.startIndex];
            for(int j = 0; j < A.cols(); j++)
                b.increment(j, this_i*A.get(i, j));
        }
    }

    @Override
    public Vec divide(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = startIndex; i < endIndex; i++)
            dv.array[i] /= c;
        
        return dv;
    }

    @Override
    public Vec add(Vec v)
    {
        if(this.length() != v.length())
            throw new ArithmeticException("Vectors must have the same length");

        
        if(v instanceof SparseVector)//Sparce knows how to do this efficently
            return ((SparseVector) v).add(this);
        
        //Else also dense
        
        double[] ret = new double[length()];
        for(int i = 0; i < ret.length; i++)
            ret[i] = array[i+startIndex] + v.get(i);
            
        return new DenseVector(ret);
    }

    @Override
    public Vec subtract(Vec v)
    {
        if(this.length() != v.length())
            throw new ArithmeticException("Vectors must have the same length");
        
        //Subtractio isnt as clever...
        
        double[] ret = new double[length()];
        for(int i = 0; i < ret.length; i++)
            ret[i] = array[i+startIndex] - v.get(i);
            
        return new DenseVector(ret);
    }

    @Override
    public void mutableAdd(double c)
    {
        clearCaches();
        for(int i = startIndex; i < endIndex; i++)
            array[i] += c;
    }

    @Override
    public void mutableAdd(double c, Vec b)
    {
        if(this.length() !=  b.length())
            throw new ArithmeticException("Can not add vectors of unequal length");
        
        clearCaches();
        for(int i = startIndex; i < endIndex; i++)
            array[i] += c*b.get(i);
    }

    @Override
    public void mutableSubtract(double c)
    {
        clearCaches();
        for(int i = startIndex; i < endIndex; i++)
            array[i] -= c;
    }

    @Override
    public void mutableMultiply(double c)
    {
        clearCaches();
        for(int i = startIndex; i < endIndex; i++)
            array[i] *= c;
    }

    @Override
    public void mutableDivide(double c)
    {
        clearCaches();
        for(int i = startIndex; i < endIndex; i++)
            array[i] /= c;
    }

    @Override
    public double pNormDist(double p, Vec y)
    {
        if(this.length() != y.length())
            throw new ArithmeticException("Vectors must be of the same length");
        
        double norm = 0;
        //TODO this could be done more efficently if y is a sparce vector
        for(int i = startIndex; i < endIndex; i++)
            norm += Math.pow(Math.abs(array[i]-y.get(i)), p);
        
        return Math.pow(norm, 1.0/p);
    }

    @Override
    public double pNorm(double p)
    {
        double norm = 0;
        //TODO this could be done more efficently if y is a sparce vector
        for(int i = startIndex; i < endIndex; i++)
            norm += Math.pow(Math.abs(array[i]), p);
        
        return Math.pow(norm, 1.0/p);
    }
    
    @Override
    public Vec clone()
    {
        DenseVector copy = new DenseVector(length());
        
        System.arraycopy(this.array, startIndex, copy.array, 0, length());
        
        return copy;
    }

    @Override
    public Vec normalized()
    {
        Vec copy = this.clone();
        copy.normalize();
        return copy;
    }

    @Override
    public void normalize()
    {
        double sum = 0;

        for(int i = startIndex; i < endIndex; i++)
            sum += array[i]*array[i];
        
        sum = Math.sqrt(sum);

        mutableDivide(sum); 
    }

    @Override
    public Vec pairwiseMultiply(Vec b)
    {
        if(this.length() != b.length())
            throw new ArithmeticException("Vectors must have the same length, " + this.length() + ", " + b.length());
        
        if(b instanceof SparseVector)//Let the sparce class do it efficently
            return b.pairwiseMultiply(this);
        Vec toReturn = b.clone();
        for(int i = 0; i < b.length(); i++)
            b.set(i, b.get(i)*array[i+startIndex]);
        
        return toReturn;
    }

    @Override
    public Vec pairwiseDivide(Vec b)
    {
        if(this.length() != b.length())
            throw new ArithmeticException("Vectors must have the same length");
        
        double[] vals = new double[this.length()];
        for(int i = 0; i < vals.length; i++)
            vals[i] = array[i+startIndex] / b.get(i);
        
        return new DenseVector(vals);
    }

    @Override
    public void mutablePairwiseMultiply(Vec b)
    {
        if(this.length() != b.length())
            throw new ArithmeticException("Vectors must have the same length");
        for(int i = startIndex; i < endIndex; i++)
            this.array[i] *= b.get(i);
    }

    @Override
    public void mutablePairwiseDivide(Vec b)
    {
        if(this.length() != b.length())
            throw new ArithmeticException("Vectors must have the same length");
        for(int i = startIndex; i < endIndex; i++)
            this.array[i] /= b.get(i);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Vec))
            return false;
        Vec otherVec = (Vec) obj;
        
        if(this.length() != otherVec.length())
            return false;
        for(int i = startIndex; i < endIndex; i++)
            if(this.get(i) != otherVec.get(i))
                return false;
        
        return true;
    }

    @Override
    public boolean equals(Object obj, double range)
    {
        if(!(obj instanceof Vec))
            return false;
        Vec otherVec = (Vec) obj;
        range = Math.abs(range);
        
        if(this.length() != otherVec.length())
            return false;
        for(int i = startIndex; i < endIndex; i++)
            if(Math.abs(this.get(i)-otherVec.get(i)) > range)
                return false;
        
        return true;
    }
    
    /**
     * Returns a new dense vector backed by the given array. This is a weak
     * reference, the given array should no longer be altered - as it will 
     * effect the values of the dense vector. 
     * 
     * @param array the array to use as the backing of a dense vector
     * @return a Dense Vector that is backed using the given array
     */
    public static DenseVector toDenseVec(double... array)
    {
        return new DenseVector(array);
    }

    @Override
    public double[] arrayCopy()
    {
        return Arrays.copyOfRange(array, startIndex, endIndex);
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }
}
