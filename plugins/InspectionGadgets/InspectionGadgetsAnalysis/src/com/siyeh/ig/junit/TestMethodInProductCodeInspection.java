/*
 * Copyright 2006-2013 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.junit;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.psiutils.TestUtils;
import org.jetbrains.annotations.NotNull;

public class TestMethodInProductCodeInspection extends BaseInspection {

  @Override
  @NotNull
  public String getID() {
    return "JUnitTestMethodInProductSource";
  }

  @Override
  @NotNull
  protected String buildErrorString(Object... infos) {
    return InspectionGadgetsBundle.message(
      "test.method.in.product.code.problem.descriptor");
  }

  @Override
  public BaseInspectionVisitor buildVisitor() {
    return new TestCaseInProductCodeVisitor();
  }

  private static class TestCaseInProductCodeVisitor
    extends BaseInspectionVisitor {

    @Override
    public void visitMethod(@NotNull PsiMethod method) {
      final PsiClass containingClass = method.getContainingClass();
      if (TestUtils.isInTestSourceContent(containingClass) ||
          !TestUtils.isAnnotatedTestMethod(method)) {
        return;
      }
      registerMethodError(method);
    }
  }
}
