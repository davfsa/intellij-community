// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.jps.incremental;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtilRt;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTarget;
import org.jetbrains.jps.model.serialization.JpsProjectLoader;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author Eugene Zhuravlev
 */
public final class Utils {
  public static final Key<Map<BuildTarget<?>, Collection<String>>> REMOVED_SOURCES_KEY = Key.create("_removed_sources_");
  public static final Key<Boolean> PROCEED_ON_ERROR_KEY = Key.create("_proceed_on_error_");
  public static final Key<Boolean> ERRORS_DETECTED_KEY = Key.create("_errors_detected_");
  private static volatile File ourSystemRoot = new File(System.getProperty("user.home"), ".idea-build");
  public static final boolean IS_TEST_MODE = Boolean.parseBoolean(System.getProperty("test.mode", "false"));
  public static final boolean IS_PROFILING_MODE = Boolean.parseBoolean(System.getProperty("profiling.mode", "false"));
  private static final int FORKED_JAVAC_HEAP_SIZE_MB;

  static {
    int size = -1;
    try {
      size = Integer.parseInt(System.getProperty("forked.javac.heap.size.mb"));
    }
    catch (Throwable ignored) {
    }
    FORKED_JAVAC_HEAP_SIZE_MB = size;
  }


  private Utils() {
  }

  public static File getSystemRoot() {
    return ourSystemRoot;
  }

  public static void setSystemRoot(File systemRoot) {
    ourSystemRoot = systemRoot;
  }

  @Nullable
  public static File getDataStorageRoot(String projectPath) {
    return getDataStorageRoot(ourSystemRoot, projectPath);
  }


  public static File getDataStorageRoot(final File systemRoot, String projectPath) {
    return getDataStorageRoot(systemRoot, projectPath, s -> s.hashCode());
  }

  public static File getDataStorageRoot(final File systemRoot, String projectPath, Function<? super String, Integer> hashFunction) {

    projectPath = FileUtil.toCanonicalPath(projectPath);
    if (projectPath == null) {
      return null;
    }

    String name;
    final int locationHash;

    final Path rootFile = Paths.get(projectPath);
    if (!Files.isDirectory(rootFile) && projectPath.endsWith(".ipr")) {
      name = StringUtil.trimEnd(rootFile.getFileName().toString(), ".ipr");
      locationHash = hashFunction.apply(projectPath);
    }
    else {
      Path directoryBased = null;
      if (rootFile.endsWith(PathMacroUtil.DIRECTORY_STORE_NAME)) {
        directoryBased = rootFile;
      }
      else {
        Path child = rootFile.resolve(PathMacroUtil.DIRECTORY_STORE_NAME);
        if (Files.exists(child)) {
          directoryBased = child;
        }
      }
      if (directoryBased == null) {
        return null;
      }
      name = PathUtilRt.suggestFileName(JpsProjectLoader.getDirectoryBaseProjectName(directoryBased));
      locationHash = hashFunction.apply(directoryBased.toString());
    }

    return new File(systemRoot, StringUtil.toLowerCase(name) + "_" + Integer.toHexString(locationHash));
  }

  public static boolean errorsDetected(CompileContext context) {
    return ERRORS_DETECTED_KEY.get(context, Boolean.FALSE);
  }

  public static String formatDuration(long duration) {
    return StringUtil.formatDuration(duration);
  }

  public static int suggestForkedCompilerHeapSize() {
    //final JpsProject project = context.getProjectDescriptor().getProject();
    //final JpsJavaCompilerConfiguration config = JpsJavaExtensionService.getInstance().getOrCreateCompilerConfiguration(project);
    //final JpsJavaCompilerOptions options = config.getCurrentCompilerOptions();
    //return options.MAXIMUM_HEAP_SIZE;
    if (FORKED_JAVAC_HEAP_SIZE_MB > 0) {
      return FORKED_JAVAC_HEAP_SIZE_MB;
    }
    final int maxMbytes = (int)(Runtime.getRuntime().maxMemory() / 1048576L);
    if (maxMbytes < 0 || maxMbytes > 1500) {
      return -1; // in the size is too big for older VMs or int overflow, return -1 to let VM choose the heap size
    }
    return Math.max(maxMbytes, 256); // per-forked process: minimum 256 Mb
  }

  /**
   * @param count initial counter value
   * @param operation a runnable to be warapped
   * @return wraps given Runnable operation so that resulting Runnable is executed every 'count' invocation of run() method
   */
  public static Runnable asCountedRunnable(int count, Runnable operation) {
    return new Runnable() {
      private final AtomicInteger myCounter = new AtomicInteger(count);
      @Override
      public void run() {
        int currentVal = myCounter.decrementAndGet();
        if (currentVal % count == 0) {
          try {
            operation.run();
          }
          finally {
            while (currentVal <= 0 && !myCounter.compareAndSet(currentVal, count + (currentVal % count))) { // restore the counter
              currentVal = myCounter.get();
            }
          }
        }
      }
    };
  }
}
