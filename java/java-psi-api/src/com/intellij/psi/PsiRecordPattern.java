// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <a href="https://openjdk.java.net/jeps/405">JEP</a>
 * <p>
 * Represents record pattern, for example: {@code Point(int x, int y) p }
 */
public interface PsiRecordPattern extends PsiPrimaryPattern {
  /**
   * @return element representing code inside '(...)' inclusive parenthesis
   */
  @NotNull
  PsiRecordStructurePattern getStructurePattern();

  /**
   * @return type of the pattern, for example in {@code Point(int x, int y) p } it is {@code Point }
   */
  @NotNull
  PsiTypeElement getTypeElement();

  /**
   * @return pattern variable if the pattern has a name, {@code null} otherwise
   */
  @Nullable
  PsiRecordPatternVariable getPatternVariable();
}
