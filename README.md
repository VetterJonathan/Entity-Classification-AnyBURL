# Entity-Classification-AnyBURL
This is the repository for the bachelor thesis "Entity Classification with AnyBURL". The bachelor thesis itself is given here, as well as additional files and information.

In this file there is a short introduction about the structure of the repository:

## AnyBURL
This folder contains AnyBURL as an application, as well as the used datasets, rules, explanations, etc.
Since this folder is very extensive, there is a second README file in this folder. 

## code
This folder contains the code used to calculate results or prepare data.
For a more detailed description of the code, there is also a README file in this folder.

## Dataset_Informations
The table contains information about the datasets used. 
The following information is shown in Sheet1: Column "Number of different Labels" indicates how many different types / labels exist in a dataset; Column "Size of Training Set" indicates how many triples are contained in a training set; Column "Number of "hasType" Relations in Training Set" indicates how many type classification triples are contained in the training set; Column "Size of Test Set" indicates how many triples are contained in a test set.
Sheet 2 shows the number of rules learned after 100 seconds per dataset.

## Entity_Classification_with_AnyBURL
This is the bachelor thesis

## Explanations
These tables record the explanations for predictions. This means that the table lists all rules that AnyBURL has used to predict at least one The individual sheets each represent one dataset.
The individual columns mean the following: Column "Rules" contains the rules that were used for the classification task; Column "Correct Decision" gives the number of correct predictions based on the associated rule; Column "Wrong Decision" gives the number of wrong predictions based on the associated rule; Column "Frequency" indicates the number of times a rule was used to make a prediction, regardless of whether the prediction was correct or incorrect; Column "Out of" indicates the total number of type predictions, this is used to better classify the column "Frequency"; Column "Rank" indicates the rank of the particular rule compared to all other learned rules. rules. It should be mentioned that all rules with the same confidence value have the same rank.
It should be noted that rules that produce more incorrect predictions than correct predictions are marked in red. Furthermore, for a better assessment, the underlying metric is always listed at the top so that the result can be directly linked.

## Results
This table lists the results of AnyBURL regarding entity classification. 
Sheet "Accuracy Results" shows the results for the datasets "Hepatitis", "Terrorists", "Mutagenesis" and "WebKB". As cross validation was used to calculate the results, the table shows the individual results of the cross validation. Furthermore, there is a comparison with other approaches, the values for which originate from "Dumancic, S., García-Durán, A., & Niepert, M. (2018). A comparative study of distributional and symbolic paradigms for relational learning".
The other sheets show the results for all experiments on the FB15k-237 dataset. Here, the Precision, Recall and F1 Score of the individual types are given, as well as the total Weighted F1 Measure of an experiment. Again, there are other applications for comparison, the data is taken from "Jain, N., Kalo, J. C., Balke, W. T., & Krestel, R. (2021, June). Do embeddings actually capture knowledge graph semantics?". Except the values for "Level-3-Artists", because here was an error, which the authors already confirmed. Therefore, the data here are from the authors of "Ruffinelli, D., Broscheit, S., & Gemulla, R. (2019, September). You can teach an old dog new tricks! on training knowledge graph embeddings"


## Tuning_Results
This table shows all the tuning results that were randomly generated. The individual columns are the individual setting options (for explanations of the values see 4.2.3 in the bachelor thesis). The first column "Round Number" can be seen as a simple ID. The last column "Measure" always shows the value of the underlying metric. So for the experiments on the FB15k-237 dataset the Weighted F1 Measure is used and for the rest the Accuracy.
The default settings are highlighted in blue.



