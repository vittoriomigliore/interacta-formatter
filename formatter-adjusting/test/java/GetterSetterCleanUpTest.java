import org.junit.Before;
import org.junit.Test;
import rules.GetterSetterCleanup;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class GetterSetterCleanUpTest {

    private File tempFile;

    @Before
    public void setUp() throws Exception {

        File resource = new File("test/resources/SamplePojo.java");
        tempFile = File.createTempFile("SamplePojoTest", ".java");
        tempFile.deleteOnExit();
        Files.copy(resource.toPath(), tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void cleanUpTest() throws Exception {
        GetterSetterCleanup rule = new GetterSetterCleanup();
        rule.apply(tempFile);

        String result = Files.readString(tempFile.toPath());

        assertNoBlankLinesInGetterSetter(result);
    }

    private void assertNoBlankLinesInGetterSetter(String result) {

        String expected = """
                public class SamplePojo {
                
                    private int value;
                
                    public int getValue() {
                        return value;
                    }
                
                    public void setValue(int value) {
                        this.value = value;
                    }
                }
                """;
        assertEquals(expected, result.replace("\r\n", "\n").replace("\r", "\n"));
    }
}
