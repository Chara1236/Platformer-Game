// Eric Li, Charlie Zhao, ICS4U, Finished 2022-06-17
//This class allows us to input and output flies useful for
// inputting levels and making save data.
import java.io.*;
import java.util.List;
import java.util.Scanner;

public final class FileManager {

    // will create file if it doesn't exist
    // does not include robust file closing as readFile is simpler and less likely to fail during reading
    // additionally, even failing won't affect readFile as reading is a non-exclusive action (a lock will not prevent it)
    public static String readFile(String fileLocation) throws IOException {
        File newFile = new File(fileLocation);
        if (newFile.createNewFile()) {
            return null;
        } else {
            Scanner fileReader = new Scanner(newFile);
            // using the delimiter \\Z reads the entire file at once
            String returnString = fileReader.useDelimiter("\\Z").next();
            fileReader.close();
            return returnString;
        }
    }

    // includes robust file closing
    public static Object readObjectFromFile(String fileLocation, List<String> allowedObject) throws IOException, ClassNotFoundException {
        ObjectInputStream objectStream;
        Object o;
        FileInputStream fileStream = new FileInputStream(fileLocation);
        // if the allowedObject list is not "Any", ensure that the objects loaded are in the list
        // this can help mitigate the security risk with deserializing untrusted files
        if (!allowedObject.contains("Any")) {
            objectStream = new SafeObjectInputStream(fileStream, allowedObject);
        } else {
            // otherwise, use the unsafe class
            objectStream = new ObjectInputStream(fileStream);
        }
        try {
            o = objectStream.readObject();
            return o;
        } catch (Exception e) {
            // please note that the broad exception Exception was used here
            // as in the event of any exception, the object should still be closed
            // additionally, the exception is re-raised, so no information is lost from being too coarse
            objectStream.close();
            fileStream.close();
            throw e;
        } finally {
            objectStream.close();
            fileStream.close();
        }
    }

    // includes robust file closing
    public static void writeObjectToFile(String fileLocation, Object o) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(fileLocation);
        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
        try {
            objectStream.writeObject(o);
        // please note that a less broad exception was used here compared to readObject, as the only "safe" exception to raise in this circumstance is IOException
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // close resources used even if writing caused an error
            objectStream.close();
            fileStream.close();
        }
    }
}
