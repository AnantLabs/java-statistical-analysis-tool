/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsat.linear;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eman7613
 */
public class DenseMatrixTest
{
    
    /**
     * 5x5
     */
    static DenseMatrix A;
    /**
     * 5x5
     */
    static DenseMatrix B;
    /**
     * 5x7
     */
    static DenseMatrix C;
    
    static DenseMatrix AB;
    static DenseMatrix BA;
    static DenseMatrix AC;
    
    /**
     * Multi threaded pool with daemon threads
     */
    static ExecutorService threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1, new ThreadFactory() {

        public Thread newThread(Runnable r)
        {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });
    
    public DenseMatrixTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        A = new DenseMatrix(new double[][] 
        {
            {1, 5, 4, 8, 9},
            {1, 5, 7, 3, 7},
            {0, 3, 8, 5, 6},
            {3, 8, 0, 7, 0},
            {1, 9, 2, 9, 6}
        } );
        
        B = new DenseMatrix(new double[][] 
        {
            {5, 3, 2, 8, 8},
            {1, 8, 3, 6, 8},
            {1, 2, 6, 5, 4},
            {3, 9, 5, 9, 6},
            {8, 3, 4, 3, 1}
        } );
        
        C = new DenseMatrix(new double[][] 
        {
            {1, 6, 8, 3, 1, 5, 10},
            {5, 5, 3, 7, 2, 10, 0},
            {8, 0, 5, 7, 9, 1, 8},
            {9, 3, 2, 7, 2, 4, 8},
            {1, 2, 6, 5, 8, 1, 9}
        } );
        
        AB = new DenseMatrix(new double[][] 
        {
            {110,   150,   117,   157,   121},
            {82,   105,   102,   121,   101},
            {74,   103,   106,   121,    92},
            {44,   136,    65,   135,   130},
            {91,   178,   110,   171,   148}
        } );
        
        BA = new DenseMatrix(new double[][] 
        {
            {40,   182,    73,   187,   126},
            {35,   174,   100,   161,   131},
            {22,   109,    74,   115,    83},
            {45,   201,   127,   193,   156},
            {21,   100,    87,   123,   123}
        } );
        
        AC = new DenseMatrix(new double[][] 
        {
            {139,    73,   113,   167,   135,   100,   187},
            {116,    54,   106,   143,   136,    81,   153},
            {130,    42,    95,   142,   136,    64,   158},
            {106,    79,    62,   114,    33,   123,    86},
            {149,    90,    99,   173,   103,   139,   152}
        } );
        
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    /**
     * Test of mutableAdd method, of class DenseMatrix.
     */
    @Test
    public void testMutableAdd_Matrix()
    {
        DenseMatrix ApB = new DenseMatrix(new double[][] 
        {
            {6,     8,     6,    16,    17},
            {2,    13,    10,     9,    15},
            {1,     5,    14,    10,    10},
            {6,    17,     5,    16,     6},
            {9,    12,     6,    12,     7}
        } );
        
        Matrix aCopy = A.copy();
        Matrix bCopy = B.copy();
        
        aCopy.mutableAdd(B);
        bCopy.mutableAdd(A);
        
        assertEquals(ApB, aCopy);
        assertEquals(ApB, bCopy);
        
        try
        {
            C.copy().mutableAdd(A);
            fail("Expected error about matrix dimensions"); 
        }
        catch(ArithmeticException ex)
        {
            //Good! We expected failure
        }
    }

    /**
     * Test of mutableAdd method, of class DenseMatrix.
     */
    @Test
    public void testMutableAdd_Matrix_ExecutorService()
    {
        DenseMatrix ApB = new DenseMatrix(new double[][] 
        {
            {6,     8,     6,    16,    17},
            {2,    13,    10,     9,    15},
            {1,     5,    14,    10,    10},
            {6,    17,     5,    16,     6},
            {9,    12,     6,    12,     7}
        } );
        
        Matrix aCopy = A.copy();
        Matrix bCopy = B.copy();
        
        aCopy.mutableAdd(B, threadpool);
        bCopy.mutableAdd(A, threadpool);
        
        assertEquals(ApB, aCopy);
        assertEquals(ApB, bCopy);
        
        try
        {
            C.copy().mutableAdd(A, threadpool);
            fail("Expected error about matrix dimensions"); 
        }
        catch(ArithmeticException ex)
        {
            //Good! We expected failure
        }
    }

    /**
     * Test of mutableAdd method, of class DenseMatrix.
     */
    @Test
    public void testMutableAdd_double()
    {
        DenseMatrix ApTwo = new DenseMatrix(new double[][] 
        {
            {1+2, 5+2, 4+2, 8+2, 9+2},
            {1+2, 5+2, 7+2, 3+2, 7+2},
            {0+2, 3+2, 8+2, 5+2, 6+2},
            {3+2, 8+2, 0+2, 7+2, 0+2},
            {1+2, 9+2, 2+2, 9+2, 6+2}
        } );
        
        Matrix aCopy = A.copy();
        
        aCopy.mutableAdd(2);
        
        assertEquals(ApTwo, aCopy);
    }

    /**
     * Test of mutableAdd method, of class DenseMatrix.
     */
    @Test
    public void testMutableAdd_double_ExecutorService()
    {
        DenseMatrix ApTwo = new DenseMatrix(new double[][] 
        {
            {1+2, 5+2, 4+2, 8+2, 9+2},
            {1+2, 5+2, 7+2, 3+2, 7+2},
            {0+2, 3+2, 8+2, 5+2, 6+2},
            {3+2, 8+2, 0+2, 7+2, 0+2},
            {1+2, 9+2, 2+2, 9+2, 6+2}
        } );
        
        Matrix aCopy = A.copy();
        
        aCopy.mutableAdd(2, threadpool);
        
        assertEquals(ApTwo, aCopy);
    }

    /**
     * Test of mutableSubtract method, of class DenseMatrix.
     */
    @Test
    public void testMutableSubtract_Matrix()
    {
        DenseMatrix AmB = new DenseMatrix(new double[][] 
        {
            {-4,     2,     2,     0,     1},
            { 0,    -3,     4,    -3,    -1},
            {-1,     1,     2,     0,     2},
            { 0,    -1,    -5,    -2,    -6},
            {-7,     6,    -2,     6,     5}
        } );
        
        DenseMatrix BmA = new DenseMatrix(new double[][] 
        {
            {-4*-1,     2*-1,     2*-1,     0*-1,     1*-1},
            { 0*-1,    -3*-1,     4*-1,    -3*-1,    -1*-1},
            {-1*-1,     1*-1,     2*-1,     0*-1,     2*-1},
            { 0*-1,    -1*-1,    -5*-1,    -2*-1,    -6*-1},
            {-7*-1,     6*-1,    -2*-1,     6*-1,     5*-1}
        } );
        
        Matrix aCopy = A.copy();
        Matrix bCopy = B.copy();
        
        aCopy.mutableSubtract(B);
        bCopy.mutableSubtract(A);
        
        assertEquals(AmB, aCopy);
        assertEquals(BmA, bCopy);
        
        try
        {
            C.copy().mutableSubtract(A);
            fail("Expected error about matrix dimensions"); 
        }
        catch(ArithmeticException ex)
        {
            //Good! We expected failure
        }
    }

    /**
     * Test of mutableSubtract method, of class DenseMatrix.
     */
    @Test
    public void testMutableSubtract_Matrix_ExecutorService()
    {
        DenseMatrix AmB = new DenseMatrix(new double[][] 
        {
            {-4,     2,     2,     0,     1},
            { 0,    -3,     4,    -3,    -1},
            {-1,     1,     2,     0,     2},
            { 0,    -1,    -5,    -2,    -6},
            {-7,     6,    -2,     6,     5}
        } );
        
        DenseMatrix BmA = new DenseMatrix(new double[][] 
        {
            {-4*-1,     2*-1,     2*-1,     0*-1,     1*-1},
            { 0*-1,    -3*-1,     4*-1,    -3*-1,    -1*-1},
            {-1*-1,     1*-1,     2*-1,     0*-1,     2*-1},
            { 0*-1,    -1*-1,    -5*-1,    -2*-1,    -6*-1},
            {-7*-1,     6*-1,    -2*-1,     6*-1,     5*-1}
        } );
        
        Matrix aCopy = A.copy();
        Matrix bCopy = B.copy();
        
        aCopy.mutableSubtract(B, threadpool);
        bCopy.mutableSubtract(A, threadpool);
        
        assertEquals(AmB, aCopy);
        assertEquals(BmA, bCopy);
        
        try
        {
            C.copy().mutableSubtract(A, threadpool);
            fail("Expected error about matrix dimensions"); 
        }
        catch(ArithmeticException ex)
        {
            //Good! We expected failure
        }
    }

    /**
     * Test of multiply method, of class DenseMatrix.
     */
    @Test
    public void testMultiply_Vec()
    {
        DenseVector b = new DenseVector(Arrays.asList(4.0, 5.0, 2.0, 6.0, 7.0));
        
        DenseVector z = new DenseVector(Arrays.asList(2.0, 1.0, 2.0, 3.0, 4.0, 5.0, 0.0));
        
        DenseVector Ab = new DenseVector(Arrays.asList(148.0, 110.0, 103.0, 94.0, 149.0));
        
        assertEquals(Ab, A.multiply(b));
        
        DenseVector Cz = new DenseVector(Arrays.asList(62.0, 100.0, 88.0, 74.0, 68.0));
        
        assertEquals(Cz, C.multiply(z));
    }

    /**
     * Test of multiply method, of class DenseMatrix.
     */
    @Test
    public void testMultiply_Vec_ExecutorService()
    {
        DenseVector b = new DenseVector(Arrays.asList(4.0, 5.0, 2.0, 6.0, 7.0));
        
        DenseVector z = new DenseVector(Arrays.asList(2.0, 1.0, 2.0, 3.0, 4.0, 5.0, 0.0));
        
        DenseVector Ab = new DenseVector(Arrays.asList(148.0, 110.0, 103.0, 94.0, 149.0));
        
        assertEquals(Ab, A.multiply(b, threadpool));
        
        DenseVector Cz = new DenseVector(Arrays.asList(62.0, 100.0, 88.0, 74.0, 68.0));
        
        assertEquals(Cz, C.multiply(z, threadpool));
    }
    
    /**
     * Test of multiply method, of class DenseMatrix.
     */
    @Test
    public void testMultiply_Matrix()
    {
        Matrix result;
        
        result = A.multiply(B);
        assertEquals(AB, result);
        
        result = B.multiply(A);
        assertEquals(BA, result);
        
        result = A.multiply(C);
        assertEquals(AC, result);
        
        try
        {
            C.multiply(A);
            fail("Expected error about matrix dimensions"); 
        }
        catch(ArithmeticException ex)
        {
            //Good! We expected failure
        }
    }

    /**
     * Test of multiply method, of class DenseMatrix.
     */
    @Test
    public void testMultiply_Matrix_ExecutorService()
    {
        Matrix result;
        
        result = A.multiply(B, threadpool);
        assertEquals(AB, result);
        
        result = B.multiply(A, threadpool);
        assertEquals(BA, result);
        
        result = A.multiply(C, threadpool);
        assertEquals(AC, result);
        
        try
        {
            C.multiply(A, threadpool);
            fail("Expected error about matrix dimensions"); 
        }
        catch(ArithmeticException ex)
        {
            //Good! We expected failure
        }
    }

    /**
     * Test of mutableMultiply method, of class DenseMatrix.
     */
    @Test
    public void testMutableMultiply_double()
    {
        DenseMatrix AtTwo = new DenseMatrix(new double[][] 
        {
            {1*2, 5*2, 4*2, 8*2, 9*2},
            {1*2, 5*2, 7*2, 3*2, 7*2},
            {0*2, 3*2, 8*2, 5*2, 6*2},
            {3*2, 8*2, 0*2, 7*2, 0*2},
            {1*2, 9*2, 2*2, 9*2, 6*2}
        } );
        
        Matrix aCopy = A.copy();
        
        aCopy.mutableMultiply(2);
        
        assertEquals(AtTwo, aCopy);
    }

    /**
     * Test of mutableMultiply method, of class DenseMatrix.
     */
    @Test
    public void testMutableMultiply_double_ExecutorService()
    {
        DenseMatrix AtTwo = new DenseMatrix(new double[][] 
        {
            {1*2, 5*2, 4*2, 8*2, 9*2},
            {1*2, 5*2, 7*2, 3*2, 7*2},
            {0*2, 3*2, 8*2, 5*2, 6*2},
            {3*2, 8*2, 0*2, 7*2, 0*2},
            {1*2, 9*2, 2*2, 9*2, 6*2}
        } );
        
        Matrix aCopy = A.copy();
        
        aCopy.mutableMultiply(2, threadpool);
        
        assertEquals(AtTwo, aCopy);
    }

    /**
     * Test of transpose method, of class DenseMatrix.
     */
    @Test
    public void testTranspose()
    {
        DenseMatrix CTranspose = new DenseMatrix(new double[][] 
        {
            {1, 5, 8, 9, 1},
            {6, 5, 0, 3, 2},
            {8, 3, 5, 2, 6},
            {3, 7, 7, 7, 5},
            {1, 2, 9, 2, 8}, 
            {5, 10, 1, 4, 1},
            {10, 0, 8, 8, 9}
        } );
        
        assertEquals(CTranspose, C.transpose());
    }

    /**
     * Test of get method, of class DenseMatrix.
     */
    @Test
    public void testGet()
    {
        //Tests both
        testSet();
    }

    /**
     * Test of set method, of class DenseMatrix.
     */
    @Test
    public void testSet()
    {
        DenseMatrix toSet = new DenseMatrix(A.rows(), A.cols());
        
        for(int i = 0; i < A.rows(); i++)
            for(int j = 0; j < A.cols(); j++)
                toSet.set(i, j, A.get(i, j));
        
        assertEquals(A, toSet);
    }

    /**
     * Test of rows method, of class DenseMatrix.
     */
    @Test
    public void testRows()
    {
        assertEquals(5, A.rows());
    }

    /**
     * Test of cols method, of class DenseMatrix.
     */
    @Test
    public void testCols()
    {
        assertEquals(5, A.cols());
        assertEquals(7, C.cols());
    }

    /**
     * Test of isSparce method, of class DenseMatrix.
     */
    @Test
    public void testIsSparce()
    {
        assertEquals(false, A.isSparce());
    }

    /**
     * Test of nnz method, of class DenseMatrix.
     */
    @Test
    public void testNnz()
    {
        assertEquals(5*5, A.nnz());
        assertEquals(5*7, C.nnz());
    }

    /**
     * Test of copy method, of class DenseMatrix.
     */
    @Test
    public void testCopy()
    {
        Matrix ACopy = A.copy();
        
        assertEquals(A, ACopy);
        assertEquals(A.multiply(B), ACopy.multiply(B));
    }

    /**
     * Test of swapRows method, of class DenseMatrix.
     */
    @Test
    public void testSwapRows()
    {
        System.out.println("swapRows");
        
        Matrix Expected = new DenseMatrix(new double[][] 
        {
            {5, 5, 3, 7, 2, 10, 0},
            {1, 2, 6, 5, 8, 1, 9},
            {8, 0, 5, 7, 9, 1, 8},
            {9, 3, 2, 7, 2, 4, 8},
            {1, 6, 8, 3, 1, 5, 10}
        } );
        
        Matrix test = C.copy();
        
        
        test.swapRows(1, 0);
        test.swapRows(1, 0);
        assertEquals(C, test);
        test.swapRows(0, 1);
        test.swapRows(0, 1);
        assertEquals(C, test);
        test.swapRows(3, 3);
        assertEquals(C, test);
        
        
        test.swapRows(0, 4);
        test.swapRows(0, 1);
        assertEquals(Expected, test);
        
        
        test = C.copy();
        test.swapRows(4, 0);
        test.swapRows(1, 0);
        assertEquals(Expected, test);
    }

    /**
     * Test of zeroOut method, of class DenseMatrix.
     */
    @Test
    public void testZeroOut()
    {
        System.out.println("zeroOut");
        
        Matrix test = C.copy();
        test.zeroOut();
        
        for(int i = 0; i < test.rows(); i++)
            for(int j = 0; j < test.cols(); j++)
                assertEquals(0, test.get(i, j), 0);
    }

    /**
     * Test of lup method, of class DenseMatrix.
     */
    @Test
    public void testLup_0args()
    {
        System.out.println("lup");
        
        Matrix[] lup;
        
        lup = A.copy().lup();
        assertTrue(lup[2].multiply(A).equals(lup[0].multiply(lup[1]), 1e-14));
        
        lup = C.copy().lup();
        assertTrue(lup[2].multiply(C).equals(lup[0].multiply(lup[1]), 1e-14));
        
        
        lup = C.transpose().lup();
        assertTrue(lup[2].multiply(C.transpose()).equals(lup[0].multiply(lup[1]), 1e-14));
    }

    /**
     * Test of lup method, of class DenseMatrix.
     */
    @Test
    public void testLup_ExecutorService()
    {
        System.out.println("lup");
        
        Matrix[] lup;
        
        lup = A.copy().lup(threadpool);
        assertTrue(lup[2].multiply(A, threadpool).equals(lup[0].multiply(lup[1], threadpool), 1e-14));
        
        lup = C.copy().lup(threadpool);
        assertTrue(lup[2].multiply(C, threadpool).equals(lup[0].multiply(lup[1], threadpool), 1e-14));
        
        
        lup = C.transpose().lup(threadpool);
        assertTrue(lup[2].multiply(C.transpose(), threadpool).equals(lup[0].multiply(lup[1], threadpool), 1e-14));
    }
}
