package com.toxicbakery.game.dungeon

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
        val taggedMessage = "$tag: $message"
        if (args.isNullOrEmpty()) console.log(taggedMessage)
        else console.log(taggedMessage, args)
    }

    companion object {
        private const val CALLER_POSITION = 5
    }

}
