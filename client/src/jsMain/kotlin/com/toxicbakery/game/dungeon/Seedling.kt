package com.toxicbakery.game.dungeon

import com.toxicbakery.logging.Arbor
import com.toxicbakery.logging.ISeedling

private external class Error

class Seedling : ISeedling {

    override val tag: String
        get() = Error()
            .asDynamic()
            .stack
            .toString()
            .split("\n")[CALLER_POSITION]
            .trim()
            .substringAfter("at ")
            .substringBefore(' ')

    override fun log(
        level: Int,
        tag: String,
        msg: String,
        throwable: Throwable?,
        args: Array<out Any?>?
    ) {
        val message = if (throwable == null) msg else "$msg\n$throwable"
        val taggedMessage = "${level.toLevelString()}$tag: $message"
        if (args.isNullOrEmpty()) level.log(taggedMessage)
        else level.log(taggedMessage, args)
    }

    private fun Int.log(msg: String) {
        when (this) {
            Arbor.VERBOSE,
            Arbor.DEBUG -> console.log(msg)
            Arbor.INFO -> console.info(msg)
            Arbor.WARNING -> console.warn(msg)
            else -> console.error(msg)
        }
    }

    private fun Int.log(msg: String, args: Array<out Any?>) {
        when (this) {
            Arbor.VERBOSE,
            Arbor.DEBUG -> console.log(msg, *args)
            Arbor.INFO -> console.info(msg, *args)
            Arbor.WARNING -> console.warn(msg, *args)
            else -> console.error(msg, *args)
        }
    }

    private fun Int.toLevelString(): String = when (this) {
        Arbor.DEBUG -> "D/"
        Arbor.ERROR -> "E/"
        Arbor.INFO -> "I/"
        Arbor.VERBOSE -> "V/"
        Arbor.WARNING -> "W/"
        else -> "WTF/"
    }

    companion object {
        private const val CALLER_POSITION = 5
    }

}
