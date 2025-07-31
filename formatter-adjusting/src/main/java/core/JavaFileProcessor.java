
package core;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JavaFileProcessor {

    private final List<FormattingRule> rules;

    public JavaFileProcessor(List<FormattingRule> rules) {

        this.rules = rules;
    }

    public void process(File file) throws IOException {

        for (FormattingRule rule : rules) {
            rule.apply(file);
        }
    }
}
