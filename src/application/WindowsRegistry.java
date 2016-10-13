


package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author Oleg Ryaboy, based on work by Miguel Enriquez 
 */
public class WindowsRegistry {

    /**
     * 
     * @param location path in the registry
     * @param key registry key
     * @return registry value or null if not found
     */
    public static final String readRegistry(String location, String key){
        try {
            String[] cmdData = execCmd("reg query " + 
                    '"'+ location + "\" /v " + key).split("\n");
            
            for (int i = 1; i <= cmdData.length - 1; i++) {
                do {
                    cmdData[i] = cmdData[i].replace("\r", "");
                } while (cmdData[i].contains("\r"));

                if (cmdData[i].contains("  ")) {
                    do {
                        cmdData[i] = cmdData[i].replace("   ", "  ");
                    } while (cmdData[i].contains("   "));

                    String[] test3 = cmdData[i].trim().split("  ");
                    return test3[test3.length - 1];
                }
            }
            return null;
        }
        catch (Exception ex) {
            MyLog.logError(ex);
            return null;
        }
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw= new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        @Override
        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            }
            catch (IOException ex) { 
                MyLog.logError(ex);
            }
        }

        public String getResult() {
            return sw.toString();
        }
    }
    
    private static String execCmd(String cmd) throws IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime()
                .exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}