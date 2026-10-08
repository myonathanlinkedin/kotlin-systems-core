package terminal

/**
 * Encodes a [ProgramStatus] into an OSC 7501 control string.
 * Format: ESC ] <code> ; <payload> BEL
 */
fun encodeStatus(status: ProgramStatus): String {
    // ESC = 0x1B, BEL = 0x07
    return "\u001B]${status.code};${status.payload}\u0007"
}

/**
 * Parses all well‑formed OSC 7501 sequences found in [input].
 * Returns a list of [ProgramStatus] objects in the order they appear.
 * Malformed sequences are silently ignored.
 */
fun parseOscSequences(input: String): List<ProgramStatus> {
    val results = mutableListOf<ProgramStatus>()
    var index = 0
    while (index < input.length) {
        // Look for ESC followed by ']'
        if (input[index] == '\u001B' && index + 1 < input.length && input[index + 1] == ']') {
            var cursor = index + 2
            // Extract numeric code
            val codeStart = cursor
            while (cursor < input.length && input[cursor].isDigit()) {
                cursor++
            }
            if (cursor == codeStart) {
                // No digits after ESC]
                index++
                continue
            }
            val codeString = input.substring(codeStart, cursor)
            val code = codeString.toIntOrNull()
            if (code == null) {
                index++
                continue
            }
            // Expect ';' delimiter
            if (cursor >= input.length || input[cursor] != ';') {
                index++
                continue
            }
            cursor++ // Move past ';' to payload start
            val payloadStart = cursor
            // Scan until BEL (0x07)
            while (cursor < input.length && input[cursor] != '\u0007') {
                cursor++
            }
            if (cursor >= input.length) {
                // No terminating BEL – malformed
                index++
                continue
            }
            val payload = input.substring(payloadStart, cursor)
            results.add(ProgramStatus(code, payload))
            // Advance index past the terminating BEL
            index = cursor + 1
        } else {
            index++
        }
    }
    return results
}