import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CreateValid {

  /**
   * Path to the training set to be split into validation and training set
   */
  private static final String input = "C:\\Users\\testUser\\Desktop\\AnyBURL\\data\\WebKB\\split2\\train.txt";

  /**
   * Path to the location where the training set (for validation) is to be stored
   */
  private static final String outputTrain = "C:\\Users\\testUser\\Desktop\\AnyBURL\\data\\WebKB\\split2\\train_validation.txt";

  /**
   * Path to the location where the validation set is to be stored
   */
  private static final String outputValid = "C:\\Users\\testUser\\Desktop\\AnyBURL\\data\\WebKB\\split2\\valid.txt";


  /**
   * Main method, which creates the valid set based on the parameters selected above.
   */
  public static void main(String[] args) {

    int helpCounter = 1; //Counter that helps to temporarily save exactly 10 lines

    //Auxiliary value which is responsible for randomly selecting a line
    int randomValue = (int) (Math.random() * 10);

    ArrayList<String> buffer = new ArrayList<String>(); //Stores 10 lines temporarily

    StringBuffer train = new StringBuffer();    //constructs a string buffer with no characters
    StringBuffer valid = new StringBuffer();    //constructs a string buffer with no characters
    try {
      File file = new File(input);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      String line;
      while (((line = br.readLine()) != null)) {

        // adds lines to the StringBuffer until there are 10 lines inside
        if (line.contains("hasType") && helpCounter < 11) {
          buffer.add(line + "\n");
          helpCounter++;

        //If ten lines are collected, one of these is randomly written into the validation set
        } else if ((line.contains("hasType") && helpCounter == 11)) {
          for (int i = 0; i < buffer.size(); i++) {
            if (i == randomValue) {
              valid.append(buffer.get(i));
            } else {
              train.append(buffer.get(i));
            }
          }

          helpCounter = 1;
          //Determination of a new random value
          randomValue = (int) (Math.random() * 10);
          //Resetting the buffer
          buffer.clear();

          //Adds the currently read line to the buffer so that it is not lost.
          buffer.add(line + "\n");

        // If a line does not have a hasType relation, always write it into the training set
        } else {
          train.append(line + "\n");
        }
      }

      //for getting the leftover values from the buffer
      for (int i = 0; i < buffer.size(); i++) {
        if (i == randomValue) {
          valid.append(buffer.get(i));
        } else {
          train.append(buffer.get(i));
        }
      }

      fr.close();    //closes the stream and release the resources

    } catch (IOException e) {
      e.printStackTrace();
    }

    //write valid and train file
    writeToFile(train, outputTrain);
    writeToFile(valid, outputValid);


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