package rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import core.FormattingRule;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Logger;

public class GetterSetterCleanup implements FormattingRule {

    private static final Logger logger = Logger.getLogger(GetterSetterCleanup.class.getName());

    public void apply(File file) {

        try {
            String originalContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            CompilationUnit cu = StaticJavaParser.parse(originalContent);
            boolean modified = applyToParsed(cu);
            if (modified) {
                Files.writeString(file.toPath(), cu.toString(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            logger.severe("Failed to process file: " + file.getAbsolutePath() + ", error: " + e.getMessage());
        }
    }

    private boolean applyToParsed(CompilationUnit cu) {

        boolean modified = false;

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (method.getBody().isEmpty()) {
                continue;
            }

            boolean isGetter = isGetter(method);
            boolean isSetter = isSetter(method);
            if (isGetter || isSetter) {
                logger.info("Method '" + method.getNameAsString() + "' identified as " + (isGetter ? "getter" : "setter"));
                BlockStmt body = method.getBody().get();
                String original = body.toString();
                String cleaned = cleanMethodBodyBraces(original);
                if (!original.equals(cleaned)) {
                    method.setBody(StaticJavaParser.parseBlock(cleaned));
                    modified = true;
                }
            }
        }

        return modified;
    }

    private boolean isGetter(MethodDeclaration method) {

        if (!method.isPublic()) {
            return false;
        }

        if (!method.getParameters().isEmpty()) {
            return false;
        }

        Type type = method.getType();
        if (type.isVoidType()) {
            return false;
        }

        String methodName = method.getNameAsString();
        boolean isBoolean = type.isPrimitiveType() && type.asPrimitiveType().toString().equals("boolean")
                || type.isClassOrInterfaceType() && type.asClassOrInterfaceType().getNameAsString().equals("Boolean");
        if (!(methodName.startsWith("get") && methodName.length() > 3) &&
                !(methodName.startsWith("is") && methodName.length() > 2 && isBoolean)) {
            return false;
        }

        if (method.getBody().isEmpty()) {
            return false;
        }
        BlockStmt body = method.getBody().get();
        if (body.getStatements().size() != 1) {
            return false;
        }
        if (!(body.getStatement(0).isReturnStmt())) {
            return false;
        }

        ReturnStmt returnStmt = (ReturnStmt) body.getStatement(0);
        if (returnStmt.getExpression().isEmpty()) {
            return false;
        }
        Expression expr = returnStmt.getExpression().get();

        return expr.isNameExpr() || expr.isFieldAccessExpr();
    }

    private boolean isSetter(MethodDeclaration method) {

        if (!method.isPublic() || !method.getType().isVoidType() || method.getParameters().size() != 1) {

            return false;
        }

        String name = method.getNameAsString();
        if (!name.startsWith("set") || name.length() == 3) {
            return false;
        }

        String property = decapitalize(name.substring(3));
        if (property.isEmpty()) {
            return false;
        }

        if (method.getBody().isEmpty()) {
            return false;
        }

        BlockStmt body = method.getBody().get();
        // Setter should have exactly one statement
        if (body.getStatements().size() != 1) {
            return false;
        }

        if (body.getStatement(0).isExpressionStmt()) {
            ExpressionStmt exprStmt = body.getStatement(0).asExpressionStmt();
            Expression expr = exprStmt.getExpression();
            if (expr.isAssignExpr()) {
                AssignExpr assignExpr = expr.asAssignExpr();
                String leftStr = assignExpr.getTarget().toString();
                if (leftStr.equals(property) || leftStr.equals("this." + property)) {
                    String paramName = method.getParameter(0).getNameAsString();
                    return assignExpr.getValue().isNameExpr() &&
                            assignExpr.getValue().asNameExpr().getNameAsString().equals(paramName);
                }
            }
        }

        return false;
    }

    private String decapitalize(String s) {

        return (s == null || s.isEmpty()) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private String cleanMethodBodyBraces(String code) {
        // Remove blank lines immediately after opening brace and before closing brace
        // Example: {\n\n   ...  \n\n}
        code = code.replaceAll("\\{\\s*\\n+", "{\n");
        code = code.replaceAll("\\n+\\s*}", "\n}");
        return code;
    }
}
