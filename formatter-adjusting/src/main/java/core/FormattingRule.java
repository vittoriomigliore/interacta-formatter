package core;

import java.io.File;
import java.io.IOException;

public interface FormattingRule {
    void apply(File file) throws IOException;
}
