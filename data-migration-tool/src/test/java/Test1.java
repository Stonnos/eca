import eca.data.migration.util.Utils;
import org.junit.Test;

/**
 * @author Roman Batygin
 */

public class Test1 {

    @Test
    public void test() {
        String r = "*Фяabc _!$%@@##12-gdgd&?()хжЁ+э\n\t";
        System.out.println(Utils.normalizeName(r));
    }
}
