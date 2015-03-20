JSAT is a library for quickly getting started with Machine Learning problems. It is developed in my free time, and made available for use under the GPL 3. Part of the library is for self education, as such - all code is self contained. JSAT has no external dependencies, and is pure Java. I also aim to make the library suitably fast for small to medium size problems. As such, much of the code supports parallel execution.

You can include JSAT in your maven project. I will try to keep the maven repo fairly up-to-date, as I encourage using the head revision as the most feature rich & bug free. Google code has disable adding new downloads. If you dont want to use maven simple go the [repo directory](http://www.edwardraff.com/maven-repo/jsat/jsat/) and download the jars from the highest revision number.

```
<repositories>
    <repository>
        <id>edwardraff-repo</id>
        <url>http://www.edwardraff.com/maven-repo/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>jsat</groupId>
        <artifactId>jsat</artifactId>
        <version>LATEST</version>
    </dependency>
</dependencies>
```

If you want to use JSAT and the GPL is not something that will work for you, let me know and we can discus the issue.

To learn about using the library functionality of JSAT, look at the [Examples](Examples.md), you can also learn some more from the [blog](http://jsatml.blogspot.com/) I've started. Note: if you leave a comment on the wiki, Google code will **not** email me about it. While I do check the wiki for comments, you will get a much faster response by emailing me.

JSAT was written to Java 6, with revision [r891](https://code.google.com/p/java-statistical-analysis-tool/source/detail?r=891) the last version I plan on making sure is Java 6 compatiable. Due to a bug in Oracle's  JDK, the default java 6 JDK can not compile JSAT. This bug has been marked as "will not fix". You can compile JSAT with JDK 7, or the eclipse / IBM java 6 compilers.

JSAT will be switching over to JDK 7 in the near future. JDK 8 will depend on adoption rate.

JSAT has numerous algorithms implemented in it, including multiple implementations of the same algorithm. These exist for many reasons. Some times they are useful performance base lines or often used as comparisons in research. Some are not useful in practice, but have academic usefulness or compute an exact solution for comparison. Most importantly, not all training methods work best in every situation. The best implementation / training method can be chosen for a particular problem.

NOTE: Please do not contact me through social media sites. I much prefer email. If you email me at Raff.Edward@gmail.com you will get a response far faster.

The list of algorithms bellow is no longer complete. To see a more complete list, download the JavaDocs and look at all classes inheriting from the Classifier interface

| **Algorithms** | **Classification** | **Regression** |
|:---------------|:-------------------|:---------------|
| Rocchio | Y | N |
| k-Nearest Neighbor | Y | Y |
| Kernel Density Estimator | Y | Y |
| DANN | Y | N |
| Kriging | N | Y |
| Multivariate Normals | Y | N |
| Naive Bayes | Y | N |
| AODE | Y | N |
| K2-Bayes Net| Y | N |
| ArcX4 | Y | N |
| AdaBoostM1 | Y | N |
| AdaBoostM1PL | Y | N |
| LogitBoost |Y | N |
| LogitBoostPL | Y | N |
| SAMME | Y | N |
| Plat's SMO SVM | Y | Y |
| Pegasos | Y | N |
| DCD SVM | Y | N |
| SMIDAS | Y | Y |
| Multiple Linear Regression | N | Y |
| Logistic Regression (L1 and L2 regularized) | Y | Y |
| Perceptron | Y | N |
| Back Propagation Neural network | Y | Y |
| LVQ | Y | N |
| SOM | Y | N |
| ID3 | Y | N |
| Decision Stump | Y | Y |
| Decision Tree| Y | Y |
| Random Forrest| Y | Y |
| Extra Tree | Y | Y |
| Extra Trees | Y | Y |
| Stochastic Gradient Boosting  | N | Y |

The following non-boosting meta algorithms are implemented
| **Algorithms** | **Classification** | **Regression** |
|:---------------|:-------------------|:---------------|
| Bagging | Y | Y |
| Wagging | Y | Y |
| Locally Weighted Learner | Y | Y |
| One-vs-All | Y | N |
| Onve-vs-One | Y | N |
| DDAG | Y | N |
| RANSAC | N | Y |
| Grid Search | Y | Y |


The following clustering algorithms are implemented
| **Algorithms** |
|:---------------|
| PAM |
| CLARA |
| k-Means |
| Mini-Batch k-Means |
| EM-Gaussian Mixture |
| DBSCAN |
| LSDBC |
| FLAME |
| OPTICS |
| Mean Shift|
| HAC |

The following feature selection and data transforms are implemented
| **Transforms** |
|:---------------|
| Johnson-Lindenstrauss |
| Linear Scaling |
| Nomincal to Numeric |
| Numeric to Histogram |
| PCA |
| Normalization |
| Polynomial |
| Remove Attribute |
| Zero Mean |
| Unit Variance |
| Whitened PCA |
| Whitened ZCA |
|RBF Random Fourier Features|
|Nystroem Transform |


| **Feature Selection** |
|:----------------------|
| Sequential Forward Selection |
| Sequential Backward Selection |
| Bidirectional Search|
| plus-L minus-R Selection |
| Mutual Information |
| ReliefF |