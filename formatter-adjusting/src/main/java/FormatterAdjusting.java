import core.FormattingRule;
import core.JavaFileProcessor;
import rules.GetterSetterCleanup;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormatterAdjusting {

    private static final Logger logger = Logger.getLogger(FormatterAdjusting.class.getName());

    public static void main(String[] args) {

        List<FormattingRule> rules = List.of(new GetterSetterCleanup());
        JavaFileProcessor processor = new JavaFileProcessor(rules);

        for (String filePath : args) {
            File file = new File(filePath);
            logger.info("Processing file: " + file.getAbsolutePath());
            try {
                processor.process(file);
                logger.info("Successfully processed: " + file.getAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to process file: " + file.getAbsolutePath(), e);
            }
        }
    }
}
