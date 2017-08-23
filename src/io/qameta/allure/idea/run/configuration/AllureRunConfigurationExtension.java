package io.qameta.allure.idea.run.configuration;

import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.PathsList;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


/**
 * @author ehborisov
 */
public class AllureRunConfigurationExtension extends RunConfigurationExtension {

    private static String ALLURE_JAVA_ADAPTORS_REGEX = "allure-testng|allure-junit4|allure-junit5|allure-spock";
    private static String ASPECTJ_DEPENCENCY = "org.aspectj:aspectjweaver";
    private static String ASPECTJWEAVER_OPTION_STRING =
            "-javaagent:\"%s/org/aspectj/aspectjweaver/%2$s/" +
                    "aspectjweaver-%2$s.jar\"";

    @Nullable
    protected String getEditorTitle() {
        return "With Allure";
    }


    @Override
    protected boolean isApplicableFor(@NotNull final RunConfigurationBase configuration) {
        return true;
    }

    @Override
    public void updateJavaParameters(RunConfigurationBase configuration, JavaParameters params,
                                     RunnerSettings runnerSettings) {
        PathsList classpath = params.getClassPath();
        final Optional<String> allureAdaptor = classpath.getPathList().stream()
                .filter(e -> e.matches(ALLURE_JAVA_ADAPTORS_REGEX)).findFirst();
        final Optional<String> aspectjDependency = classpath.getPathList().stream()
                .filter(e -> e.contains(ASPECTJ_DEPENCENCY)).findFirst();
        if (allureAdaptor.isPresent() && aspectjDependency.isPresent()) {
            //TODO:extract path to adspectj jar
            params.getVMParametersList().addParametersString(ASPECTJWEAVER_OPTION_STRING);
        }
    }

    @Nullable
    @Override
    protected SettingsEditor createEditor(@NotNull final RunConfigurationBase configuration) {
        //not implemented
        return null;
    }

    @Override
    public void readExternal(@NotNull final RunConfigurationBase runConfiguration, @NotNull final Element element)
            throws InvalidDataException {
        //not implemented
    }

    @Override
    protected void writeExternal(@NotNull final RunConfigurationBase runConfiguration, @NotNull final Element element)
            throws WriteExternalException {
        //not implemented
    }
}
