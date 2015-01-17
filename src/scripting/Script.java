package scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by docjaq on 17/01/15.
 */
public enum Script {
    MONSTER("resources/scripts/monster.js");

    private File scriptFile;
    private Invocable invocable;
    private long scriptLastModified = 0;

    private Script(String filePath) {
        this.scriptFile = new File(filePath);
        checkForUpdate();
    }

    public void checkForUpdate() {
        if (scriptFile.lastModified() > scriptLastModified) {
            this.invocable = getInvocable(scriptFile);
        }
    }

    private Invocable getInvocable(File scriptFile) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // evaluate script
        try {
            engine.eval(new java.io.FileReader(scriptFile));
            System.out.println("Created script engine for script " + scriptFile.getAbsolutePath());
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return (Invocable) engine;
    }

    public void runFunction(ScriptFunction function, Object... parameters) {
        try {
            invocable.invokeFunction(function.getFunctionName(), parameters);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
