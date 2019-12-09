package com.toxicbakery.game.dungeon.map.preview

import com.toxicbakery.game.dungeon.map.MapLegend
import java.io.File
import java.io.OutputStream

@Suppress("MagicNumber")
class BmpMapPreviewer : MapPreviewer {

    /**
     * Write int in LE format
     */
    private fun Int.toByteArray(): ByteArray {
        val byteArray = ByteArray(4)
        byteArray[0] = (this and 0xff).toByte()
        byteArray[1] = (this shr 8 and 0xff).toByte()
        byteArray[2] = (this shr 16 and 0xff).toByte()
        byteArray[3] = (this shr 24 and 0xff).toByte()
        return byteArray
    }

    /**
     * Write short in LE format
     */
    private fun Short.toByteArray(): ByteArray {
        val i = this.toInt()
        val byteArray = ByteArray(2)
        byteArray[0] = (i and 0xff).toByte()
        byteArray[1] = (i shr 8 and 0xff).toByte()
        return byteArray
    }

    /**
     * Draw map counting down on x for correct bmp orientation
     */
    private fun OutputStream.writeMapData(mapSize: Int, mapData: Array<MapLegend>) {
        for (x in mapSize - 1 downTo 0) {
            // Pixel color (BGR)
            for (y in 0 until mapSize) writeColorFor(mapData[x * mapSize + y])

            // Pad row to value divisible by 4
            for (i in 0 until mapSize % 4) write(0)
        }
    }

    /**
     * Write a MapLegend color to the stream in BGR format.
     */
    private fun OutputStream.writeColorFor(mapLegend: MapLegend) = write(
        when (mapLegend) {
            MapLegend.BEACH,
            MapLegend.DESERT -> byteArrayOf(0x00, 255.toByte(), 255.toByte())
            MapLegend.FOREST_1 -> byteArrayOf(34.toByte(), 139.toByte(), 34.toByte())
            MapLegend.FOREST_2 -> byteArrayOf(0x00, 139.toByte(), 0x00)
            MapLegend.FOREST_3 -> byteArrayOf(0x00, 128.toByte(), 0x00)
            MapLegend.FOREST_4 -> byteArrayOf(0x00, 100.toByte(), 0x00)
            MapLegend.PLAIN -> byteArrayOf(0.toByte(), 252.toByte(), 124.toByte())
            MapLegend.OCEAN -> byteArrayOf(200.toByte(), 0x00, 0x00)
            MapLegend.MOUNTAIN -> byteArrayOf(128.toByte(), 128.toByte(), 128.toByte())
            else -> byteArrayOf(255.toByte(), 255.toByte(), 255.toByte())
        }
    )

    /**
     * BMP file header.
     */
    private fun OutputStream.writeFileHeader(mapSize: Int) {
        write(0x42) // B
        write(0x4D) // M
        write((54 + mapSize * mapSize).toByteArray()) // BMP Size (header + img)
        write(ByteArray(4)) // App Name
        write(54.toByteArray()) // BMP Data offset
    }

    /**
     * BMP basic header using 3 byte colors in BGR format.
     */
    private fun OutputStream.writeDibHeader(mapSize: Int) {
        write(40.toByteArray()) // Header Size
        write(mapSize.toByteArray()) // Width
        write(mapSize.toByteArray()) // Height
        write(1.toShort().toByteArray()) // Planes
        write(24.toShort().toByteArray()) // bits per pixel (BGR format)
        write(0.toByteArray()) // BI_RGB flag (no pixel array compression)
        write((mapSize * mapSize).toByteArray()) // BMP size including padding
        write(2835.toByteArray()) // Pixels/meter horizontal
        write(2835.toByteArray()) // Pixels/meter vertical
        write(0.toByteArray()) // Colors in palette
        write(0.toByteArray()) // Important color count
    }

    override fun preview(mapSize: Int, mapData: Array<MapLegend>) = File("render.bmp")
        .outputStream()
        .use { stream ->
            stream.writeFileHeader(mapSize)
            stream.writeDibHeader(mapSize)
            stream.writeMapData(mapSize, mapData)
        }

}
