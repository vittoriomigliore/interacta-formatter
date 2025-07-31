# Interacta Formatter

This project ensures consistent Java code formatting using a two-step process:

1. **IntelliJ Formatter**  
   Applies standard code style rules (indentation, spacing, etc.) using IntelliJ's built-in formatter and shared configuration files.

2. **Formatter Adjusting Utility**  
   A custom Java tool that removes blank lines inside getter and setter methods (`getX()`, `setX()`, `isX()`). This runs automatically via a Git pre-commit hook.

## Installation

1. **IntelliJ Formatter Configuration**
   - Import the provided `intellij-formatter.xml` into IntelliJ:
     - Go to Preferences → Editor → Code Style → Java.
     - Click the gear icon and select "Import Scheme" → "IntelliJ IDEA code style XML".
     - Choose `intellij-formatter.xml` from this repository.

2. **Formatter Adjusting Utility**
   - Open a terminal and navigate to the `formatter-adjusting` directory:
     - `cd formatter-adjusting`
   - Run `mvn package` inside this directory to produce a JAR file named `formatter-adjusting-1.0-jar-with-dependencies.jar` in the `target/` folder.

3. **'Formatter Adjusting Utility' and 'Pre-commit Hook' copy script**
   - Run the `copy-formatter-adjusting.sh` script with the path to the folder containing all Interacta repositories as an argument. For example:
     - `./copy-formatter-adjusting.sh </path/to/interacta-projects>`
   - This will copy the adjusting utility and install the pre-commit hook in each configured repository.

## Project Structure

- `intellij-formatter.xml`: IntelliJ code style configuration.
- `formatter-adjusting/`: Adjusting utility.
- `sample-hooks/pre-commit`: Pre-commit hook script which runs the adjusting utility JAR before each commit.
- `copy-formatter-adjusting.sh`: Script to copy the adjusting utility and hook to multiple Interacta projects.

## Summary Table

| Step                    | Purpose                            |
|-------------------------|------------------------------------|
| IntelliJ Formatter      | Applies standard code style        |
| Formatter Adjusting     | Cleans up blank lines in accessors |
| Git Pre-commit Hook     | Automates adjusting before commits |
