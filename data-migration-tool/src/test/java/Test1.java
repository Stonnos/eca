import org.junit.Test;

/**
 * @author Roman Batygin
 */

public class Test1 {

    @Test
    public void test() {
        String r = "*abc _!$%@@##12-gdgd";
        System.out.println(r.replaceAll("[^\\w]", "_"));
    }
}
