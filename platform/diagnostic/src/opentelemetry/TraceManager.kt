// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.diagnostic.opentelemetry

import com.intellij.diagnostic.telemetry.JaegerJsonSpanExporter
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.util.ShutDownTracker
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.sdk.trace.export.SpanExporter
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes
import org.jetbrains.annotations.ApiStatus
import java.nio.file.Path
import java.util.concurrent.TimeUnit

/**
 * See [Span](https://opentelemetry.io/docs/reference/specification),
 * [Manual Instrumentation](https://opentelemetry.io/docs/instrumentation/java/manual/#create-spans-with-events).
 */
@ApiStatus.Experimental
object TraceManager {
  private var sdk: OpenTelemetry = OpenTelemetry.noop()

  fun init() {
    val serviceName = ApplicationNamesInfo.getInstance().fullProductName
    val appInfo = ApplicationInfo.getInstance()
    val serviceVersion = appInfo.build.asStringWithoutProductCode()
    val serviceNamespace = appInfo.build.productCode

    val traceFile = System.getProperty("idea.diagnostic.opentelemetry.file")
    val spanExporters = mutableListOf<SpanExporter>()
    if (traceFile != null) {
      val jsonSpanExporter = JaegerJsonSpanExporter()
      JaegerJsonSpanExporter.setOutput(file = Path.of(traceFile),
                                       serviceName = serviceName,
                                       serviceVersion = serviceVersion,
                                       serviceNamespace = serviceNamespace)
      spanExporters.add(jsonSpanExporter)
    }

    val jaegerEndpoint = System.getProperty("idea.diagnostic.opentelemetry.jaeger")
    if (jaegerEndpoint != null) {
      spanExporters.add(JaegerGrpcSpanExporter.builder().setEndpoint(jaegerEndpoint).build())
    }

    val endpoint = System.getProperty("idea.diagnostic.opentelemetry.otlp")
    if (endpoint != null) {
      spanExporters.add(OtlpGrpcSpanExporter.builder().setEndpoint(endpoint).build())
    }

    val tracerProvider = SdkTracerProvider.builder()
      .addSpanProcessor(BatchSpanProcessor.builder(SpanExporter.composite(spanExporters)).build())
      .setResource(Resource.create(Attributes.of(
        ResourceAttributes.SERVICE_NAME, serviceName,
        ResourceAttributes.SERVICE_VERSION, serviceVersion,
        ResourceAttributes.SERVICE_NAMESPACE, serviceNamespace
      )))
      .build()
    sdk = OpenTelemetrySdk.builder()
      .setTracerProvider(tracerProvider)
      .buildAndRegisterGlobal()

    if (spanExporters.isNotEmpty()) {
      ShutDownTracker.getInstance().registerShutdownTask(Runnable {
        tracerProvider?.forceFlush()?.join(10, TimeUnit.SECONDS)
        JaegerJsonSpanExporter.finish()
      })
    }
  }

  /**
   * We do not provide default tracer - we enforce using of separate scopes for subsystems.
   */
  fun getTracer(scopeName: String): Tracer = sdk.getTracer(scopeName)
}