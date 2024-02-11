// Eric Li, Charlie Zhao, ICS4U, Finished 6/15/2022
// Ensures that no arbitrary classes are loaded from the save files

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.List;

public class SafeObjectInputStream extends ObjectInputStream {

    List<String> allowedClass;

    public SafeObjectInputStream(InputStream in, List<String> allowedClass) throws IOException {
        super(in);
        this.allowedClass = allowedClass;
    }

    // local files are generally assumed to be safe, but this additional check slightly hardens the application against using arbitrary attacks to exploit
    // please note that it is still insecure; don't use in sensitive contexts!
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        if (!allowedClass.contains(desc.getName())) {
            throw new SecurityException(desc.getName());
        }
        return super.resolveClass(desc);
    }
}
