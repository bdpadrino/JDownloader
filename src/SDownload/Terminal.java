
package SDownload;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/** Este comando lo halle en el siguiente link
 *http://stackoverflow.com/questions/30875816/two-part-how-to-run-ls-from-a-java-program-and-how-to-tell-computers-on-a-sto
 * Yo solo lo encapsule en una clase. Muchas gracias a "udinnet"
 * @author udinnet 
 */
public class Terminal {
    
    public Terminal() {}

    public String executeCommand(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}