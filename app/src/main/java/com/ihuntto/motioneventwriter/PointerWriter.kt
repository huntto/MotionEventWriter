package com.ihuntto.motioneventwriter

import java.io.BufferedOutputStream
import java.io.OutputStream

fun write(outputStream: OutputStream, pointers: HashMap<Int, ArrayList<PointerView.Point>>) {
    BufferedOutputStream(outputStream).use { bufferedStream ->
        val sb = StringBuilder()
        for (pointer in pointers) {
            for (point in pointer.value) {
                sb.append(point.x).append(',')
                    .append(point.y).append(',')
                    .append(point.time).append(',')
            }
            sb.deleteCharAt(sb.length - 1)
            sb.append(System.lineSeparator())
        }
        bufferedStream.write(sb.toString().encodeToByteArray())
        bufferedStream.flush()
    }
}
