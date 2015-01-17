package scripting;

/**
 * Created by docjaq on 17/01/15.
 */
public enum ScriptFunction {
    UPDATE("update");

    private String functionName;

    private ScriptFunction(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}
