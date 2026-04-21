package com.coc.zkqcode.jar.code.universal.recognizer

/**
 * Cleans OCR-recognized text by fixing common digit misrecognitions.
 * Replaces characters that are frequently confused with digits:
 * o/O -> 0, I/l -> 1, Z/z -> 2, S/s -> 5, G -> 6
 */
fun cleanOcrDigits(text: String): String {
    return text.replace(" ", "")
        .replace("o", "0").replace("O", "0")
        .replace("I", "1").replace("l", "1")
        .replace("Z", "2").replace("z", "2")
        .replace("S", "5").replace("s", "5")
        .replace("G", "6")
}
