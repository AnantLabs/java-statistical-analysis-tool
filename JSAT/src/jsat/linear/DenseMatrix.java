
package jsat.linear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsat.utils.SystemInfo;
import static java.lang.Math.*;

/**
 *
 * @author Edward Raff
 */
public class DenseMatrix extends Matrix
{
    /**
     * Step size if the computation accesses 2*NB2^2 * dataTypeSize data
     */
    private static int NB2 = (int) sqrt(SystemInfo.L2CacheSize/(8*2));
    private static final int maxThreads = Runtime.getRuntime().availableProcessors();
    
    private final double[][] matrix;

    /**
     * Creates a new matrix based off the given vectors. 
     * @param a the first Vector, this new Matrix will have as many rows as the length of this vector
     * @param b the second Vector, this new Matrix will have as many columns as this length of this vector
     */
    public DenseMatrix(Vec a, Vec b)
    {
        matrix = new double[a.length()][b.length()];
        for(int i = 0; i < a.length(); i++)
        {
            Vec rowVals = b.multiply(a.get(i));
            for(int j = 0; j < b.length(); j++)
                matrix[i][j] = rowVals.get(j);
        }
    }
    
    public DenseMatrix(int rows, int cols)
    {
        matrix = new double[rows][cols];
    }
    
    /**
     * Creates a new matrix that is a copy of the given matrix. 
     * An error will be throw if the rows of the given matrix 
     * are not all the same size
     * 
     * @param matrix the matrix to copy the values of
     */
    public DenseMatrix(double[][] matrix)
    {
        this.matrix = new double[matrix.length][matrix[0].length];
        for(int i = 0; i < this.matrix.length; i++)
            if(matrix[i].length != this.matrix[i].length)//The matrix we were given better have rows of the same length!
                throw new RuntimeException("Given matrix was not of consistent size (rows have diffrent lengths)");
            else
                System.arraycopy(matrix[i], 0, this.matrix[i], 0, this.matrix[i].length);
    }
   
    private class MuttableAddRun implements Runnable
    {
        final CountDownLatch latch;
        final Matrix b;
        final int threadId;

        public MuttableAddRun(CountDownLatch latch, Matrix b, int threadId)
        {
            this.latch = latch;
            this.b = b;
            this.threadId = threadId;
        }
        
        

        public void run()
        {
            for(int i = 0+threadId; i < rows(); i+=maxThreads)
                for(int j = 0; j < cols(); j++)
                    matrix[i][j] += b.get(i, j);
            latch.countDown();
        }
    }
    
    @Override
    public void mutableAdd(Matrix b)
    {
        if(!sameDimensions(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        
        for(int i = 0; i < rows(); i++)
            for(int j = 0; j < cols(); j++)
                this.matrix[i][j] += b.get(i, j);
    }

    @Override
    public void mutableAdd(Matrix b, ExecutorService threadPool)
    {
        if(!sameDimensions(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        
        CountDownLatch latch = new CountDownLatch(maxThreads);
        
        for(int threadId = 0; threadId < maxThreads; threadId++)
            threadPool.submit(new MuttableAddRun(latch, b, threadId));
        
        try
        {
            latch.await();
        }
        catch (InterruptedException ex)
        {
            //Eww, mutable failure is ugly
            Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void mutableAdd(double c)
    {
        for(int i = 0; i < rows(); i++)
            for(int j = 0; j < cols(); j++)
                matrix[i][j] += c;
    }
    
    private class MuttableAddConstRun implements Runnable
    {
        final CountDownLatch latch;
        final double constant;
        final int threadID;

        public MuttableAddConstRun(CountDownLatch latch, double constant, int threadID)
        {
            this.latch = latch;
            this.constant = constant;
            this.threadID = threadID;
        }
        
        public void run()
        {
            for(int i = 0+threadID; i < rows(); i+=maxThreads)
                for(int j = 0; j < cols(); j++)
                    matrix[i][j] += constant;
            latch.countDown();
        }
    }

    @Override
    public void mutableAdd(double c, ExecutorService threadPool)
    {
        CountDownLatch latch = new CountDownLatch(maxThreads);
        
        for(int threadID = 0; threadID < maxThreads; threadID++)
            threadPool.submit(new MuttableAddConstRun(latch, c, threadID));
        
        try
        {
            latch.await();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private class MuttableSubRun implements Runnable
    {
        final CountDownLatch latch;
        final int threadID;
        final Matrix b;

        public MuttableSubRun(CountDownLatch latch, int threadID, Matrix b)
        {
            this.latch = latch;
            this.threadID = threadID;
            this.b = b;
        }

        public void run()
        {
            for(int i = 0+threadID; i < rows(); i+=maxThreads)
                for(int j = 0; j < cols(); j++)
                    matrix[i][j] -= b.get(i, j);
            latch.countDown();
        }
    }

    @Override
    public void mutableSubtract(Matrix b)
    {
        if(!sameDimensions(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        
        for(int i = 0; i < rows(); i++)
            for(int j = 0; j < cols(); j++)
                this.matrix[i][j] -= b.get(i, j);
    }

    @Override
    public void mutableSubtract(Matrix b, ExecutorService threadPool)
    {
        if(!sameDimensions(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        
        CountDownLatch latch = new CountDownLatch(maxThreads);
        
        for(int threadID = 0; threadID < maxThreads; threadID++)
            threadPool.submit(new MuttableSubRun(latch, threadID, b));
        try
        {
            latch.await();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Vec multiply(Vec b)
    {
        if(this.cols() != b.length())
            throw new ArithmeticException("Matrix dimensions do not agree, [" + rows() +"," + cols() + "] x [" + b.length() + ",1]" );
        
        
        DenseVector result = new DenseVector(rows());
        for(int i = 0; i < rows(); i++)
        {
            //The Dense construcure does not copy the matrix, it just takes the refernce -making it fast
            DenseVector row = new DenseVector(matrix[i]);
            result.set(i, row.dot(b));//We use the dot product in this way so that if the incoming matrix is sparce, we can take advantage of save computaitons
        }
        
        return result;
    }
    
    private class VecMultiRun implements Callable<Double>
    {
        final Vec row;
        final Vec b;

        public VecMultiRun(Vec row, Vec b)
        {
            this.row = row;
            this.b = b;
        }

        public Double call() throws Exception
        {
            return row.dot(b);
        }
       
        
        
    }

    @Override
    public Vec multiply(Vec b, ExecutorService threadPool)
    {
        if(this.cols() != b.length())
            throw new ArithmeticException("Matrix dimensions do not agree");
        
        DenseVector result = new DenseVector(rows());
        
        List<Future<Double>> vecVals = new ArrayList<Future<Double>>(rows());
        for(int i = 0; i < rows(); i++)
        {
            DenseVector row = new DenseVector(matrix[i]);
            vecVals.add(threadPool.submit(new VecMultiRun(row, b)));
        }
        
        try
        {
            for (int i = 0; i < vecVals.size(); i++)
            {
                result.set(i, vecVals.get(i).get());
            }
        }
        catch (InterruptedException interruptedException)
        {
        }
        catch (ExecutionException executionException)
        {
            
        }
        
        return result;
    }

    /**
     * 
     * @param b
     * @return 
     */
    private Matrix pureRowOrderMultiply(Matrix b)
    {
        if(!canMultiply(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        DenseMatrix result = new DenseMatrix(this.rows(), b.cols());
        
        /*
         * In stead of row echelon order (i, j, k), we compue in "pure row oriented"
         * 
         * see
         * 
         * Data structures in Java for matrix computations
         * 
         * CONCURRENCY AND COMPUTATION: PRACTICE AND EXPERIENCE
         * Concurrency Computat.: Pract. Exper. 2004; 16:799–815 (DOI: 10.1002/cpe.793)
         * 
         */
        
        //Pull out the index operations to hand optimize for speed. 
        double[] Arowi;
        double[] Crowi;
        for(int i = 0; i < result.rows(); i++)
        {
            Arowi = this.matrix[i];
            Crowi = result.matrix[i];
            
            for(int k = 0; k < this.cols(); k++)
            {
                double a = Arowi[k];
                for(int j = 0; j < Crowi.length; j++)
                    Crowi[j] += a*b.get(k, j);
            }
        }
        
        return result;
    }
    
    private Matrix blockMultiply(Matrix b)
    {
        if(!canMultiply(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        DenseMatrix result = new DenseMatrix(this.rows(), b.cols());
        ///Should choose step size such that 2*NB2^2 * dataTypeSize <= CacheSize
        
        int iLimit = result.rows();
        int jLimit = result.cols();
        int kLimit = this.cols();
        
        for(int i0 = 0; i0 < iLimit; i0+=NB2)
            for(int k0 = 0; k0 < kLimit; k0+=NB2)
                for(int j0 = 0; j0 < jLimit; j0+=NB2)
                {
                    for(int i = i0; i < min(i0+NB2, iLimit); i++)
                    {
                        double[] c_row_i = result.matrix[i];
                        
                        for(int k = k0; k < min(k0+NB2, kLimit); k++)
                        {
                            double a = this.matrix[i][k];
                            
                            for(int j = j0; j < min(j0+NB2, jLimit); j++)
                                c_row_i[j] += a * b.get(k, j);
                            }
                        }
                    }
        
        return result;
    }
    
    private class BlockMultRun implements Runnable
    {
        final CountDownLatch latch;
        final DenseMatrix result;
        final Matrix b;
        final int kLimit, jLimit, iLimit, threadID;
        
        public BlockMultRun(CountDownLatch latch, DenseMatrix result, Matrix b, int threadID)
        {
            this.latch = latch;
            this.result = result;
            this.b = b;
            this.kLimit = cols();
            this.jLimit = result.cols();
            this.iLimit = result.cols();
            this.threadID = threadID;
        }
        
        public void run()
        {
            for(int i0 = NB2*threadID; i0 < iLimit; i0+=NB2*maxThreads)
                for(int k0 = 0; k0 < kLimit; k0+=NB2)
                    for(int j0 = 0; j0 < jLimit; j0+=NB2)
                    {
                        for(int i = i0; i < min(i0+NB2, iLimit); i++)
                        {
                            double[] c_row_i = result.matrix[i];

                            for(int k = k0; k < min(k0+NB2, kLimit); k++)
                            {
                                double a = matrix[i][k];

                                for(int j = j0; j < min(j0+NB2, jLimit); j++)
                                    c_row_i[j] += a * b.get(k, j);
                            }
                        }
                    }

            latch.countDown();
        }
        
    }
    
    private Matrix blockMultiply(Matrix b, ExecutorService threadPool)
    {
        if(!canMultiply(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        DenseMatrix result = new DenseMatrix(this.rows(), b.cols());
        
        CountDownLatch latch = new CountDownLatch(maxThreads);
        
        for(int threadID = 0; threadID < maxThreads; threadID++)
            threadPool.submit(new BlockMultRun(latch, result, b, threadID));
        try
        {
            latch.await();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    @Override
    public Matrix multiply(Matrix b)
    {
        return pureRowOrderMultiply(b);
    }
    
    /**
     * this is a direct conversion of the outer most loop of {@link #multiply(jsat.linear.Matrix) } 
     */
    private class MultRun implements Runnable
    {
        
        final CountDownLatch latch;
        final DenseMatrix A, result;
        final Matrix B;
        final int threadID;

        public MultRun(CountDownLatch latch, DenseMatrix A, DenseMatrix result, Matrix B, int threadID)
        {
            this.latch = latch;
            this.A = A;
            this.result = result;
            this.B = B;
            this.threadID = threadID;
        }
        
        public void run()
        {

            //Pull out the index operations to hand optimize for speed. 
            double[] Arowi;
            double[] Crowi;
            for(int i = 0+threadID; i < result.rows(); i+=maxThreads)
            {
                Arowi = A.matrix[i];
                Crowi = result.matrix[i];

                for(int k = 0; k < A.cols(); k++)
                {
                    double a = Arowi[k];
                    for(int j = 0; j < Crowi.length; j++)
                        Crowi[j] += a*B.get(k, j);
                }
            }
            latch.countDown();
        }
    }

    @Override
    public Matrix multiply(Matrix b, ExecutorService threadPool)
    {
        if(this.rows()/NB2 >= maxThreads)//Perform block execution only when we have a large enough matrix to keep ever core busy!
            return blockMultiply(b, threadPool);
        if(!canMultiply(this, b))
            throw new ArithmeticException("Matrix dimensions do not agree");
        DenseMatrix result = new DenseMatrix(this.rows(), b.cols());
        CountDownLatch cdl = new CountDownLatch(maxThreads);
        
        for (int threadID = 0; threadID < maxThreads; threadID++)
            threadPool.submit(new MultRun(cdl, this, result, b, threadID));
            
        try
        {
            cdl.await();
        }
        catch (InterruptedException ex)
        {
            //faulre? Gah - try seriel
            return this.multiply(b);
        }
        
        return result;
    }
    
    @Override
    public void mutableMultiply(double c)
    {
        for(int i = 0; i < rows(); i++)
            for(int j = 0; j < cols(); j++)
                matrix[i][j] *= c;
    }
    
    private class MultConstant implements Runnable
    {
        final CountDownLatch latch;
        final double c;
        final int threadID;

        public MultConstant(CountDownLatch latch, double c, int threadID)
        {
            this.latch = latch;
            this.c = c;
            this.threadID = threadID;
        }

        public void run()
        {
            for(int i = 0+threadID; i < rows(); i+=maxThreads)
                for(int j = 0; j < cols(); j++)
                    matrix[i][j] *= c;
            latch.countDown();
        }
        
    }

    @Override
    public void mutableMultiply(double c, ExecutorService threadPool)
    {
        CountDownLatch latch = new CountDownLatch(maxThreads);
        for(int threadID = 0; threadID < maxThreads; threadID++)
            threadPool.submit(new MultConstant(latch, c, threadID));
        try
        {
            latch.await();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void mutableTranspose()
    {
       for(int i = 0; i < rows()-1; i++)
            for(int j = i+1; j < cols(); j++)
            {
                double tmp = matrix[j][i];
                matrix[j][i] = matrix[i][j];
                matrix[i][j] = tmp;
            }
    }
        
    @Override
    public Matrix transpose()
    {
        DenseMatrix transpose = new DenseMatrix(cols(), rows());
        int BL = NB2;
        for (int i0 = 0; i0 < rows(); i0 += BL)
            for (int j0 = 0; j0 < cols(); j0 += BL)
                for (int i = i0; i < min(i0+BL, rows()); i++)
                    for (int j = j0; j < min(j0+BL, cols()); j++)
                        transpose.matrix[j][i] = this.matrix[i][j];

        return transpose;
    }
    
    @Override
    public double get(int i, int j)
    {
        return matrix[i][j];
    }

    @Override
    public void set(int i, int j, double value)
    {
        matrix[i][j] = value;
    }

    @Override
    public int rows()
    {
        return matrix.length;
    }

    @Override
    public int cols()
    {
        return matrix[0].length;
    }

    @Override
    public boolean isSparce()
    {
        return false;
    }

    @Override
    public long nnz()
    {
        //In a dense matrix we consider all entries to be non null
        return  ((long) matrix.length )*matrix[0].length;
    }

    @Override
    public void swapRows(int r1, int r2)
    {
        if(r1 >= rows() || r2 >= rows())
            throw new ArithmeticException("Can not swap row, matrix is smaller then requested");
        else if(r1 < 0 || r2 < 0)
            throw new ArithmeticException("Can not swap row, there are no negative row indices");
        double[] tmp = matrix[r1];
        matrix[r1] = matrix[r2];
        matrix[r2] = tmp;
    }
    
    @Override
    public void zeroOut()
    {
        for(int i = 0; i < rows(); i++)
            Arrays.fill(matrix[i], 0);
    }
    
    public Matrix[] lup()
    {
        Matrix[] lup = new Matrix[3];
        
        Matrix P = eye(rows());
        DenseMatrix L;
        DenseMatrix U = this;
        
        //Initalization is a little wierd b/c we want to handle rectangular cases as well!
        if(rows() > cols())//In this case, we will be changing U before returning it (have to make it smaller, but we can still avoid allocating extra space
            L = new DenseMatrix(rows(), cols());
        else
            L = new DenseMatrix(rows(), rows());        
        
        for(int i = 0; i < U.rows(); i++)
        {
            //If rectangular, we still need to loop through to update ther est of L - even though we wont make many other changes
            if(i < U.cols())
            {
                //Partial pivoting, find the largest value in this colum and move it to the top! 
                //Find the largest magintude value in the colum k, row j
                int largestRow = i;
                double largestVal = Math.abs(U.matrix[i][i]);
                for (int j = i + 1; j < U.rows(); j++)
                {
                    double rowJLeadVal = Math.abs(U.matrix[j][i]);
                    if (rowJLeadVal > largestVal)
                    {
                        largestRow = j;
                        largestVal = rowJLeadVal;
                    }
                }

                //SWAP!
                U.swapRows(largestRow, i);
                P.swapRows(largestRow, i);
                L.swapRows(largestRow, i);
                
                L.matrix[i][i] = 1;
            }   

            //Seting up L 
            for(int k = 0; k < Math.min(i, U.cols()); k++)
            {
                L.matrix[i][k] = U.matrix[i][k]/U.matrix[k][k];
                U.matrix[i][k] = 0;

                for(int j = k+1; j < U.cols(); j++)
                {
                    U.matrix[i][j] -= L.matrix[i][k]*U.matrix[k][j];
                }
            }
        }
        
        
        if(rows() > cols())//Clean up!
        {
            //We need to change U to a square nxn matrix in this case, we can safely drop the last 2 columns!
            double[][] newU = new double[cols()][];
            System.arraycopy(U.matrix, 0, newU, 0, newU.length);
            U = new DenseMatrix(newU);//We have made U point at a new object, but the array is still pointing at the same rows! 
        }
        
        lup[0] = L;
        lup[1] = U;
        lup[2] = P;
        
        return lup;
    }
    
    private class LUProwRun implements Callable<Integer>
    {
        final DenseMatrix L;
        final DenseMatrix U;
        final int k, threadNumber;
        double largestSeen = Double.MIN_VALUE;
        int largestIndex ;

        public LUProwRun(DenseMatrix L, DenseMatrix U, int k, int threadNumber)
        {
            this.L = L;
            this.U = U;
            this.k = k;
            largestIndex = k+1;
            this.threadNumber = threadNumber;
        }
       
        /**
         * Returns the index of the row with the largest absolute value we ever saw in column k+1
         */
        public Integer call() throws Exception
        {
            for(int i = k+1+threadNumber; i < U.rows(); i+=maxThreads)
            {
                L.matrix[i][k] = U.matrix[i][k]/U.matrix[k][k];

                //We perform the first iteration of the loop outside, as we want to cache its value for searching later
                U.matrix[i][k+1] -= L.matrix[i][k]*U.matrix[k][k+1];
                if(Math.abs(U.matrix[i][k+1]) > largestSeen)
                {
                    largestSeen = Math.abs(U.matrix[i][k+1]);
                    largestIndex = i;
                }
                for(int j = k+2; j < U.cols(); j++)
                {
                    U.matrix[i][j] -= L.matrix[i][k]*U.matrix[k][j];
                }
            }
            
            return largestIndex;
        }
        
    }
    
    @Override
    public Matrix[] lup(ExecutorService threadPool)
    {
        Matrix[] lup = new Matrix[3];
        
        Matrix P = eye(rows());
        DenseMatrix L;
        DenseMatrix U = this;
        
        //Initalization is a little wierd b/c we want to handle rectangular cases as well!
        if(rows() > cols())//In this case, we will be changing U before returning it (have to make it smaller, but we can still avoid allocating extra space
            L = new DenseMatrix(rows(), cols());
        else
            L = new DenseMatrix(rows(), rows());
        
        List<Future<Integer>> bigIndecies = new ArrayList<Future<Integer>>(maxThreads);
        for(int k = 0; k < Math.min(rows(), cols()); k++)
        {
            //Partial pivoting, find the largest value in this colum and move it to the top! 
            //Find the largest magintude value in the colum k, row j
            int largestRow = k;
            double largestVal = Math.abs(U.matrix[k][k]);
            if(bigIndecies.isEmpty())
                for(int j = k+1; j < U.rows(); j++)
                {
                    double rowJLeadVal = Math.abs(U.matrix[j][k]);
                    if(rowJLeadVal > largestVal)
                    {
                        largestRow = j;
                        largestVal = rowJLeadVal;
                    }
                }
            else
            {
                for(Future<Integer> fut : bigIndecies)
                {
                    try
                    {
                        int j = fut.get();
                        double rowJLeadVal = Math.abs(U.matrix[j][k]);
                        if(rowJLeadVal > largestVal)
                        {
                            largestRow = j;
                            largestVal = rowJLeadVal;
                        }
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (ExecutionException ex)
                    {
                        Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                bigIndecies.clear();
            }
            
            //SWAP!
            U.swapRows(largestRow, k);
            P.swapRows(largestRow, k);
            L.swapRows(largestRow, k);
            
            L.matrix[k][k] = 1;
            //Seting up L 
            for(int threadNumber = 0; threadNumber < maxThreads; threadNumber++)
                bigIndecies.add(threadPool.submit(new LUProwRun(L, U, k, threadNumber)));
        }
        
        
        //Zero out the bottom rows
        for(int k = 0; k < Math.min(rows(), cols()); k++)
            for(int j = 0; j < k; j++)
                U.matrix[k][j] = 0;
        
        
        if(rows() > cols())//Clean up!
        {
            //We need to change U to a square nxn matrix in this case, we can safely drop the last 2 columns!
            double[][] newU = new double[cols()][];
            System.arraycopy(U.matrix, 0, newU, 0, newU.length);
            U = new DenseMatrix(newU);//We have made U point at a new object, but the array is still pointing at the same rows! 
        }
        
        lup[0] = L;
        lup[1] = U;
        lup[2] = P;
        
        return lup;
    }
    
    public Matrix[] qr()
    {
        int N = cols(), M  = rows();
        Matrix[] qr = new Matrix[2];
        
        DenseMatrix Q = Matrix.eye(M);
        DenseMatrix A;
        if(isSquare())
        {
            mutableTranspose();
            A = this;
        }
        else
            A = (DenseMatrix) this.transpose();
        int to = cols() > rows() ? M : N;
        double[] vk = new double[M];
        for(int k = 0; k < to; k++)
        {
            double[] A_k = A.matrix[k];
            double vkNorm = 0, beta = 0;
            
            for(int i = k+1; i < M; i++)
            {
                vk[i] = A_k[i];
                vkNorm += vk[i]*vk[i];
            }
            beta = vkNorm;
            
            double vk_k = vk[k] = A_k[k];//force into register, help the JIT!
            vkNorm += vk_k*vk_k;
            vkNorm = sqrt(vkNorm);
            
            
            double alpha = -signum(vk_k) * vkNorm;
            vk_k  -= alpha;
            vk[k] = vk_k;
            beta += vk_k*vk_k;
            
            
            if(beta == 0)
                continue;
            double TwoOverBeta = 2.0/beta;
            //Computing Q
            {
                //We are computing Q' in what we are treating as the column major order, which represents Q in row major order, which is what we want!
                for(int j = 0; j < Q.cols(); j++)
                {
                    double[] Q_j = Q.matrix[j];
                    double y = 0;//y = vk dot A_j
                    for (int i = k; i < Q.cols(); i++)
                        y += vk[i] * Q_j[i];

                    y *= TwoOverBeta;
                    for (int i = k; i < Q.rows(); i++)
                    {
                        Q_j[i] -= y*vk[i];
                    }
                }
            }
            
            //First run of loop removed, as it will be setting zeros. More accurate to just set them ourselves
            if(k < N)
            {
                double[] A_j = A.matrix[k];
                double y = 0;//y = vk dot A_j
                for(int i = k; i < A.cols(); i++)
                    y += vk[i]*A_j[i];
        
                y *= TwoOverBeta;
                A_j[k] -= y*vk[k];
                
                for(int i = k+1; i < M; i++)
                    A_j[i] = 0.0;
            }
            //The rest of the normal look
            for(int j = k+1; j < N; j++)
            {
                double[] A_j = A.matrix[j];
                double y = 0;//y = vk dot A_j
                for(int i = k; i < A.cols(); i++)
                    y += vk[i]*A_j[i];
        
                y *= TwoOverBeta;
                for(int i = k; i < M; i++)
                    A_j[i] -= y*vk[i];
            }
        }
        qr[0] = Q;
        if(isSquare())
        {
            A.mutableTranspose();
            qr[1] = A;
        }
        else
            qr[1] = A.transpose();
        return qr;
    }
    
    private class QRRun implements Runnable
    {
        DenseMatrix A, Q;
        double[] vk;
        double TwoOverBeta;
        int k, threadID, N, M;
        CountDownLatch latch;

        public QRRun(DenseMatrix A, DenseMatrix Q, double[] vk, double TwoOverBeta, int k, int threadID, CountDownLatch latch)
        {
            this.A = A;
            this.Q = Q;
            this.vk = vk;
            this.TwoOverBeta = TwoOverBeta;
            this.k = k;
            this.threadID = threadID;
            this.latch = latch;
            this.N = A.rows();
            this.M = A.cols();
        }
        
        public void run()
        {
            //Computing Q
            {
                //We are computing Q' in what we are treating as the column major order, which represents Q in row major order, which is what we want!
                for(int j = 0+threadID; j < Q.cols(); j+=maxThreads)
                {
                    double[] Q_j = Q.matrix[j];
                    double y = 0;//y = vk dot A_j
                    for (int i = k; i < Q.cols(); i++)
                        y += vk[i] * Q_j[i];

                    y *= TwoOverBeta;
                    for (int i = k; i < Q.rows(); i++)
                    {
                        Q_j[i] -= y*vk[i];
                    }
                }
            }
            
            //First run of loop removed, as it will be setting zeros. More accurate to just set them ourselves
            if(k < N && threadID == 0)
            {
                double[] A_j = A.matrix[k];
                double y = 0;//y = vk dot A_j
                for(int i = k; i < A.cols(); i++)
                    y += vk[i]*A_j[i];
        
                y *= TwoOverBeta;
                A_j[k] -= y*vk[k];
                
                for(int i = k+1; i < M; i++)
                    A_j[i] = 0.0;
            }
            //The rest of the normal look
            for(int j = k+1+threadID; j < N; j+=maxThreads)
            {
                double[] A_j = A.matrix[j];
                double y = 0;//y = vk dot A_j
                for(int i = k; i < A.cols(); i++)
                    y += vk[i]*A_j[i];
        
                y *= TwoOverBeta;
                for(int i = k; i < M; i++)
                    A_j[i] -= y*vk[i];
            }
            latch.countDown();
        }
        
    }
    
    public Matrix[] qr(ExecutorService threadPool)
    {
        int N = cols(), M  = rows();
        Matrix[] qr = new Matrix[2];
        
        DenseMatrix Q = Matrix.eye(M);
        DenseMatrix A;
        if(isSquare())
        {
            mutableTranspose();
            A = this;
        }
        else
            A = (DenseMatrix) this.transpose();
        
        double[] vk = new double[M];
        
        int to = cols() > rows() ? M : N;
        for(int k = 0; k < to; k++)
        {
            double[] A_k = A.matrix[k];
            double vkNorm = 0, beta = 0;
            
            for(int i = k+1; i < M; i++)
            {
                vk[i] = A_k[i];
                vkNorm += vk[i]*vk[i];
            }
            beta = vkNorm;
            
            double vk_k = vk[k] = A_k[k];
            vkNorm += vk_k*vk_k;
            vkNorm = sqrt(vkNorm);
            
            
            double alpha = -signum(vk_k) * vkNorm;
            vk_k -= alpha;
            beta += vk_k*vk_k;
            vk[k] = vk_k;
            
            
            if(beta == 0)
                continue;
            
            double TwoOverBeta = 2.0/beta;
            
            CountDownLatch latch = new CountDownLatch(maxThreads);
            for(int threadID = 0; threadID < maxThreads; threadID++)
                threadPool.submit(new QRRun(A, Q, vk, TwoOverBeta, k, threadID, latch));
            try
            {
                latch.await();
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(DenseMatrix.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        qr[0] = Q;
        if(isSquare())
        {
            A.mutableTranspose();
            qr[1] = A;
        }
        else
            qr[1] = A.transpose();
        return qr;
    }
    
    @Override
    public Matrix copy()
    {
        DenseMatrix copy = new DenseMatrix(rows(), cols());
        for(int i = 0; i < matrix.length; i++)
            System.arraycopy(matrix[i], 0, copy.matrix[i], 0, matrix[i].length);
        
        return copy;
    }

}
