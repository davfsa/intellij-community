/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.toml.lang

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile

object TomlFileType : LanguageFileType(TomlLanguage) {
    override fun getName() = "TOML file"
    override fun getDescription() = "TOML file"
    override fun getDefaultExtension() = "toml"

    override fun getIcon() = AllIcons.FileTypes.Text!!

    override fun getCharset(file: VirtualFile, content: ByteArray) = "UTF-8"
}
