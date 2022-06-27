import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class RankExplanations {

  /**
   * Path to a rules file sorted in ascending order of confidence. This is best in .csv format, as
   * this must contain a header line before the actual data begins. and the individual values must
   * be separated by ";". Furthermore, the confidence value must be in third place and the rule
   * itself in fourth place.
   */
  public static final String inputPath = "C:\\Users\\testUser\\Desktop\\AnyBURL\\rules\\Terrorists\\RuleRanks2.csv";

  /**
   * Path where the finished file is saved
   */
  public static final String outputPath = "C:\\Users\\testUser\\Desktop\\AnyBURL\\rules\\Terrorists\\RuleRanked2_basis.csv";


  /**
   * Main Method for adding the rank to a rules file sorted in ascending order of confidence.
   * Rules that have the same confidence also have the same rank. If, for example, rank 1 exists
   * twice, the next rank assigned is rank 2.
   */
  public static void main(String args[]) {
    StringBuffer sb = new StringBuffer();
    int rank = 0; //Value which reflects the rank
    double value = 2.0; //Auxiliary variable to save the value of the Confidence


    try {
      File file = new File(inputPath);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      String line;
      String helpLine; //Help value which is intended for the transformation of the current line
      line = br.readLine(); //Reads header line
      sb.append(line + ";Rank\n"); //adds "Rank" to header line
      while ((line = br.readLine()) != null) {
        //Swap , with . if the Confidence was saved with ,
        helpLine = line.replace(',','.');
        //Splits line into the individual parts
        String[] help = helpLine.split(Pattern.quote(";"));

        //True if Confidence value of current rule is smaller than of previously considered rule.
        if(Double.parseDouble(help[2]) < value) {
          //Set new threshold
          value = Double.parseDouble(help[2]);
          //increase the rank
          rank++;
        }

        sb.append(line);
        //Write the currently viewed row + the calculated rank
        sb.append(";" + rank + "\n");
      }
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    //Writing StringBuffer in new FIle
    BufferedWriter bwr = null;
    try {
      bwr = new BufferedWriter(new FileWriter(new File(outputPath)));

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

