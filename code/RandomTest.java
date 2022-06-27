import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class RandomTest {


  /**
   * The path where the tuning results will be saved.
   */
  private static final String outputPathTuning = "C:\\Users\\testUser\\Desktop\\AnyBURL\\tuning\\tuning_results.csv";

  /**
   * Path to the "config-learn_tune.properties" file which is used for learning. (The file must
   * contain the following lines: POLICY, REWARD, EPSILON, THRESHOLD_CORRECT_PREDICTIONS,
   * THRESHOLD_CONFIDENCE, MAX_LENGTH_CYCLIC, MAX_LENGTH_ACYCLIC)
   */
  private static final String pathConfigFile = "C:\\Users\\testUser\\Desktop\\AnyBURL\\config-learn_tune.properties";

  /**
   * Path to the folder where the AnyBURL application and configuration files are located.
   * The start of the path is "C:\", so only the path from this starting point must be specified.
   */
  private static final String pathAnyBURL = "Users\\testUser\\Desktop\\AnyBURL";

  /**
   * Path to the predictions of AnyBURL (see configuration file). The exact prediction file must
   * always be linked to.
   */
  private static final String pathPredictions = "C:\\Users\\testUser\\Desktop\\AnyBURL\\predictions\\alpha-100";

  /**
   * Indicates the number of runs
   */
  private static final Integer numberOfRuns = 150;

  /**
   * Is true if the underlying dataset uses Accuracy as metric and false if it uses the Weighted F1
   * Measure as metric.
   */
  private static final Boolean accuracyMetric = true;

  /**
   * List of all names of the different types that are present in the dataset currently in use.
   * The names of the types must be exactly the same as in the data. This variable only needs to
   * be adjusted if the Weighted F1 metric is to be used.
   */
  private static final String[] types = {"Type1", "Type2", "Type3" , "Type4", "Type5"};




  /**
   * Main method, which performs the random test task based on the parameters selected above.
   */
  public static void main(String[] args) throws Exception {

    //Setting Values
    int policy, reward, thres_pred, max_cyclic, max_acyclic, single_relation;
    double epsilon, thres_Conf;

    //StringBuffer in which the result is stored
    StringBuffer result = new StringBuffer();

    //Commands
    String learnCommand = "cmd /c \"cd / && cd " + pathAnyBURL + " && java -Xmx3G -cp "
        + "AnyBURL-JUNO.jar de.unima.ki.anyburl.LearnReinforced config-learn_tune.properties\"";
    String applyCommand = "cmd /c \"cd / && cd " + pathAnyBURL + " && java -Xmx3G -cp "
        + "AnyBURL-JUNO.jar de.unima.ki.anyburl.Apply config-apply_tune.properties\"";

    //Starting line in the result
    result.append("Round No;Policy;Reward;Epsilon;Threshold Confidence;Threshold Correct "
        + "Prediction;Max Length Cyclic;Max Length Acyclic;Single Relations;Measure\n");

    for(int i = 0; i < numberOfRuns; i++) {
      //Set the default settings
      if(i == 0){
        policy = 2;
        reward = 5;
        epsilon = 0.1;
        thres_pred = 2;
        thres_Conf = 0.0001;
        max_cyclic = 3;
        max_acyclic = 1;
        single_relation = 0;

      // Set the random settings
      } else {
        policy = (int) (Math.random() * 2 + 1);
        reward = randomReward();
        epsilon = Math.random();
        thres_Conf = (Math.random() * (0.3 - 0.00001)) + 0.00001;
        thres_pred = (int) ((Math.random()) * 15 + 1);
        max_cyclic = randomMaxCyclic();
        max_acyclic = (int) ((Math.random()) * 3 + 1);
        single_relation = (int) ((Math.random()) * 2);
      }

      changeConfigLearn(policy,reward,epsilon,thres_Conf,thres_pred, max_cyclic, max_acyclic,
          single_relation);

      //run learning
      Process runtime = Runtime.getRuntime().exec(learnCommand);
      Show_Output(runtime);

      //run predicting
      Process runtime2 = Runtime.getRuntime().exec(applyCommand);
      Show_Output(runtime2);

      //Save Value after Evaluation
      result.append((i+1) + ";" + policy + ";" + reward + ";" + epsilon + ";" + thres_Conf + ";"
          + thres_pred + ";" + max_cyclic + ";" + max_acyclic + ";");
      if(single_relation > 0){
        result.append("yes;");
      } else {
        result.append("no;");
      }

      //Save Measure based on selection from the top
      if(accuracyMetric){
        result.append(EvaluationOfResults.getAccuracyValue(pathPredictions) + "\n");
      } else {
        result.append(EvaluationOfResults.getWeightedF1(pathPredictions, "", types, false) + "\n");
      }

      //Caching the result, in case of errors
      if(i % 10 == 0) {
        writeToFile(result, outputPathTuning);
      }
    }
    //save final results
    writeToFile(result, outputPathTuning);
  }


  /**
   * Displays the output of the process.
   *
   * @param process The process to be displayed
   */
  public static void Show_Output(Process process) throws IOException {
    BufferedReader output_reader = new BufferedReader(new
        InputStreamReader(process.getInputStream()));
    String output = "";
    while ((output = output_reader.readLine()) != null) {
      System.out.println(output);
    }
  }

  /**
   * Changes the entries in the configuration file to the parameters.
   *
   * @param policy Specifies which type of policy to use
   * @param reward Specifies which type of reward to use
   * @param epsilon Specifies which epsilon value is to be used
   * @param threshold_confidence Specifies which threshold value is to be used for the confidence
   * @param threshold_correct_predictions Specifies which threshold value is to be used for the
   *                                      number of correct predictions
   * @param max_length_cyclic Specifies the maximum value to be used for the length of cyclic rules
   * @param max_length_acyclic Specifies the maximum value to be used for the length of acyclic
   *                           rules
   * @param single_relation Specifies whether only relations of one type are to be learned
   */
  public static void changeConfigLearn(Integer policy, Integer reward, Double epsilon,
      Double threshold_confidence, Integer threshold_correct_predictions, Integer max_length_cyclic,
      Integer max_length_acyclic,  Integer single_relation){
    StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
    try {
      File file = new File(pathConfigFile);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      String line;

      //sets Settings
      while ((line = br.readLine()) != null) {
        if (line.contains("POLICY")) {
          sb.append("POLICY = " + policy + "\n");
        } else if(line.contains("REWARD")){
          sb.append("REWARD = " + reward + "\n");
        } else if(line.contains("EPSILON")){
          sb.append("EPSILON = " + epsilon + "\n");
        } else if(line.contains("THRESHOLD_CORRECT_PREDICTIONS")){
          sb.append("THRESHOLD_CORRECT_PREDICTIONS = " + threshold_correct_predictions + "\n");
        } else if(line.contains("THRESHOLD_CONFIDENCE")) {
          sb.append("THRESHOLD_CONFIDENCE = " + threshold_confidence + "\n");
        }  else if(line.contains("MAX_LENGTH_CYCLIC")) {
          sb.append("MAX_LENGTH_CYCLIC = " + max_length_cyclic + "\n");
        }  else if(line.contains("MAX_LENGTH_ACYCLIC")) {
          sb.append("MAX_LENGTH_ACYCLIC = " + max_length_acyclic + "\n");
        }  else if(line.contains("SINGLE_RELATIONS")) {
          //delete it first
        } else {
          sb.append(line + "\n");
        }
      }
      //insert if desired
      if(single_relation > 0){
        sb.append("SINGLE_RELATIONS = hasType");
      }
      fr.close();    //closes the stream and release the resources

    } catch (IOException e) {
      e.printStackTrace();
    }

    //Writing new Config file
    writeToFile(sb, pathConfigFile);
  }

  /**
   * Writes a given StringBuffer to a file, at the desired location
   *
   * @param sb The StringBuffer which contains the text for the file
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

  /**
   * Specifies a random value for the reward (1, 3 or 5)
   *
   * @return the random reward value
   */
  public static Integer randomReward(){
    int help = (int) (Math.random() * 3 + 1);
    if(help == 1){
      return 1;
    } else if (help == 2){
      return 3;
    }else {
      return 5;
    }
  }

  /**
   * Specifies a random value for the maximum length of cyclic rules (3, 5 or 7)
   *
   * @return the random maximum length of cyclic rules value
   */
  public static Integer randomMaxCyclic(){
    int help = (int) (Math.random() * 3 + 1);
    if(help == 1){
      return 3;
    } else if (help == 2){
      return 5;
    }else {
      return 7;
    }
  }

}
