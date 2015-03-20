# Introduction #

In many applications, the data set that is being used is not intrinsically represented by feature vectors. On such instance is when dealing with text problems. One of the classic examples of this is spam classification. A user receives an email, and we want to automatically move it to a spam folder if it is not desirable. This example will show how to quickly build such a classifier in order to show how to load text data into JSAT.


# Preliminaries #

The data for this problem is a large data sets, the 2005 Trec Spam corpus. It can be downloaded [here](http://plg.uwaterloo.ca/cgi-bin/cgiwrap/gvcormac/foo). This data set is large, and contains 92,189 emails and is over 800 MB on disk. For this reason, make sure to increase the memory allocated to your JVM instance to 2-6 gigabytes depending on how the data set will be loaded, and what model you plan to use.

Human produced text has no fixed format, and text could come from any source. It could be webpages, PDFs, a socket connection, or files on disk. Because of this diversity JSAT exposes text data set creation as abstract classes that need to be implemented by the user. One such class is jsat.text.ClassificationTextDataLoader

## Bag of Words ##

ClassificationTextDataLoader provides a contract for loading several text files as part of a classification data set. It creates a Bag of Words (BoW) model. What this means is that each document will be represented a a vector of values. Each index maps to a specific token, and the value indicates the presence of the token in the document.

In order to break a text into words, the user must supply a Tokenizer (jsat.text.tokenizer.Tokenizer). This specifies how to break a text into different tokens.

The other needed value is a WordWeighting object (jsat.text.wordweighting.WordWeighting). If a token is not present in a document, its value is always zero. But the other question is what value to store if a token does occur. The WordWeighting object is used to control this behavior. For example, you could set the value to 1.0 regardless of how many times the token occurred.

More about tokenization and word weighting will be talked about in another example in the future.

# Loading the 2005 Trec Spam Corpus #

## Trec Format ##

The Trec data set contains an independent file for each and every email. This makes the data set slow to load into memory, as much of the cost of loading a document is opening the file in the first place. This has to be taken into account when we load the data.

To know which emails are spam and ham (good emails that are not spam) the Trec data set provides an index file listing each and every file. It's format is
<Spam or Ham> <relative path to file for email>

## CODE: Implementing ClassificationTextDataLoader ##

This is an implementation of the ClassificationTextDataLoader for the Trec Spam data set.

```
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsat.classifiers.CategoricalData;
import jsat.text.ClassificationTextDataLoader;
import jsat.text.tokenizer.Tokenizer;
import jsat.text.wordweighting.WordWeighting;


/**
 * This is a data set loader for the Trec 2005 Spam data set. 
 * 
 * @author Edward Raff
 */
public class SpamTrec2005Loader extends ClassificationTextDataLoader
{
    private static final Map<String, Integer> classKeys = new HashMap<String, Integer>();
    static
    {
        classKeys.put("spam", 0);
        classKeys.put("ham" , 1);
    }
    
    private File indexFile;

    /**
     * 
     * @param indexFile the file for the index file
     * @param tokenizer the tokenizer to use
     * @param weighting the weighting to apply
     */
    public SpamTrec2005Loader(File indexFile, Tokenizer tokenizer, WordWeighting weighting)
    {
        super(tokenizer, weighting);
        this.indexFile = indexFile;
    }
    
    
    @Override
    protected void setLabelInfo()
    {
        labelInfo = new CategoricalData(2);
        for(Entry<String, Integer> entry : classKeys.entrySet())
            labelInfo.setOptionName(entry.getKey(), entry.getValue());
    }

    @Override
    public void initialLoad()
    {
        try
        {
            char[] buffer = new char[8192*2];
            //Trec index file format is <label> <relativePathToFile>
            BufferedReader br = new BufferedReader(new FileReader(indexFile), 8192*2);
            String line;
            StringBuilder dataFileText = new StringBuilder();
            
            while( (line = br.readLine()) != null)
            {
                String[] split = line.split("\\s+");//split one white space
                
                File dataFile = new File(indexFile.getParentFile(), split[1]);
                
                if(dataFile.exists())
                {
                    FileReader dataFileReader = new FileReader(dataFile);
                    
                    dataFileText.delete(0, dataFileText.length());
                    
                    int readIn;
                    while( (readIn = dataFileReader.read(buffer) ) >= 0 )
                    {
                        dataFileText.append(buffer, 0, readIn);
                    }
  
                    dataFileReader.close();
                    addOriginalDocument(dataFileText.toString(), classKeys.get(split[0]));
                }
                else
                    System.out.println("File " + dataFile + " was not found. Make sure your virus scanner didn't remove it!");
            }
            br.close();
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(SpamTrec2005Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(SpamTrec2005Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

```

ClassificationTextDataLoader requires us to implement two methods.

First is setLabelInfo. This is present to force the implementer to set the labelInfo object which stores the class information for this classification data set.

The other method is initialLoad, which is where all the real work is done. The goal of this method is to load every text document that will become apart of the data set as a String. It must then call addOriginalDocument to add the string to the data set, and indicate what its true class label is.

Some of the work in the code is there for performance reasons. Since the email files are mostly all very small, a separate buffer is manually created instead of using a BufferedReader object. This is to avoid additional unneeded stress on the garbage collector, as each BufferedReader would have created its own buffer for a small file (this avoids over 700 MB of garbage being created).

# Using the data loader #

Now that we have a loader for the data set, we can use the following code to load in the data set, learn a classifier using cross validation, and test it on the data.

It uses a simple Tokenizer and Word Weighting scheme already provided by JSAT.

## CODE ##

```
import java.io.File;
import jsat.classifiers.*;
import jsat.classifiers.bayesian.NaiveBayesUpdateable;
import jsat.text.tokenizer.NaiveTokenizer;
import jsat.text.wordweighting.TfIdf;


/**
 * An example that loads in a text data set of spam and learns a classifier. 
 * 
 * @author Edward Raff
 */
public class SpamTrec2005Example
{
    public static void main(String[] args)
    {
        File trecIndexFile = new File("<path to data>/trec05p-1/full/index");
        
        SpamTrec2005Loader loader = new SpamTrec2005Loader(trecIndexFile, new NaiveTokenizer(), new TfIdf());
        
        ClassificationDataSet cds = loader.getDataSet();
        System.out.println("Data set loaded in");
        System.out.println(cds.getSampleSize() + " data points");
        System.out.println(cds.getNumNumericalVars() + " features");
        
        //ture indicates that the data is sparse and zeros are not important. 
        //This is almost always the case when working with text data
        Classifier classifier = new NaiveBayesUpdateable(true);
        
        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(classifier, cds);
        cme.evaluateCrossValidation(10);
        
        System.out.println("Total Training Time: " + cme.getTotalTrainingTime());
        System.out.println("Total Classification Time: " + cme.getTotalClassificationTime());
        System.out.println("Total Error rate: " + cme.getErrorRate());
        cme.prettyPrintConfusionMatrix();
        
    }
}
```

# Results #
Loading the data set will take most of the time, between 1 - 10 minutes depending on whether you have a solid state or mechanical hard drive. Once loaded, it will perform cross validation with a Naive Bayes classifier. This results in an error rate of around 3%. There are other classifiers in JSAT that can get an even better error rate.

However, you may notice that the data set loaded over 9 million features! This is a large source of memory overhead, as each feature's original string needs to be saved in memory - thats over 9 million string objects that mostly have an observation of 1. This highlights the importance of feature selection. Most of these are caused by random strings in the spam emails of no meaning and are often very long. Because they are random, they only occur once.

One way to deal with this is to create a smarter tokenizer. You can also do feature selection after the fact, but that is slower.

However, you should now have the knowledge necessary to start loading in your own text data sets into JSAT. There are several other abstract data loaders in jsat.text that can be used as well.