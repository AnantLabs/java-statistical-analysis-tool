/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsat.math.rootfinding;

import jsat.math.rootfinding.Bisection;
import jsat.math.Function;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static java.lang.Math.*;

/**
 *
 * @author eman7613
 */
public class BisectionTest
{
    /**
     * Root at 0
     */
    Function sinF = new Function() {

        public double f(double... x)
        {
            return sin(x[0]);
        }
    };
    
    /**
     * Root at 0 + 2nd param
     */
    Function sinFp1 = new Function() {

        public double f(double... x)
        {
            return sin(x[0]+x[1]);
        }
    };
    
    /**
     * Root at approx -4.87906
     */
    Function polyF = new Function() {

        public double f(double... x)
        {
            double xp = x[0];
            
            return pow(xp, 3)+5*pow(xp,2)+xp+2;
        }
    };
    
    public BisectionTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }
    
    @Before
    public void setUp()
    {
    }

    /**
     * Test of root method, of class Bisection.
     */
    @Test
    public void testRoot_4args()
    {
        System.out.println("root");
        double eps = 1e-15;
        double result = Bisection.root(-PI/2, PI/2, sinF);
        assertEquals(0, result, eps);
        
        result = Bisection.root(-6, 6, polyF);
        assertEquals(-4.8790576334840479813, result, eps);
        
        result = Bisection.root(-6, 6, polyF, 0);
        assertEquals(-4.8790576334840479813, result, eps);
        
        
        result = Bisection.root(-PI / 2, PI / 2, sinFp1, 0.0, 1.0);
        assertEquals(-1.0, result, eps);
        
        try
        {
            result = Bisection.root(-PI / 2, PI / 2, sinFp1);
            fail("Should not have run");
        }
        catch (Exception ex)
        {
        }
    }

    /**
     * Test of root method, of class Bisection.
     */
    @Test
    public void testRoot_5args()
    {
        System.out.println("root");
        double eps = 1e-15;
        double result = Bisection.root(eps, -PI/2, PI/2, sinF);
        assertEquals(0, result, eps);
        
        result = Bisection.root(eps, -6, 6, polyF);
        assertEquals(-4.8790576334840479813, result, eps);
        
        result = Bisection.root(eps, -6, 6, polyF, 0);
        assertEquals(-4.8790576334840479813, result, eps);
        
        
        result = Bisection.root(eps, -PI / 2, PI / 2, sinFp1, 0.0, 1.0);
        assertEquals(-1.0, result, eps);
        
        try
        {
            result = Bisection.root(eps, -PI / 2, PI / 2, sinFp1);
            fail("Should not have run");
        }
        catch (Exception ex)
        {
        }
    }
    
    @Test
    public void testRoot_6args()
    {
        System.out.println("root");
        double eps = 1e-15;
        double result = Bisection.root(eps, -PI/2, PI/2, sinF, 0);
        assertEquals(0, result, eps);
        
        result = Bisection.root(eps, -PI/2, PI/2, sinFp1, 0, 0.0, 1.0);
        assertEquals(-1.0, result, eps);
        
        result = Bisection.root(eps, -PI/2, PI/2, sinFp1, 1, 3.0, 0.0);
        assertEquals(PI-3.0, result, eps);
        
        result = Bisection.root(eps, -6, 6, polyF, 0);
        assertEquals(-4.8790576334840479813, result, eps);
    }

    /**
     * Test of root method, of class Bisection.
     */
    @Test
    public void testRoot_7args()
    {
        System.out.println("root");
        double eps = 1e-13;
        int maxIterations = 1000;
        double result = Bisection.root(eps, maxIterations, -PI/2, PI/2, sinF, 0);
        assertEquals(0, result, eps);
        
        result = Bisection.root(eps, maxIterations, -PI/2, PI/2, sinFp1, 0, 0.0, 1.0);
        assertEquals(-1.0, result, eps);
        
        result = Bisection.root(eps, maxIterations, -PI/2, PI/2, sinFp1, 1, 3.0, 0.0);
        assertEquals(PI-3.0, result, eps);
        
        result = Bisection.root(eps, maxIterations, -6, 6, polyF, 0);
        assertEquals(-4.8790576334840479813, result, eps);
    }
}
