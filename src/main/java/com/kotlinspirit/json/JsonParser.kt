package com.kotlinspirit.json

import com.kotlinspirit.ext.CharStack

class JsonParser {
    private var index: Int = 0
    private val objectStack = CharStack()  // Stack for managing nested objects
    private val arrayStack = CharStack()   // Stack for managing nested arrays

    // Helper function to skip whitespaces
    private fun skipWhitespace(string: CharSequence) {
        while (index < string.length && string[index].isWhitespace()) {
            index++
        }
    }

    // Helper function to parse a JSON string
    private fun parseString(string: CharSequence): Boolean {
        if (index >= string.length || string[index] != '"') return false
        index++ // Skip the opening quote
        while (index < string.length) {
            val char = string[index]
            if (char == '\\') {
                index++ // Skip the escape character
            } else if (char == '"') {
                index++ // Skip the closing quote
                return true
            }
            index++
        }
        return false // Unterminated string
    }

    // Helper function to parse a JSON number
    private fun parseNumber(string: CharSequence): Boolean {
        if (index >= string.length || (string[index] !in '0'..'9' && string[index] != '-')) return false
        while (index < string.length && (string[index].isDigit() || string[index] in listOf('.', 'e', 'E', '+', '-'))) {
            index++
        }
        return true
    }

    // Helper function to parse literal values: true, false, null
    private fun parseLiteral(expected: String, string: CharSequence): Boolean {
        if (index + expected.length > string.length) return false
        for (char in expected) {
            if (string[index] != char) return false
            index++
        }
        return true
    }

    // Helper function to parse a JSON array
    private fun parseArray(string: CharSequence): Boolean {
        if (index >= string.length || string[index] != '[') return false
        arrayStack.push('[')
        index++ // Skip the opening bracket
        skipWhitespace(string)
        if (index < string.length && string[index] == ']') {
            index++ // Empty array
            arrayStack.pop()
            return true
        }
        while (index < string.length) {
            if (!parseValue(string)) {
                return false
            }
            skipWhitespace(string)
            if (index < string.length && string[index] == ']') {
                index++ // End of array
                arrayStack.pop()
                return true
            }
            if (index >= string.length || string[index] != ',') {
                return false
            }
            index++ // Move past the comma
            skipWhitespace(string)
        }
        return false // Unterminated array
    }

    // Main function to parse a JSON object
    private fun parseObject(string: CharSequence): Boolean {
        if (index >= string.length || string[index] != '{') return false
        objectStack.push('{')
        index++ // Move past the '{'

        skipWhitespace(string)
        if (index < string.length && string[index] == '}') {
            index++ // Empty object
            objectStack.pop()
            return true
        }

        while (index < string.length) {
            skipWhitespace(string)
            if (index >= string.length || !parseString(string)) return false // Parse key
            skipWhitespace(string)
            if (index >= string.length || string[index] != ':') return false
            index++ // Move past the ':'
            skipWhitespace(string)
            if (!parseValue(string)) return false // Parse value
            skipWhitespace(string)
            if (index < string.length && string[index] == '}') {
                index++ // End of object
                objectStack.pop()
                return true
            }
            if (index >= string.length || string[index] != ',') return false
            index++ // Move past the comma
        }

        return false // Unterminated object
    }

    // Helper function to parse JSON values
    private fun parseValue(string: CharSequence): Boolean {
        skipWhitespace(string)
        if (index >= string.length) return false

        return when (string[index]) {
            '{' -> parseObject(string)
            '[' -> parseArray(string)
            '"' -> parseString(string)
            in '0'..'9', '-' -> parseNumber(string)
            't' -> parseLiteral("true", string)
            'f' -> parseLiteral("false", string)
            'n' -> parseLiteral("null", string)
            else -> false
        }
    }

    // Public method to start parsing JSON objects
    fun parseJsonObject(seek: Int, string: CharSequence): Int? {
        index = seek
        if (index < string.length && string[index] == '{') {
            if (parseObject(string)) {
                return index // Return the current index if the object was parsed successfully
            }
        }
        return null // If it doesn't start with '{', or parsing fails, it's not a valid JSON object
    }

    // Public method to start parsing JSON arrays
    fun parseJsonArray(seek: Int, string: CharSequence): Int? {
        index = seek
        if (index < string.length && string[index] == '[') {
            if (parseArray(string)) {
                return index // Return the current index if the array was parsed successfully
            }
        }
        return null // If it doesn't start with '[', or parsing fails, it's not a valid JSON array
    }
}
