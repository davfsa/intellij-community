// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.intellij.build

interface ApplicationInfoProperties {
  val majorVersion: String
  val minorVersion: String
  val microVersion: String
  val patchVersion: String
  val fullVersionFormat: String
  val isEAP: Boolean
  val versionSuffix: String?
  /**
   * The first number from 'minor' part of the version. This property is temporary added because some products specify composite number (like '1.3')
   * in 'minor version' attribute instead of using 'micro version' (i.e. set minor='1' micro='3').
   */
  val minorVersionMainPart: String
  val shortProductName: String
  val productCode: String
  val productName: String
  val majorReleaseDate: String
  val edition: String?
  val motto: String?
  val companyName: String
  val shortCompanyName: String
  val svgRelativePath: String?
  val svgProductIcons: List<String>
  val patchesUrl: String?

  val upperCaseProductName: String
  val fullVersion: String
  val productNameWithEdition: String

  val appInfoXml: String
}
