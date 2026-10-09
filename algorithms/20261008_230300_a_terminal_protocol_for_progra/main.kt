package terminal

fun main() {
    // Unit Test 1: Single well‑formed sequence
    val single = "\u001B]7501;Ready\u0007"
    val parsedSingle = parseOscSequences(single)
    assert(parsedSingle.size == 1) { "Expected exactly one parsed status." }
    assert(parsedSingle[0] == ProgramStatus(7501, "Ready")) { "Parsed status does not match expected." }
    val encodedSingle = encodeStatus(parsedSingle[0])
    assert(encodedSingle == single) { "Round‑trip encoding failed for single sequence." }

    // Unit Test 2: Multiple concatenated sequences
    val multi = "\u001B]7501;Init\u0007\u001B]7502;Running\u0007\u001B]7503;Done\u0007"
    val parsedMulti = parseOscSequences(multi)
    assert(parsedMulti.size == 3) { "Expected three parsed statuses." }
    assert(parsedMulti[0] == ProgramStatus(7501, "Init")) { "First status mismatch." }
    assert(parsedMulti[1] == ProgramStatus(7502, "Running")) { "Second status mismatch." }
    assert(parsedMulti[2] == ProgramStatus(7503, "Done")) { "Third status mismatch." }
    // Verify each round‑trip
    parsedMulti.forEach { status ->
        val roundTrip = encodeStatus(status)
        assert(multi.contains(roundTrip)) { "Encoded status not found in original string." }
    }

    // Unit Test 3: Malformed sequences are ignored
    val malformed = "\u001B]7501Ready\u0007\u001B]ABC;Bad\u0007\u001B]7502;Good\u0007"
    val parsedMalformed = parseOscSequences(malformed)
    assert(parsedMalformed.size == 1) { "Only one well‑formed status should be parsed." }
    assert(parsedMalformed[0] == ProgramStatus(7502, "Good")) { "Parsed good status incorrectly." }

    // Unit Test 4: Empty payload handling
    val emptyPayload = "\u001B]7504;\u0007"
    val parsedEmpty = parseOscSequences(emptyPayload)
    assert(parsedEmpty.size == 1) { "Empty payload should produce a status." }
    assert(parsedEmpty[0] == ProgramStatus(7504, "")) { "Empty payload not parsed correctly." }

    // Unit Test 5: Non‑ASCII characters in payload
    val unicodePayload = "\u001B]7505;Привет мир\u0007"
    val parsedUnicode = parseOscSequences(unicodePayload)
    assert(parsedUnicode.size == 1) { "Unicode payload should be parsed." }
    assert(parsedUnicode[0] == ProgramStatus(7505, "Привет мир")) { "Unicode payload mismatch." }

    // Demonstration output
    println("All unit tests passed. Parsed statuses from demo string:")
    val demo = "\u001B]7501;Start\u0007 some text \u001B]7502;Processing\u0007 end."
    val demoParsed = parseOscSequences(demo)
    demoParsed.forEach { println(it) }
}