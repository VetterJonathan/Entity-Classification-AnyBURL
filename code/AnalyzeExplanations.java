import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class AnalyzeExplanations {

  /**
   *  Path to an explanation file
   */
  private static final String inputPath = "C:\\Users\\testUser\\Desktop\\AnyBURL\\explanations\\ESWC_FB15K-237\\explanations-split1.txt";

  /**
   * Path where the analysed file should be located
   */
  private static final String outputPath = "C:\\Users\\testUser\\Desktop\\AnyBURL\\explanations\\ESWC_FB15K-237\\split1.csv";


  /**
   * Main method, which performs the desired task based on the parameters selected above.
   */
  public static void main(String args[]) {

    analyze(inputPath, outputPath);

  }


  /**
   * Method to convert an explanation file from AnyBURL to a filtered and partially analysed file
   *
   * @param inputPath Path to an explanation file
   * @param outputPath Path where the analysed file should be located
   */
  public static void analyze(String inputPath, String outputPath){
    try {
      File file = new File(inputPath);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
      String line;
      String givenLabel = ""; //stores the given label

      //Columns in the output of the analysis
      sb.append("Relation;Confidence of Prediction;Rules;Correct\n");
      boolean continuation = true; //indicates which lines still count towards the same explanation

      while ((line = br.readLine()) != null) {
        //Detects lines containing a hasType relation and stores it in the result
        if (line.contains(" hasType ")) {
          sb.append(line + ";");
          //The label is filtered out of the relation
          givenLabel = line.split(Pattern.quote(" "))[2];

          //Lines are read in until the tail predictions are reached
          while ((line = br.readLine()) != null && continuation) {
            if(line.contains("Tails:")){
              //The next line is read, as this is where the relevant information is located
              line = br.readLine();
              //The line is split to get the confidence of the prediction and to get important
              // values from the rest later
              String parts[] = line.split(Pattern.quote(" ["));
              //Insert the Confidence and replace the . with a ,
              sb.append(parts[0].substring(2).replace('.',',') + ";");
              //Divide the split line further to get to the rule (innerParts[3]
              String innerParts[] = parts[1].split(Pattern.quote("\t"));
              sb.append(innerParts[3] + ";");

              //Checks if the given label is equal to the predicted label
              if(givenLabel.equals(parts[1].substring(11).split(Pattern.quote(" "))[0])) {
                sb.append("Yes\n");
              } else {
                sb.append("No\n");
              }
              //Is set to false, since the explanation for this prediction has been generated
              continuation = false;
            }
          }
          //True to be able to look at the next prediction
          continuation = true;
        }
      }
      fr.close();    //closes the stream and release the resources

      //Writing StringBuffer in new FIle
      BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(outputPath)));

      //write contents of StringBuffer to a file
      bwr.write(sb.toString());

      //flush the stream
      bwr.flush();

      //close the stream
      bwr.close();

      System.out.println("Content of StringBuffer written to File.");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
