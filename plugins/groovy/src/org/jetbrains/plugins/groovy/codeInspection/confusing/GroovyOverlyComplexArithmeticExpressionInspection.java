/*
 * Copyright 2007-2008 Dave Griffith
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
package org.jetbrains.plugins.groovy.codeInspection.confusing;

import com.intellij.codeInspection.ui.SingleIntegerFieldOptionsPanel;
import com.siyeh.InspectionGadgetsBundle;

import javax.swing.*;

public class GroovyOverlyComplexArithmeticExpressionInspection extends GroovyOverlyComplexArithmeticExpressionInspectionBase {

  @Override
  public JComponent createGroovyOptionsPanel() {
    return new SingleIntegerFieldOptionsPanel(InspectionGadgetsBundle.message("overly.complex.arithmetic.expression.max.number.option"),
                                              this, "m_limit");
  }
}
