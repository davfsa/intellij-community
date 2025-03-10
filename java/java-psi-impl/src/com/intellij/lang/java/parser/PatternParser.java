// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.lang.java.parser;

import com.intellij.core.JavaPsiBundle;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.impl.source.tree.JavaElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.PsiBuilderUtil.expect;
import static com.intellij.lang.java.parser.JavaParserUtil.*;

public class PatternParser {
  private static final TokenSet PATTERN_MODIFIERS = TokenSet.create(JavaTokenType.FINAL_KEYWORD);

  private final JavaParser myParser;

  public PatternParser(JavaParser javaParser) {
    myParser = javaParser;
  }

  /**
   * Checks whether given token sequence can be parsed as a pattern.
   * The result of the method makes sense only for places where pattern is expected (case label and instanceof expression).
   */
  @Contract(pure = true)
  public boolean isPattern(final PsiBuilder builder) {
    PsiBuilder.Marker patternStart = builder.mark();
    while (builder.getTokenType() == JavaTokenType.LPARENTH) {
      builder.advanceLexer();
    }
    myParser.getDeclarationParser().parseModifierList(builder, PATTERN_MODIFIERS);
    PsiBuilder.Marker type = myParser.getReferenceParser().parseType(builder, ReferenceParser.EAT_LAST_DOT | ReferenceParser.WILDCARD);
    boolean isPattern = type != null && (builder.getTokenType() == JavaTokenType.IDENTIFIER ||
                                         builder.getTokenType() == JavaTokenType.LPARENTH);
    patternStart.rollbackTo();
    return isPattern;
  }

  /**
   * Must be called only if isPattern returned true
   */
  public PsiBuilder.@NotNull Marker parsePattern(final PsiBuilder builder) {
    PsiBuilder.Marker guardPattern = builder.mark();
    PsiBuilder.Marker primaryPattern = parsePrimaryPattern(builder);
    if (builder.getTokenType() != JavaTokenType.ANDAND) {
      guardPattern.drop();
      return primaryPattern;
    }
    builder.advanceLexer();
    PsiBuilder.Marker guardingExpression = myParser.getExpressionParser().parseConditionalAnd(builder, ExpressionParser.FORBID_LAMBDA_MASK);
    if (guardingExpression == null) {
      error(builder, JavaPsiBundle.message("expected.expression"));
    }
    done(guardPattern, JavaElementType.GUARDED_PATTERN);
    return guardPattern;
  }

  PsiBuilder.@NotNull Marker parsePrimaryPattern(final PsiBuilder builder) {
    if (builder.getTokenType() == JavaTokenType.LPARENTH) {
      PsiBuilder.Marker parenPattern = builder.mark();
      builder.advanceLexer();
      parsePattern(builder);
      if (!expect(builder, JavaTokenType.RPARENTH)) {
        error(builder, JavaPsiBundle.message("expected.rparen"));
      }
      done(parenPattern, JavaElementType.PARENTHESIZED_PATTERN);
      return parenPattern;
    }
    return parseTypeOrRecordPattern(builder);
  }

  private void parseRecordStructurePattern(final PsiBuilder builder) {
    PsiBuilder.Marker recordStructure = builder.mark();
    boolean hasLparen = expect(builder, JavaTokenType.LPARENTH);
    assert hasLparen;

    boolean isFirst = true;
    while (builder.getTokenType() != JavaTokenType.RPARENTH) {
      if (!isFirst) {
        expectOrError(builder, JavaTokenType.COMMA, "expected.comma");
      }

      if (builder.getTokenType() == null) {
        break;
      }

      if (isPattern(builder)) {
        parsePattern(builder);
        isFirst = false;
      }
      else {
        error(builder, JavaPsiBundle.message("expected.pattern"));
        if (builder.getTokenType() == JavaTokenType.RPARENTH) {
          break;
        }
        builder.advanceLexer();
      }
    }
    if (!expect(builder, JavaTokenType.RPARENTH)) {
      builder.error(JavaPsiBundle.message("expected.rparen"));
    }
    recordStructure.done(JavaElementType.RECORD_STRUCTURE_PATTERN);
  }

  private PsiBuilder.@NotNull Marker parseTypeOrRecordPattern(final PsiBuilder builder) {
    PsiBuilder.Marker pattern = builder.mark();
    PsiBuilder.Marker patternVariable = builder.mark();
    myParser.getDeclarationParser().parseModifierList(builder, PATTERN_MODIFIERS);

    PsiBuilder.Marker type = myParser.getReferenceParser().parseType(builder, ReferenceParser.EAT_LAST_DOT | ReferenceParser.WILDCARD);
    assert type != null; // guarded by isPattern
    boolean isRecord = false;
    if (builder.getTokenType() == JavaTokenType.LPARENTH) {
      parseRecordStructurePattern(builder);
      isRecord = true;
    }

    final boolean hasIdentifier;
    if (builder.getTokenType() == JavaTokenType.IDENTIFIER) { // pattern variable after the record structure pattern
      if (isRecord) {
        PsiBuilder.Marker variable = builder.mark();
        builder.advanceLexer();
        variable.done(JavaElementType.RECORD_PATTERN_VARIABLE);
      } else {
        builder.advanceLexer();
      }
      hasIdentifier = true;
    } else {
      hasIdentifier = false;
    }

    if (isRecord) {
      patternVariable.drop();
      done(pattern, JavaElementType.RECORD_PATTERN);
    }
    else {
      assert hasIdentifier;// guarded by isPattern
      done(patternVariable, JavaElementType.PATTERN_VARIABLE);
      done(pattern, JavaElementType.TYPE_TEST_PATTERN);
    }
    return pattern;
  }
}
