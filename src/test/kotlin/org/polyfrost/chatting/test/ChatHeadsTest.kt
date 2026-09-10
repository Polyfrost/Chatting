package org.polyfrost.chatting.test

import com.mojang.authlib.GameProfile
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.polyfrost.chatting.chat.ChatHeads
import java.util.UUID

class ChatHeadsTest {

    private val beber = UUID.randomUUID()

    private fun info(name: String = "beber", id: UUID = beber): PlayerInfo =
        PlayerInfo(GameProfile(id, name), false)

    @Test
    fun `plain messages carry no server head`() {
        Assertions.assertFalse(ChatHeads.hasServerHeadFor(Component.literal("<beber> bruh"), info()))
    }

    //? if >=1.21.10 {
    private fun message(profile: net.minecraft.world.item.component.ResolvableProfile): Component =
        Component.empty()
            .append(
                Component.`object`(
                    net.minecraft.network.chat.contents.objects.PlayerSprite(profile, true)
                )
            )
            .append(" <beber> bruh")

    @Test
    fun `a nested head with only a uuid is matched`() {
        val message = message(net.minecraft.world.item.component.ResolvableProfile.createUnresolved(beber))
        Assertions.assertTrue(ChatHeads.hasServerHeadFor(message, info()))
    }

    @Test
    fun `a nested head with only a name is matched`() {
        val message = message(net.minecraft.world.item.component.ResolvableProfile.createUnresolved("BEBER"))
        Assertions.assertTrue(ChatHeads.hasServerHeadFor(message, info()))
    }

    @Test
    fun `another players head does not suppress ours`() {
        val message = message(net.minecraft.world.item.component.ResolvableProfile.createUnresolved("notbeber"))
        Assertions.assertFalse(ChatHeads.hasServerHeadFor(message, info()))
    }
    //?}
}
