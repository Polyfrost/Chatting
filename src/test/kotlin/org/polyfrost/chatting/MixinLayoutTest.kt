package org.polyfrost.chatting

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Source-level invariants for the legacy renderer adapters. These catch config
 * drift and unqualified helper collisions before Mixin merges classes into a
 * shared Minecraft target.
 */
class MixinLayoutTest {
    private val mixinDirectory = Path.of("src/main/java/org/polyfrost/chatting/mixin")
    private val mixinConfig = Path.of("src/main/resources/mixins.chatting.ornithe.json")

    @Test
    fun `mixin configuration names every adapter exactly once`() {
        val config = Files.readString(mixinConfig)
        val clientSection = Regex("\\\"client\\\"\\s*:\\s*\\[(.*?)]", RegexOption.DOT_MATCHES_ALL)
            .find(config)?.groupValues?.get(1) ?: error("Missing client mixin list")
        val configured = Regex("\\\"([^\\\"]+)\\\"")
            .findAll(clientSection).map { it.groupValues[1] }.toSet()
        val sources = Files.walk(mixinDirectory).use { paths ->
            paths.filter { Files.isRegularFile(it) && it.extension == "java" && !it.fileName.toString().endsWith("Plugin.java") }
                .map { mixinDirectory.relativize(it).toString().removeSuffix(".java").replace('/', '.') }
                .toList().toSet()
        }
        assertEquals(sources, configured, "Mixin config and adapter sources must stay in parity")
    }

    @Test
    fun `private non-injection mixin helpers are unique and namespaced`() {
        val privateMethod = Regex("^\\s*private\\s+(?:static\\s+)?[\\w\\$<>, ?\\[\\].]+\\s+([\\w\\$]+)\\s*\\(")
        val injectionAnnotation = Regex("@(Inject|Redirect|ModifyArgs|ModifyArg|ModifyVariable|ModifyConstant|WrapOperation|Overwrite)")
        val uniqueAnnotation = Regex("@Unique")

        Files.walk(mixinDirectory).use { paths ->
            paths.filter { Files.isRegularFile(it) && it.extension == "java" }.forEach { source ->
                val lines = Files.readAllLines(source)
                lines.forEachIndexed { index, line ->
                    val name = privateMethod.find(line)?.groupValues?.get(1) ?: return@forEachIndexed
                    val annotations = lines.subList(maxOf(0, index - 16), index).joinToString("\n")
                    if (!injectionAnnotation.containsMatchIn(annotations)) {
                        assertTrue(name.startsWith("chatting$"), "$source:$name must use the chatting$ namespace")
                        assertTrue(uniqueAnnotation.containsMatchIn(annotations), "$source:$name must be @Unique")
                    }
                }
            }
        }
    }
}
