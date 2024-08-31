package com.kotlinspirit.json

import com.kotlinspirit.ext.CharStack

internal class ReverseJsonParser {
    private var index: Int = 0
    private val objectStack = CharStack()  // Stack for managing nested objects
    private val arrayStack = CharStack()   // Stack for managing nested arrays

    // Helper function to skip whitespaces (in reverse)
    private fun skipWhitespace(string: CharSequence) {
        while (index >= 0 && string[index].isWhitespace()) {
            index--
        }
    }

    // Helper function to parse a JSON string (in reverse)
    private fun parseString(string: CharSequence): Boolean {
        if (index < 0 || string[index] != '"') return false
        index-- // Skip the closing quote
        while (index >= 0) {
            val char = string[index]
            if (char == '\\') {
                index-- // Skip the escape character
            } else if (char == '"') {
                index-- // Skip the opening quote
                return true
            }
            index--
        }
        return false // Unterminated string
    }

    // Helper function to parse a JSON number (in reverse)
    private fun parseNumber(string: CharSequence): Boolean {
        if (index < 0 || (string[index] !in '0'..'9' && string[index] != '-')) return false
        while (index >= 0 && (string[index].isDigit() || string[index] in listOf('.', 'e', 'E', '+', '-'))) {
            index--
        }
        return true
    }

    // Helper function to parse literal values: true, false, null (in reverse)
    private fun parseLiteral(expected: String, string: CharSequence): Boolean {
        if (index + 1 < expected.length || index - expected.length + 1 < 0) return false
        for (i in expected.length - 1 downTo 0) {
            if (string[index] != expected[i]) return false
            index--
        }
        return true
    }

    // Helper function to parse a JSON array (in reverse)
    private fun parseArray(string: CharSequence): Boolean {
        if (index < 0 || string[index] != ']') return false
        arrayStack.push(']')
        index-- // Skip the closing bracket
        skipWhitespace(string)
        if (index >= 0 && string[index] == '[') {
            index-- // Empty array
            arrayStack.pop()
            return true
        }
        while (index >= 0) {
            if (!parseValue(string)) return false
            skipWhitespace(string)
            if (index >= 0 && string[index] == '[') {
                index-- // End of array
                arrayStack.pop()
                return true
            }
            if (index < 0 || string[index] != ',') return false
            index-- // Move past the comma
            skipWhitespace(string)
        }
        return false // Unterminated array
    }

    // Main function to parse a JSON object (in reverse)
    private fun parseObject(string: CharSequence): Boolean {
        if (index < 0 || string[index] != '}') return false
        objectStack.push('}')
        index-- // Skip the closing brace

        skipWhitespace(string)
        if (index >= 0 && string[index] == '{') {
            index-- // Empty object
            objectStack.pop()
            return true
        }

        while (index >= 0) {
            skipWhitespace(string)
            if (!parseValue(string)) return false // Parse value first (in reverse order)
            skipWhitespace(string)
            if (index < 0 || string[index] != ':') return false // Expecting a colon after the value in reverse
            index-- // Move past the ':'
            skipWhitespace(string)
            if (index < 0 || !parseString(string)) return false // Now parse the key
            skipWhitespace(string)
            if (index >= 0 && string[index] == '{') {
                index-- // End of object
                objectStack.pop()
                return true
            }
            if (index < 0 || string[index] != ',') return false
            index-- // Move past the comma
        }

        return false // Unterminated object
    }

    // Helper function to parse JSON values (in reverse)
    private fun parseValue(string: CharSequence): Boolean {
        skipWhitespace(string)
        if (index < 0) return false

        return when (string[index]) {
            '}' -> parseObject(string)
            ']' -> parseArray(string)
            '"' -> parseString(string)
            in '0'..'9', '-' -> parseNumber(string)
            'e' -> parseLiteral("true", string) || parseLiteral("false", string)
            'l' -> parseLiteral("null", string)
            else -> false
        }
    }

    // Public method to start parsing JSON objects in reverse
    fun parseJsonObject(seek: Int, string: CharSequence): Int? {
        index = seek
        if (index >= 0 && string[index] == '}') {
            if (parseObject(string)) {
                return index // Return the current index if the object was parsed successfully
            }
        }

        return null // If it doesn't start with '}', or parsing fails, it's not a valid JSON object
    }

    // Public method to start parsing JSON arrays in reverse
    fun parseJsonArray(seek: Int, string: CharSequence): Int? {
        index = seek
        if (index >= 0 && string[index] == ']') {
            if (parseArray(string)) {
                return index // Return the current index if the array was parsed successfully
            }
        }

        return null // If it doesn't start with ']', or parsing fails, it's not a valid JSON array
    }
}
