// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInspection.visibility;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.daemon.impl.IdentifierUtil;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.EntryPointsManager;
import com.intellij.codeInspection.ex.EntryPointsManagerBase;
import com.intellij.codeInspection.reference.*;
import com.intellij.codeInspection.ui.InspectionOptionsPanel;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.java.analysis.JavaAnalysisBundle;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.usageView.UsageViewTypeLocation;
import com.intellij.util.ObjectUtils;
import com.intellij.util.VisibilityUtil;
import one.util.streamex.StreamEx;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class VisibilityInspection extends GlobalJavaBatchInspectionTool {
  private static final ExtensionPointName<VisibilityExtension> EP_NAME = new ExtensionPointName<>("com.intellij.visibility");

  private static final Logger LOG = Logger.getInstance(VisibilityInspection.class);
  public boolean SUGGEST_PACKAGE_LOCAL_FOR_MEMBERS = true;
  public boolean SUGGEST_PACKAGE_LOCAL_FOR_TOP_CLASSES = true;
  public boolean SUGGEST_PRIVATE_FOR_INNERS;
  public boolean SUGGEST_FOR_CONSTANTS = true;
  private final Map<String, Boolean> myExtensions = new TreeMap<>();
  @NonNls public static final String SHORT_NAME = "WeakerAccess";

  private final class OptionsPanel extends InspectionOptionsPanel {
    private final JCheckBox myPackageLocalForMembersCheckbox;
    private final JCheckBox myPrivateForInnersCheckbox;
    private final JCheckBox myPackageLocalForTopClassesCheckbox;
    private final JCheckBox mySuggestForConstantsCheckbox;

    private OptionsPanel() {
      myPackageLocalForMembersCheckbox = new JCheckBox(JavaAnalysisBundle.message("inspection.visibility.option.package.private.members"));
      myPackageLocalForMembersCheckbox.setSelected(SUGGEST_PACKAGE_LOCAL_FOR_MEMBERS);
      myPackageLocalForMembersCheckbox.getModel().addItemListener(
        e -> SUGGEST_PACKAGE_LOCAL_FOR_MEMBERS = myPackageLocalForMembersCheckbox.isSelected());

      add(myPackageLocalForMembersCheckbox);

      myPackageLocalForTopClassesCheckbox = new JCheckBox(JavaAnalysisBundle.message(
        "inspection.visibility.package.private.top.level.classes"));
      myPackageLocalForTopClassesCheckbox.setSelected(SUGGEST_PACKAGE_LOCAL_FOR_TOP_CLASSES);
      myPackageLocalForTopClassesCheckbox.getModel().addItemListener(
        e -> SUGGEST_PACKAGE_LOCAL_FOR_TOP_CLASSES = myPackageLocalForTopClassesCheckbox.isSelected());

      add(myPackageLocalForTopClassesCheckbox);

      myPrivateForInnersCheckbox = new JCheckBox(JavaAnalysisBundle.message("inspection.visibility.private.inner.members"));
      myPrivateForInnersCheckbox.setSelected(SUGGEST_PRIVATE_FOR_INNERS);
      myPrivateForInnersCheckbox.getModel().addItemListener(e -> SUGGEST_PRIVATE_FOR_INNERS = myPrivateForInnersCheckbox.isSelected());

      add(myPrivateForInnersCheckbox);

      mySuggestForConstantsCheckbox = new JCheckBox(JavaAnalysisBundle.message("inspection.visibility.option.constants"));
      mySuggestForConstantsCheckbox.setSelected(SUGGEST_FOR_CONSTANTS);
      mySuggestForConstantsCheckbox.getModel().addItemListener(
        e -> SUGGEST_FOR_CONSTANTS = mySuggestForConstantsCheckbox.isSelected());

      add(mySuggestForConstantsCheckbox);

      for (EntryPoint entryPoint : EntryPointsManagerBase.DEAD_CODE_EP_NAME.getExtensions()) {
        if (entryPoint instanceof EntryPointWithVisibilityLevel) {
          final JCheckBox checkBox = new JCheckBox(((EntryPointWithVisibilityLevel)entryPoint).getTitle());
          checkBox.setSelected(isEntryPointEnabled((EntryPointWithVisibilityLevel)entryPoint));
          checkBox.addActionListener(e -> myExtensions.put(((EntryPointWithVisibilityLevel)entryPoint).getId(), checkBox.isSelected()));
          add(checkBox);
        }
      }
    }
  }

  @Override
  public JComponent createOptionsPanel() {
    return new OptionsPanel();
  }

  @NotNull
  @Override
  public LocalInspectionTool getSharedLocalInspectionTool() {
    return new AccessCanBeTightenedInspection(this);
  }

  @Override
  @NotNull
  public String getGroupDisplayName() {
    return InspectionsBundle.message("group.names.declaration.redundancy");
  }

  @Override
  @NotNull
  public String getShortName() {
    return SHORT_NAME;
  }

  @Override
  public CommonProblemDescriptor @Nullable [] checkElement(@NotNull final RefEntity refEntity,
                                                           @NotNull final AnalysisScope scope,
                                                           @NotNull final InspectionManager manager,
                                                           @NotNull final GlobalInspectionContext globalContext,
                                                           @NotNull final ProblemDescriptionsProcessor processor) {
    if (!(refEntity instanceof RefJavaElement)) {
      return null;
    }
    final RefJavaElement refElement = (RefJavaElement)refEntity;

    if (refElement instanceof RefParameter) return null;
    if (refElement.isSyntheticJSP()) return null;

    if (refElement instanceof RefElementImpl) {
      PsiFile containingFile = ((RefElementImpl)refElement).getContainingFile();
      if (containingFile == null || !containingFile.getLanguage().isKindOf(JavaLanguage.INSTANCE)) {
        return null;
      }
    }

    if (!SUGGEST_FOR_CONSTANTS && refEntity instanceof RefField) {
      RefField refField = (RefField)refEntity;
      if (refField.isFinal() && refField.isStatic() && refField.isOnlyAssignedInInitializer()) {
        return null;
      }
    }

    if (refElement instanceof RefField) {
      final RefField refField = (RefField)refElement;
      if (refField.isImplicitlyWritten() || refField.isImplicitlyRead()) {
        return null;
      }
      if (refField.isEnumConstant()) {
        return null;
      }
    }

    //ignore implicit constructors. User should not be able to see them.
    if (refElement instanceof RefImplicitConstructor) return null;

    //ignore library override methods.
    if (refElement instanceof RefMethod) {
      RefMethod refMethod = (RefMethod) refElement;
      if (refMethod.isExternalOverride() || refMethod.isRecordAccessor()) return null;
    }

    //ignore anonymous classes. They do not have access modifiers.
    if (refElement instanceof RefClass) {
      RefClass refClass = (RefClass) refElement;
      if (refClass.isAnonymous() || refClass.isServlet() || refClass.isApplet() || refClass.isLocalClass()) {
        return null;
      }
    }
    //ignore interface members. They always have public access modifier.
    if (refElement.getOwner() instanceof RefClass) {
      RefClass refClass = (RefClass) refElement.getOwner();
      if (refClass.isInterface()) return null;
    }

    if (keepVisibilityLevel(refElement)) {
      return null;
    }

    @EntryPointWithVisibilityLevel.VisibilityLevelResult
    int minLevel = getMinVisibilityLevel(refElement);
    //ignore entry points.
    if (refElement.isEntry()) {
      if (minLevel == EntryPointWithVisibilityLevel.ACCESS_LEVEL_INVALID) return null;
    }

    //ignore unreferenced code. They could be a potential entry points.
    if (refElement.getInReferences().isEmpty()) {
      if (minLevel == EntryPointWithVisibilityLevel.ACCESS_LEVEL_INVALID) {
        minLevel = getMinVisibilityLevel(refElement);
        if (minLevel == EntryPointWithVisibilityLevel.ACCESS_LEVEL_INVALID) return null;
      }
      String weakestAccess = PsiUtil.getAccessModifier(minLevel);
      if (weakestAccess != refElement.getAccessModifier()) {
        return createDescriptions(refElement, weakestAccess, manager, globalContext);
      }
    }

    if (refElement instanceof RefClass) {
      if (isTopLevelClass(refElement) && minLevel <= 0 && !SUGGEST_PACKAGE_LOCAL_FOR_TOP_CLASSES) return null;
    }

    String access = getPossibleAccess(refElement, minLevel <= 0 ? PsiUtil.ACCESS_LEVEL_PRIVATE : minLevel);
    if (access != refElement.getAccessModifier()) {
      return createDescriptions(refElement, access, manager, globalContext);
    }
    return null;
  }

  private boolean keepVisibilityLevel(@NotNull RefJavaElement refElement) {
    return StreamEx.of(EntryPointsManagerBase.DEAD_CODE_EP_NAME.getExtensionList())
      .select(EntryPointWithVisibilityLevel.class)
      .anyMatch(point -> point.keepVisibilityLevel(isEntryPointEnabled(point), refElement));
  }

  private static CommonProblemDescriptor @NotNull [] createDescriptions(RefElement refElement, String access,
                                                                        @NotNull InspectionManager manager,
                                                                        @NotNull GlobalInspectionContext globalContext) {
    final PsiElement element = refElement.getPsiElement();
    final PsiElement nameIdentifier = element != null ? IdentifierUtil.getNameIdentifier(element) : null;
    if (nameIdentifier != null) {
      String targetVisibility = VisibilityUtil.toPresentableText(access);
      final String message = JavaAnalysisBundle.message("inspection.visibility.compose.suggestion", targetVisibility);
      final String elementDescription = ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
      final String quickFixName = JavaAnalysisBundle.message("change.visibility.level", elementDescription, targetVisibility);
      final AcceptSuggestedAccess fix = new AcceptSuggestedAccess(globalContext.getRefManager(), access, quickFixName);
      return new ProblemDescriptor[] {
        manager.createProblemDescriptor(nameIdentifier, message, fix, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false)
      };
    }
    return CommonProblemDescriptor.EMPTY_ARRAY;
  }

  @EntryPointWithVisibilityLevel.VisibilityLevelResult
  int getMinVisibilityLevel(@NotNull PsiMember member) {
    //noinspection MagicConstant
    return StreamEx.of(EntryPointsManagerBase.DEAD_CODE_EP_NAME.getExtensions())
      .select(EntryPointWithVisibilityLevel.class)
      .filter(point -> isEntryPointEnabled(point))
      .mapToInt(point -> point.getMinVisibilityLevel(member))
      .max().orElse(-1);
  }

  private boolean isEntryPointEnabled(EntryPointWithVisibilityLevel point) {
    return myExtensions.getOrDefault(point.getId(), true);
  }

  @EntryPointWithVisibilityLevel.VisibilityLevelResult
  private int getMinVisibilityLevel(@NotNull RefJavaElement refElement) {
    if (refElement.isEntry()) {
      PsiElement element = refElement.getPsiElement();
      if (element instanceof PsiMember) {
        return getMinVisibilityLevel((PsiMember)element);
      }
    }
    return EntryPointWithVisibilityLevel.ACCESS_LEVEL_INVALID;
  }

  @NotNull
  @PsiModifier.ModifierConstant
  private String getPossibleAccess(@NotNull RefJavaElement refElement, int minLevel) {
    @PsiModifier.ModifierConstant String curAccess = refElement.getAccessModifier();
    String weakestAccess = PsiUtil.getAccessModifier(minLevel);

    if (isTopLevelClass(refElement) || isCalledOnSubClasses(refElement)) {
      weakestAccess = PsiModifier.PACKAGE_LOCAL;
    }
    if (isAbstractMethod(refElement)) {
      weakestAccess = PsiModifier.PROTECTED;
    }
    if (refElement instanceof RefMethod) {
      final RefMethod refMethod = (RefMethod)refElement;
      if (refMethod.isConstructor()) {
        final RefClass ownerClass = refMethod.getOwnerClass();
        if (ownerClass != null && ownerClass.isRecord()) {
          weakestAccess = ownerClass.getAccessModifier();
        }
      }
    }

    if (curAccess == weakestAccess) return curAccess;

    while (true) {
      String weakerAccess = getWeakerAccess(curAccess, refElement);
      if (weakerAccess == null || RefJavaUtil.getInstance().compareAccess(weakerAccess, weakestAccess) < 0) break;
      if (isAccessible(refElement, weakerAccess)) {
        curAccess = weakerAccess;
      }
      else {
        break;
      }
    }

    return curAccess;
  }

  private static boolean isCalledOnSubClasses(RefElement refElement) {
    return refElement instanceof RefMethod && ((RefMethod)refElement).isCalledOnSubClass();
  }

  private static boolean isAbstractMethod(RefElement refElement) {
    return refElement instanceof RefMethod && ((RefMethod) refElement).isAbstract();
  }

  private static boolean isTopLevelClass(RefElement refElement) {
    return refElement instanceof RefClass && refElement.getOwner() instanceof RefPackage;
  }

  @Nullable
  @PsiModifier.ModifierConstant
  private String getWeakerAccess(@PsiModifier.ModifierConstant String curAccess, RefElement refElement) {
    if (curAccess == PsiModifier.PUBLIC) {
      return isTopLevelClass(refElement) ? PsiModifier.PACKAGE_LOCAL : PsiModifier.PROTECTED;
    }
    if (curAccess == PsiModifier.PROTECTED) {
      return SUGGEST_PACKAGE_LOCAL_FOR_MEMBERS ? PsiModifier.PACKAGE_LOCAL : PsiModifier.PRIVATE;
    }
    if (curAccess == PsiModifier.PACKAGE_LOCAL) {
      return PsiModifier.PRIVATE;
    }

    return null;
  }

  private boolean isAccessible(@NotNull RefJavaElement to, @NotNull @PsiModifier.ModifierConstant String accessModifier) {
    for (RefElement refElement : to.getInReferences()) {
      if (!isAccessibleFrom(refElement, to, accessModifier)) return false;
    }

    if (to instanceof RefMethod) {
      RefMethod refMethod = (RefMethod) to;

      if (refMethod.isAbstract() && (refMethod.getDerivedMethods().isEmpty() || refMethod.getAccessModifier() == PsiModifier.PRIVATE)) return false;

      for (RefMethod refOverride : refMethod.getDerivedMethods()) {
        if (accessModifier == PsiModifier.PRIVATE) return false;
        if (!isAccessibleFrom(refOverride, to, accessModifier)) return false;
      }

      for (RefMethod refSuper : refMethod.getSuperMethods()) {
        if (RefJavaUtil.getInstance().compareAccess(refSuper.getAccessModifier(), accessModifier) > 0) return false;
      }
    }

    if (to instanceof RefClass) {
      RefClass refClass = (RefClass) to;
      for (RefClass subClass : refClass.getSubClasses()) {
        if (!isAccessibleFrom(subClass, to, accessModifier)) return false;
      }

      for (RefEntity refElement : refClass.getChildren()) {
        if (!isAccessible((RefJavaElement)refElement, accessModifier)) return false;
      }

      for (final RefElement refElement : refClass.getInTypeReferences()) {
        if (!isAccessibleFrom(refElement, refClass, accessModifier)) return false;
      }

      List<RefJavaElement> classExporters = ((RefClassImpl)refClass).getClassExporters();
      if (classExporters != null) {
        for (RefJavaElement refExporter : classExporters) {
          if (getAccessLevel(accessModifier) < getAccessLevel(refExporter.getAccessModifier())) return false;
        }
      }
    }

    return true;
  }

  private static int getAccessLevel(@PsiModifier.ModifierConstant String access) {
    if (access == PsiModifier.PRIVATE) return 1;
    if (access == PsiModifier.PACKAGE_LOCAL) return 2;
    if (access == PsiModifier.PROTECTED) return 3;
    return 4;
  }

  private boolean isAccessibleFrom(RefElement from, RefJavaElement to, String accessModifier) {
    if (accessModifier == PsiModifier.PUBLIC) return true;

    final RefJavaUtil refUtil = RefJavaUtil.getInstance();
    if (accessModifier == PsiModifier.PACKAGE_LOCAL) {
      return RefJavaUtil.getPackage(from) == RefJavaUtil.getPackage(to);
    }

    RefClass fromTopLevel = refUtil.getTopLevelClass(from);
    RefClass toTopLevel = refUtil.getTopLevelClass(to);
    RefClass fromOwner = refUtil.getOwnerClass(from);
    RefClass toOwner = refUtil.getOwnerClass(to);

    if (accessModifier == PsiModifier.PROTECTED) {
      if (to instanceof RefJavaElementImpl && ((RefJavaElementImpl)to).isProtectedAccessForbidden()) {
        return false;
      }
      return fromTopLevel != null && refUtil.isInheritor(fromTopLevel, toOwner)
             || fromOwner != null && refUtil.isInheritor(fromOwner, toTopLevel)
             || toTopLevel != null && toTopLevel == fromTopLevel;
    }

    if (accessModifier == PsiModifier.PRIVATE) {
      if (SUGGEST_PRIVATE_FOR_INNERS) {
        //TODO
        final PsiClass fromTopLevelElement = fromTopLevel != null ? (PsiClass)fromTopLevel.getPsiElement() : null;
        final PsiElement target = to.getPsiElement();
        if (fromTopLevelElement != null) {
          if (containsReferenceTo(fromTopLevelElement.getExtendsList(), target)) return false;
          if (containsReferenceTo(fromTopLevelElement.getImplementsList(), target)) return false;
          if (containsReferenceTo(fromTopLevelElement.getModifierList(), target)) return false;
        }
        return fromTopLevel == toOwner || fromOwner == toTopLevel || toOwner != null && (
          refUtil.getOwnerClass(toOwner) == from || from instanceof RefMethod && toOwner == ((RefMethod)from).getOwnerClass() ||
          from instanceof RefField && toOwner == ((RefField)from).getOwnerClass());
      }

      if (fromOwner != null && fromOwner.isStatic() && !to.isStatic() && refUtil.isInheritor(fromOwner, toOwner)) return false;

      if (fromTopLevel == toOwner) {
        if (from instanceof RefClass) {
          PsiClass fromClass = ((RefClass)from).getUastElement().getJavaPsi();
          final PsiElement target = to.getPsiElement();
          if ((to instanceof RefClass || to instanceof RefField) &&
              containsReferenceTo(fromClass.getModifierList(), target)) return false;
          if (to instanceof RefClass) {
            if (containsReferenceTo(fromClass.getExtendsList(), target)) return false;
            if (containsReferenceTo(fromClass.getImplementsList(), target)) return false;
          }
        }

        return true;
      }
    }

    return false;
  }

  static boolean containsReferenceTo(PsiElement source, PsiElement target) {
    return SyntaxTraverser.psiTraverser(source)
             .filter(PsiJavaCodeReferenceElement.class)
             .filter(ref -> ref.isReferenceTo(target))
             .isNotEmpty();
  }

  @Override
  protected boolean queryExternalUsagesRequests(@NotNull final RefManager manager,
                                                @NotNull final GlobalJavaInspectionContext globalContext,
                                                @NotNull final ProblemDescriptionsProcessor processor) {
    final EntryPointsManager entryPointsManager = globalContext.getEntryPointsManager(manager);
    for (RefElement entryPoint : entryPointsManager.getEntryPoints(manager)) {
      //don't ignore entry points with explicit visibility requirements
      if (ReadAction.nonBlocking(() -> entryPoint instanceof RefJavaElement && getMinVisibilityLevel((RefJavaElement)entryPoint) > 0)
        .executeSynchronously()) {
        continue;
      }
      ignoreElement(processor, entryPoint);
    }

    for (VisibilityExtension addin : EP_NAME.getExtensionList()) {
      addin.fillIgnoreList(manager, processor);
    }
    manager.iterate(new RefJavaVisitor() {
      @Override public void visitElement(@NotNull final RefEntity refEntity) {
        if (!(refEntity instanceof RefElement)) return;
        if (processor.getDescriptions(refEntity) == null) return;
        refEntity.accept(new RefJavaVisitor() {
          @Override public void visitField(@NotNull final RefField refField) {
            if (refField.getAccessModifier() != PsiModifier.PRIVATE) {
              globalContext.enqueueFieldUsagesProcessor(refField, psiReference -> {
                ignoreElement(processor, refField);
                return false;
              });
            }
          }

          @Override public void visitMethod(@NotNull final RefMethod refMethod) {
            if (!refMethod.isExternalOverride() && refMethod.getAccessModifier() != PsiModifier.PRIVATE &&
                !(refMethod instanceof RefImplicitConstructor)) {
              globalContext.enqueueDerivedMethodsProcessor(refMethod, derivedMethod -> {
                ignoreElement(processor, refMethod);
                return false;
              });

              globalContext.enqueueMethodUsagesProcessor(refMethod, psiReference -> {
                ignoreElement(processor, refMethod);
                return false;
              });
            }
          }

          @Override public void visitClass(@NotNull final RefClass refClass) {
            if (!refClass.isAnonymous() && !PsiModifier.PRIVATE.equals(refClass.getAccessModifier())) {
              globalContext.enqueueDerivedClassesProcessor(refClass, inheritor -> {
                ignoreElement(processor, refClass);
                return false;
              });

              globalContext.enqueueClassUsagesProcessor(refClass, psiReference -> {
                ignoreElement(processor, refClass);
                return false;
              });

              final RefMethod defaultConstructor = refClass.getDefaultConstructor();
              if (entryPointsManager.isAddNonJavaEntries() && defaultConstructor != null) {
                final PsiClass psiClass = ObjectUtils.tryCast(refClass.getPsiElement(), PsiClass.class);
                String qualifiedName = psiClass != null ? psiClass.getQualifiedName() : null;
                if (qualifiedName != null) {
                  final Project project = manager.getProject();
                  PsiSearchHelper.getInstance(project)
                                 .processUsagesInNonJavaFiles(qualifiedName, (file, startOffset, endOffset) -> {
                                   entryPointsManager.addEntryPoint(defaultConstructor, false);
                                   ignoreElement(processor, defaultConstructor);
                                   return false;
                                 }, GlobalSearchScope.projectScope(project));
                }
              }
            }
          }
        });

      }
    });
    return false;
  }

  private static void ignoreElement(@NotNull ProblemDescriptionsProcessor processor, @NotNull RefEntity refElement){
    processor.ignoreElement(refElement);

    if (refElement instanceof RefClass) {
      RefClass refClass = (RefClass) refElement;
      RefMethod defaultConstructor = refClass.getDefaultConstructor();
      if (defaultConstructor != null) {
        processor.ignoreElement(defaultConstructor);
        return;
      }
    }

    RefEntity owner = refElement.getOwner();
    if (owner instanceof RefElement) {
      processor.ignoreElement(owner);
    }
  }

  @Override
  public void compose(@NotNull StringBuilder buf, @NotNull final RefEntity refEntity, @NotNull final HTMLComposer composer) {
    composer.appendElementInReferences(buf, (RefElement)refEntity);
  }

  @NotNull
  @Override
  public QuickFix<?> getQuickFix(final String hint) {
    return new AcceptSuggestedAccess(null, hint, null);
  }

  @Override
  @Nullable
  public String getHint(@NotNull final QuickFix fix) {
    return ((AcceptSuggestedAccess)fix).myHint;
  }

  @Override
  public void writeSettings(@NotNull Element node) {
    super.writeSettings(node);
    for (Map.Entry<String, Boolean> entry : myExtensions.entrySet()) {
      if (!entry.getValue()) {
        node.addContent(new Element("disabledExtension").setAttribute("id", entry.getKey()));
      }
    }
    for (Element child : node.getChildren()) {
      if ("SUGGEST_FOR_CONSTANTS".equals(child.getAttributeValue("name")) && "true".equals(child.getAttributeValue("value"))) {
        node.removeContent(child);
        break;
      }
    }
  }

  @Override
  public void readSettings(@NotNull Element node) {
    super.readSettings(node);
    for (Element extension : node.getChildren("disabledExtension")) {
      final String id = extension.getAttributeValue("id");
      if (id != null) {
        myExtensions.put(id, false);
      }
    }
  }

  @TestOnly
  public void setEntryPointEnabled(@NotNull String entryPointId, boolean enabled) {
    LOG.assertTrue(ApplicationManager.getApplication().isUnitTestMode());
    myExtensions.put(entryPointId, enabled);
  }

  private static final class AcceptSuggestedAccess implements LocalQuickFix {
    @SafeFieldForPreview
    private final RefManager myManager;
    @PsiModifier.ModifierConstant private final String myHint;
    private final @IntentionName String myName;

    private AcceptSuggestedAccess(final RefManager manager, @PsiModifier.ModifierConstant String hint, @IntentionName String name) {
      myManager = manager;
      myHint = hint;
      myName = name;
    }

    @Override
    @NotNull
    public String getName() {
      return myName != null ? myName : getFamilyName();
    }

    @Override
    @NotNull
    public String getFamilyName() {
      return JavaAnalysisBundle.message("inspection.visibility.accept.quickfix");
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      final PsiModifierListOwner element = PsiTreeUtil.getParentOfType(descriptor.getPsiElement(), PsiModifierListOwner.class);
      if (element != null) {
        RefElement refElement = null;
        if (myManager != null) {
          refElement = myManager.getReference(element);
        }
        if (element instanceof PsiVariable) {
          ((PsiVariable)element).normalizeDeclaration();
        }

        PsiModifierList list = element.getModifierList();

        LOG.assertTrue(list != null);

        if (element instanceof PsiMethod) {
          PsiMethod psiMethod = (PsiMethod)element;
          PsiClass containingClass = psiMethod.getContainingClass();
          if (containingClass != null && containingClass.getParent() instanceof PsiFile &&
              myHint == PsiModifier.PRIVATE &&
              list.hasModifierProperty(PsiModifier.FINAL)) {
            list.setModifierProperty(PsiModifier.FINAL, false);
          }
        }

        list.setModifierProperty(myHint, true);
        if (refElement instanceof RefJavaElement) {
          RefJavaUtil.getInstance().setAccessModifier((RefJavaElement)refElement, myHint);
        }
      }
    }
  }
}
