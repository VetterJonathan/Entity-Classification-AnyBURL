import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class Transformation {

  /**
   * True if the underlying dataset is in functional form (e.g.: verb(ob1, ob2)  ) and false if it
   * is in the form ( Entity 0 0 1 1 ).
   */
  public static final Boolean functionalForm = true;

  /**
   * True if the underlying dataset are labels in the form label(entity)
   */
  public static final Boolean labels = true;

  /**
   * Path where the file to be transformed is located
   */
  public static final String inputPath = "C:\\Users\\testUser\\Desktop\\inputTraining.txt";

  /**
   * Path where the transformed file is to be saved
   */
  public static final String outputPath = "C:\\Users\\testUser\\Desktop\\AnyBURL\\data\\ESWC_FB15K-237\\entity_classification_4\\training.txt";





  /**
   * Main method, which performs the desired task based on the parameters selected above.
   */
  public static void main(String args[]){
    if(labels){
        transformLabels(inputPath, outputPath);
    } else if(functionalForm) {
        transformDataFunctionalForm(inputPath, outputPath);
    } else {
      transformDataMultipleTypes(inputPath, outputPath);
    }
  }

  /**
   * The method transforms data that are in functional form into triples, e.g.: pred(type1,type2)
   * to type1 pred type2
   *
   * @param inputPath Path to the original datasets
   * @param outputPath Path where the modified datasets are stored
   */
  public static void transformDataFunctionalForm(String inputPath, String outputPath) {
    try {
      File file = new File(inputPath);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
      String line;
      while ((line = br.readLine()) != null) {

        //splits the read line and thereby exposes the predicate (=parts[0])
        String[] parts = line.split(Pattern.quote("("));

        //splits remaining part of line and gets Subject (=innerParts[0]) / Object (=innerParts[1])
        String[] innerParts = parts[1].split(Pattern.quote(","));
        sb.append(innerParts[0] + " " + parts[0] + " " + innerParts[1]
            .substring(0, innerParts[1].length() - 1));
        sb.append("\n");  //line feed
      }
      fr.close();    //closes the stream and release the resources

      writeToFile(sb,outputPath);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The method transforms labels that are in functional form into triples, by adding the predicate
   * "hasType": label(type1, type2) to type1 hasType Label
   *
   * @param inputPath Path to the original datasets
   * @param outputPath Path where the modified datasets are stored
   */
  public static void transformLabels(String inputPath, String outputPath) {
    try {
      File file = new File(inputPath);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
      String line;
      while ((line = br.readLine()) != null) {
        //splits the read line and thereby exposes the label (=parts[0]) and the entity (=parts[1])
        String[] parts = line.split(Pattern.quote("("));
        //Assembles label and object with the new "hasType" relation
        sb.append(parts[1].substring(0, parts[1].length() - 1) + " hasType " + parts[0]);
        sb.append("\n");  //line feed
      }
      fr.close();    //closes the stream and release the resources

      writeToFile(sb, outputPath);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method that puts data into the correct form, i.e.: entity 0 1 1 to entity hasType Type2,
   * entity hasType Type3
   * Here, the source data are in the form Entity 0 0 1 1 0.
   * The method was written for transforming the data from "Do Embeddings Actually Capture Knowledge
   * Graph Semantics?"
   *
   * @param inputPath Path to the original datasets
   * @param outputPath Path where the modified datasets are stored
   */
  public static void transformDataMultipleTypes(String inputPath, String outputPath) {
    try {
      File file = new File(inputPath);    //creates a new file instance
      FileReader fr = new FileReader(file);   //reads the file
      BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
      StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
      String line;
      while ((line = br.readLine()) != null) {

        //Divides the line into its parts, where parts[0] is the entity and parts[i] the different
        // labels with i>=1
        String[] parts = line.split(Pattern.quote("\t"));

        //Now creates the appropriate type assignments
        for(int i = 1; i < parts.length; i++){
          //If there is a 1 in the original, a type assignment is generated
          if(!parts[i].equals("0")){
            sb.append(parts[0] + " hasType Type" + i + "\n");
          }
        }
      }
      fr.close();    //closes the stream and release the resources

      writeToFile(sb, outputPath);

    } catch (IOException e) {
      e.printStackTrace();
    }
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


