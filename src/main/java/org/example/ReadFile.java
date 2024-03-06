package org.example;
import java.io.InputStream;
import java.util.Scanner;

public class ReadFile {
    
    public String readFileData(String file){
        ReadFile obj = new ReadFile();
    
            InputStream is = obj.getiostream(file);
            try (Scanner sc = new Scanner(is).useDelimiter("\\A")) {
                String rt = sc.hasNext() ? sc.next() : "";
                return rt;
            }
    }

        private InputStream getiostream(final String fileName)
    {
        InputStream io = this.getClass()
            .getClassLoader()
            .getResourceAsStream(fileName);
        
        if (io == null) {
            throw new IllegalArgumentException(fileName + "- NOT FOUND");
        }
        return io;
    }
}
