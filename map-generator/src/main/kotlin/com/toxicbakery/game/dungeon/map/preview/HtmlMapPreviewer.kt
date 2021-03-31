package com.toxicbakery.game.dungeon.map.preview

import com.toxicbakery.game.dungeon.map.MapLegend
import java.io.File

class HtmlMapPreviewer : MapPreviewer {

    override fun preview(mapSize: Int, mapData: Array<MapLegend>) {
        File("render.raw.html")
            .bufferedWriter()
            .use { writer ->
                writer.write(
                    """
                        |<html>
                        |<head>
                        |<title>Render</title>
                        |<style>
                        |html, body {
                        |   color: #fff;
                        |   background-color: #000;
                        |   font-family: "Lucida Console", Monaco, monospace;
                        |   white-space: nowrap;
                        |}
                        |._16, ._17, ._18, ._19, ._23 { color: green; }
                        |._20 { color: blue; }
                        |._22, ._24 { color: yellow; }
                        |._25 { color: cyan; }
                        |._28 { color: grey; }
                        |</style>
                        |</head>
                        |<body style="white-space: nowrap;">
                        |""".trimMargin()
                )
                for (x in 0 until mapSize) {
                    for (y in 0 until mapSize) {
                        val mapLegendValue = mapData[x * mapSize + y]
                        writer.write(mapLegendValue.htmlRepresentation)
                    }
                    writer.write("</br>\n")
                }
                writer.write("</body></html>")
            }
    }

}
