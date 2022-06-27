# code
Now follows a short introduction to the code:
Generally speaking, one should compile the code in the command line before each execution ("javac *.java") in order to be able to execute it afterwards ("java program_name" without    .java extension).
Attributes that users should / can change are always the attributes that are given at the beginning of a class and are declared with "private static final". All variables have an explanation and are initialised with an example value. Therefore, the explanations in this README file are rather short. 
It is important to note that each class has a "main method" and can be executed to achieve the desired task.

## AnalyzeExplanations
This class converts an explanation file (inputPath) from AnyBURL into a filtered and partially analysed version.

## Create Valid
This class converts a given training set (input) into a validation set consisting of 10% of the type assignments (outputValid) and a new training set (outputTrain) for the validation set.

## EvaluationOfResults
This class calculates the desired metrics of the desired datasets. 

## RandomTest
This class is used to automatically calculate tuning results. The given "private static final" attributes must be set and then the tuning process runs automatically. However, one still has to set a little something outside of the code: In the "config-learn_tune.properties" and "config-apply_tune.properties", the paths to the training sets and test sets must be adjusted in order to also test values for this dataset.
When choosing the number of runs, it should be noted that learning the rules always takes at least 100 seconds and that the prediction can also vary depending on the computing power and size of the test set.

## RankExplanations
This class calculates the rank of rules. It is important that all information about the choice of attributes is followed, otherwise the ranking would no longer be correct.

## Transformation
This class transforms the original data format into a data format that AnyBURL can use.