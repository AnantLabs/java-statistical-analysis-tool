package jsat.distributions;

import static java.lang.Math.*;

/**
 *
 * @author eman7613
 */
public class ExponentialDistribution extends ContinousDistribution
{
    private double lambda;

    public ExponentialDistribution()
    {
    }

    public ExponentialDistribution(double lambda)
    {
        if(lambda <= 0)
            throw new RuntimeException("The rate parameter must be greater than zero");
        this.lambda = lambda;
    }

    @Override
    public double pdf(double d)
    {
        return lambda*exp(-lambda*d);
    }

    @Override
    public double invPdf(double d)
    {
        return exp(lambda*d)/lambda;
    }

    @Override
    public double cdf(double d)
    {
        return 1-exp(-lambda*d);
    }

    @Override
    public double invCdf(double d)
    {
        return -log(1-d)/lambda;
    }

    @Override
    public double min()
    {
        return 0;
    }

    @Override
    public double max()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public String getDescriptiveName()
    {
        return "Exponential(\u03BB=" + lambda + ")";
    }

    @Override
    public String getDistributionName()
    {
        return "Exponential";
    }

    @Override
    public String[] getVariables()
    {
        return new String[] {"\u03BB"};
    }

    @Override
    public void setVariable(String var, double value)
    {
        if(var.equals("\u03BB"))
        {
            if (value <= 0)
                throw new RuntimeException("The rate parameter must be greater than zero");
            lambda = value;
        }
    }

    @Override
    public ContinousDistribution copy()
    {
        return new ExponentialDistribution(lambda);
    }

}
