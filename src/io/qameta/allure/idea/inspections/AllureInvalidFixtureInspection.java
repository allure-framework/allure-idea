package io.qameta.allure.idea.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @author ehborisov
 */
public class AllureInvalidFixtureInspection extends BaseLocalInspectionTool {

    private static final List<String> GROUP_ANNOTATIONS = Arrays.asList(
            "org.testng.annotations.BeforeGroups",
            "org.testng.annotations.AfterGroups"
    );
    public static final String INSPECTION_TEXT = "TestNG @BeforeGroups and @AfterGroups fixtures are not " +
            "supported by Allure reports";

    public boolean isEnabledByDefault() {
        return true;
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return INSPECTION_TEXT;
    }

    @NotNull
    public String getShortName() {
        return "AllureUnsupportedFixtureInspection";
    }

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new AllureStepVisitor(holder);
    }

    static class AllureStepVisitor extends JavaElementVisitor {
        final ProblemsHolder holder;

        AllureStepVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitClass(PsiClass aClass) {
            final List<PsiMethod> methods = Arrays.asList(aClass.getAllMethods()).stream()
                    .filter(AllureInvalidFixtureInspection::hasTestNGGroupFixture).collect(toList());
            methods.forEach(m -> holder.registerProblem(m, INSPECTION_TEXT));
        }
    }

    private static boolean hasTestNGGroupFixture(final PsiMethod method){
        return Arrays.asList(method.getModifierList().getAnnotations()).stream()
                .anyMatch(a -> Objects.nonNull(a) && GROUP_ANNOTATIONS.contains(getAnnotationName(a)));
    }

    @Nullable
    private static String getAnnotationName(@NotNull final PsiAnnotation annotation) {
        final Ref<String> qualifiedAnnotationName = new Ref<>();
        ApplicationManager.getApplication().runReadAction(() -> {
                    String qualifiedName = annotation.getQualifiedName();
                    qualifiedAnnotationName.set(qualifiedName);
                }
        );
        return qualifiedAnnotationName.get();
    }

}
