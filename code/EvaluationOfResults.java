import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class EvaluationOfResults {

  /**
   * Path to a single prediction file
   */
  public static final String resultPath = "C:\\Users\\testUser\\Desktop\\AnyBURL\\predictions\\alpha-10";;

  /**
   * Path where the file containing the results is to be saved
   */
  public static final String outputPath = "C:\\Users\\testUser\\Desktop\\AnyBURL\\results\\lol.txt";

  /**
   * Is true if the underlying dataset uses Accuracy as metric and false if it uses the Weighted F1
   * Measure as metric.
   */
  private static final Boolean accuracyMetric = true;

  /**
   * Specifies whether the result should be written to a file (if True then it is written to a
   * file). If False, only the value of the metric is written to the command line and is not saved
   * to a file, and no additional values are displayed in the output
   */
  private static final Boolean getWrittenOutput = true;

  /**
   * List of all names of the different types that are present in the dataset currently in use.
   * The names of the types must be exactly the same as in the data. This variable only needs to
   * be adjusted if the Weighted F1 metric needs to be calculated
   */
  private static final String[] types = {"Type1", "Type2", "Type3" , "Type4", "Type5"};

  /*
  For simplicity, list of types, per dataset
  (as we have used them, so a possible check if this is the case would be useful):

  Hepatitis: {"TYPE_B", "TYPE_C"}
  Terrorists: {"Weapon_Attack", "Kidnapping", "Bombing" , "Arson", "other_attack", "NBCR_Attack"}
  Mutagenesis: {"Mutagenic_no", "Mutagenic_yes"}
  WebKB: {"Course", "Department", "Faculty" , "Person", "ResearchProject", "Staff", "Student"}

  FB15k-237
  Level-1: {"Type1", "Type2", "Type3"}
  Level-2-Organizations: {"Type1", "Type2", "Type3" , "Type4"}
  Level-2-Persons: {"Type1", "Type2", "Type3" , "Type4", "Type5"}
  Level-3-Artists: {"Type1", "Type2"}
   */

  /**
   * Main method, which performs the desired task based on the parameters selected above.
   */
  public static void main(String args[]){
    if(accuracyMetric){
      if(getWrittenOutput){
        getAccuracyExtended(resultPath, outputPath);
      } else {
        System.out.println(getAccuracyValue(resultPath));
      }
    } else {
      if(getWrittenOutput) {
        getWeightedF1(resultPath, outputPath, types, true);
      } else {
        System.out.println(getWeightedF1(resultPath, outputPath, types, false));
      }
    }

  }

  /**
   * This method calculates the accuracy of a prediction, the average position of the correct
   * answer, and the average confidence of the correct answer.
   *
   * @param resultPath Path to a single prediction file
   * @param outputPath path where the accuracy results will be saved
   */
  public static void getAccuracyExtended(String resultPath, String outputPath) {
    int helpCounter = 0; //Value to help select the correct lines
    int rowCounter = 0; //Counts the number of lines or, more precisely, the number of predictions
    int correctPredictions = 0; //counts the number of correct predictions
    int averagePositionOfPrediction = 0; //counts the position of the correct prediction
    double averageConfidenceOfPrediction = 0.0; //counts the confidence of the correct prediction
    boolean continuation = false; //indicates which lines still count towards the same prediction
    String givenLabel = ""; //stores the given label
    StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters

    try {
      File file = new File(resultPath);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      String line;
      while ((line = br.readLine()) != null) {

        //True if there is a type prediction in the line or we are still in the associated lines
        // of a type prediction
        if (line.contains("hasType") || continuation) {
          //True if it is the first line of a prediction
          if (helpCounter == 0) {
            //Divides the line into different parts to identify the searched type
            String[] parts = line.split(Pattern.quote(" "));
            givenLabel = parts[2];
            helpCounter++;
            continuation = true;

          //True when we are in the second associated line of a prediction
          //In this one are the tail predictions
          } else if (helpCounter == 2) {
            //Divides line into different parts to identify if this type matches the searched type
            String[] resultParts = line.split(Pattern.quote(" "));
            String[] finalResult = resultParts[1].split(Pattern.quote("\t"));

            //True if the first result of the tail prediction is equal to the type searched for
            if (givenLabel.equals(finalResult[0])) {
              correctPredictions++;
              averageConfidenceOfPrediction += Double.parseDouble(finalResult[1]);

            } else {
              int countPos = 2; //Counter to start at the second prediction
              //If first result does not match, search continues until the correct result is found
              for (int j = 2; j < finalResult.length; j = j + 2) {
                if (givenLabel.equals(finalResult[j])) {
                  averagePositionOfPrediction += countPos;
                  averageConfidenceOfPrediction += Double.parseDouble(finalResult[j + 1]);
                }
                countPos++;
              }
            }
            //A prediction was fully processed, resets values and increases number of rows
            rowCounter++;
            helpCounter = 0;
            continuation = false;
          //This case occurs for the irrelevant, head predictions, therefore nothing is calculated
          } else {
            helpCounter++;
          }
        }
      }
      fr.close();    //closes the stream and release the resources

      //Select the appropriate name, depending on the round
      sb.append("Results:\n");
      //Write and calculate the values
      sb.append("Number of Predictions: " + rowCounter + "\nNumber of correct Predictions: "
          + correctPredictions + "\nAccuracy: " + ((double) correctPredictions
          / (double) rowCounter) + "\nAverage Position of correct Answer: "
          + (double)(averagePositionOfPrediction + correctPredictions)/ (double) rowCounter
          + "\nAverage Confidence of correct Answer: "
          + (averageConfidenceOfPrediction/ (double) rowCounter));
      sb.append("\n\n");  //line feed

    } catch (IOException e) {
      e.printStackTrace();
    }

    //Write to the file
    writeToFile(sb, outputPath);
  }

  /**
   * Method that calculates the accuracy for a prediction and returns this value.
   *
   * @param path Path to the predictions file
   * @return Accuracy of the given prediction
   */
  public static double getAccuracyValue(String path) {
    int helpCounter = 0; //Value to help select the correct lines
    int rowCounter = 0; //Counts the number of lines or, more precisely, the number of predictions
    int correctPredictions = 0; //counts the number of correct predictions
    boolean continuation = false; //indicates which lines still count towards the same prediction
    String givenLabel = ""; //stores the given label

      try {
        File file = new File(path);    //creates a new file instance
        FileReader fr = new FileReader(file);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line;
        while ((line = br.readLine()) != null) {
          //True if there is a type prediction in the line or we are still in the associated lines
          // of a type prediction
          if (line.contains("hasType") || continuation) {
            //True if it is the first line of a prediction
            if (helpCounter == 0) {
              //Divides the line into different parts to identify the searched type
              String[] parts = line.split(Pattern.quote(" "));
              givenLabel = parts[2];
              helpCounter++;
              continuation = true;

            //True when we are in the second associated line of a prediction
            //In this one are the tail predictions
            } else if (helpCounter == 2) {
              //Divides line into different parts to identify if this type matches the searched type
              String[] resultParts = line.split(Pattern.quote(" "));
              String[] finalResult = new String[2];
              if(resultParts.length > 1){
                finalResult = resultParts[1].split(Pattern.quote("\t"));
              }
              //True if the first result of the tail prediction is equal to the type searched for
              if (givenLabel.equals(finalResult[0])) {
                correctPredictions++;
              }
              //A prediction was fully processed, resets values and increases number of rows
              rowCounter++;
              helpCounter = 0;
              continuation = false;

            //This case occurs for the irrelevant, head predictions, therefore nothing is calculated
            } else {
              helpCounter++;
            }
          }

        }
        fr.close();    //closes the stream and release the resources

      } catch (IOException e) {
        e.printStackTrace();
      }

     //Calculate accuracy
     return ((double) correctPredictions / (double) rowCounter);
  }


  /**
  * Calculates the Weighted F1 metric and returns it. Depending on the setting of the parameters,
   * the method writes them to a file, as well as the F1 score, precision and recall for the
   * respective type.
  *
  * @param resultPath Path to the predictions file
  * @param outputPath Path where the weighted F1 measure results will be saved
  * @param differentLabels An array containing the names of the different labels; the names must
   *                        exactly match those from the data
  * @param getOutputFile If this is true, an output file is created, otherwise not
  * @return the Weighted F1 Measure of the prediction
  */
  public static double getWeightedF1(String resultPath, String outputPath, String[] differentLabels,
      Boolean getOutputFile) {

    //constructs a matrix to calculate the metric
    int[][] confusionMatrix = new int[differentLabels.length][differentLabels.length];
    int columnIndex = -1; //Index indicating the column, initialised with dummy value
    int rowIndex = -1; //Index indicating the row, initialised with dummy value
    StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
    try {
      File file = new File(resultPath);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      String line;
      while ((line = br.readLine()) != null) {
        if (line.contains("hasType")) {
          //Divides the line into different parts to identify the searched type
          String[] parts = line.split(Pattern.quote(" "));
          for(int i = 0; i < differentLabels.length; i++){
            //Search the matching label for the type in the array of all labels
            if(parts[2].equals(differentLabels[i])){
              columnIndex = i;
            }
          }
          br.readLine(); //read line command, to skip the head prediction line
          line = br.readLine(); //Overwrite line with Tail Prediction

          //Line is split to find AnyBURLs best prediction
          String[] resultParts = line.split(Pattern.quote(" "));
          String[] finalResult = resultParts[1].split(Pattern.quote("\t"));
          for(int j = 0; j < differentLabels.length; j++){
            //Search the matching label for the type in the array of all labels
            if(finalResult[0].equals(differentLabels[j])){
              rowIndex = j;
            }
          }
          if(rowIndex < 0 || columnIndex < 0){ //check if the values are correct
            System.out.println("A Mistake was Made!");
          } else {
            //Increase matching entry in the matrix and reset values
            confusionMatrix[rowIndex][columnIndex]++;
            rowIndex = -1;
            columnIndex = -1;
          }
        }
      }
      fr.close();    //closes the stream and release the resources

    } catch (IOException e) {
      e.printStackTrace();
    }

    // Matrix with Precision, Recall and F1 Score
    // Column 0 is Precision, Column 1 is Recall, Column 2 is F1 Score
    double[][] precisionRecallF1Matrix = new double[differentLabels.length][3];
    double helpAllPredicted = 0;
    double helpAllActual = 0;
    for (int i = 0; i < differentLabels.length; i++) {
      for (int j = 0; j < differentLabels.length; j++){
        helpAllActual += confusionMatrix[j][i];
        helpAllPredicted += confusionMatrix[i][j];
      }
      // compute Precision
      precisionRecallF1Matrix[i][0] = (double) confusionMatrix[i][i] / helpAllPredicted;
      // compute Recall
      precisionRecallF1Matrix[i][1] = (double) confusionMatrix[i][i] / helpAllActual;
      //compute F1
      precisionRecallF1Matrix[i][2] = 2 * (precisionRecallF1Matrix[i][0]
          * precisionRecallF1Matrix[i][1]) / (precisionRecallF1Matrix[i][0]
          + precisionRecallF1Matrix[i][1]);

      //Write Results in StringBuffer
      sb.append("Entity Class:\t" + differentLabels[i] + "\nPrecision:\t"
          + precisionRecallF1Matrix[i][0]
          + "\nRecall:\t\t" + precisionRecallF1Matrix[i][1] + "\nF1-score:\t"
          + precisionRecallF1Matrix[i][2] + "\n\n");

      helpAllActual = 0;
      helpAllPredicted = 0;
    }

    //compute weighted F1 Score
    double numberOfAllEntities = 0;
    double sumWeightedF1Scores = 0;
    for (int i = 0; i < differentLabels.length; i++){
      for (int j = 0; j < differentLabels.length; j++){
        helpAllActual += confusionMatrix[j][i];
      }
      numberOfAllEntities += helpAllActual;
      sumWeightedF1Scores += (helpAllActual * precisionRecallF1Matrix[i][2]);
      helpAllActual = 0;
    }

    //Write Result in StringBuffer and compute Weighted F1 Measure
    sb.append("-------------------------------------\nWeighted-F1:\t"
        + (sumWeightedF1Scores / numberOfAllEntities));


    if(getOutputFile) {
      //Writing StringBuffer in new FIle
      writeToFile(sb,outputPath);
    }
    return (sumWeightedF1Scores / numberOfAllEntities);
  }


  /**
   * Writes a given StringBuffer to a file, at the desired location
   *
   * @param sb   The StringBuffer which contains the text for the file
   * @param path The location to which the file is to be written
   */
  private static void writeToFile(StringBuffer sb, String path) {
    BufferedWriter bwr = null;
    try {
      bwr = new BufferedWriter(new FileWriter(new File(path)));

      //write contents of StringBuffer to a file
      bwr.write(sb.toString());

      //flush the stream
      bwr.flush();

      //close the stream
      bwr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }





}



