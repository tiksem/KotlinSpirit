# KotlinSpirit

Lightweight library for creating parsers, inspired by C++ boost spirit library.

# Introduction

There are no many good libraries or frameworks to parse text easily in Kotlin. Yeah, we have regular expressions. But they are hard to debug, hard to read, relatively hard to reuse and they crash with StackOverflow exception on large inputs. Yeah, we have parser generators. But they are too powerful and complex for simple use cases. So the idea was to create a simple library with compile-time expressions checking to create parsers fast and integrate them easily directly with Kotlin code and Kotlin types.

# Gradle installation

Add the following into your repositories section
```
repositories {
  ...
  maven { url 'https://jitpack.io' }
}
```

Add the following dependency into your `build.gradle` file dependencies section
```
implementation "com.github.tiksem:KotlinSpirit:1.3.1"
```

# Creating a simple parser
KotlinSpirit consists of basic rules and operators. All the rules are defined in Rules object namespace. In all the examples below we consider, that the rules are already imported from Rules object.

Let's create a simple parser for key-value pair, where the key is name and the value is age, as our first example

```Kotlin
val name = char('A'..'Z') + +char('a'..'z')
val age = int
val r = name + '=' + age
val parser = r.compile() // after creating a rule we compile it into a parser
parser.matches("Ivan=43") // true
parser.matches("ivan=43") // false
```

This simple example demonstrates usages of char and int basic rules, sequence operator +, and oneOrMore operator +. In this example we used `matches` parser function, which checks if a string matches from the beginning to the end. We will discuss how to get parsing results and use other parser functions later.

# Basic rules
Let's describe basic rules used as simple units for creating complex rules. Don't forget to import them from Rules object namespace.

## Number rules
Those rules represent the corresponding numbers, with bound checks for integers
```
int, long, float, double, uint, ulong, short, ushort, byte, ubyte, bigDecimal, bigint
```
You can also pass ranges and exact numbers to match spesific values
```
int(-12)
long(11L..4334L)
```
## Char rules
`char` represents any single chcracter

`char(vararg ch: Char)` Represents any character from the characters list. For example: `val operators = char('+', '-', '*', '/')`

`char(vararg range: CharRange)` Represents characters from the given ranges. For example: `val letter = char('a'..'z', 'A'..'Z')`

`char(chars: CharArray, ranges: Array<CharRange>)` Mix of chars list and ranges

`charIf(predicate: (Char) -> Boolean)` Char with a custom matching predicate
## Boolean rule
Mathches true or false
```
boolean
```
## String rules
`str(string: String, ignoreCase: Boolean = false)` Matches an exact string. For example `str("Sun")`

All the string rules below have similar signatures as char rules and they match 0 or more characters. 
Generally speaking those rules are always successful.
```Kotlin
str(vararg ch: Char)
str(vararg range: CharRange)
str(chars: CharArray, ranges: Array<CharRange>)
str(predicate: (Char) -> Boolean)
```
If you want to match a string with at least one character you can use `nonEmptyStr` instead.

In our example above you may notice, that the name rule contains `+char('a'..'z')`, that is basically the same as `nonEmptyStr('a'..'z')`.

`+rule` is a repeat operator. We will discuss it later.

### OneOf string rule
Matches one of the strings from a given list. The rule is always greedy so it tries to match as long string as possible from the given list
```Kotlin
oneOf(vararg strings: CharSequence)
```
Example: `oneOf("Jhon", "Ivan", "Bin")` matches one of the names.

#### OneOf case insensitive string rule
Matches one of the strings from a given list, ignoring case. The rule is always greedy so it tries to match as long string as possible from the given list
```Kotlin
caseInsensitiveOneOf(vararg strings: CharSequence)
```
#### Skipper
KotlinSpirit adds an additional ability to parse more complicated cases by detecting words in text, where characters are divided by spesific delimiters, using skipper in your OneOf rule.
Let's imagine we want to detect and replace banned words with an empty string.
```Kotlin
val bannedWordsRule = caseInsensitiveOneOf(strings = listOf('slave', 'blood', 'murder', 'drugs')) // Without using a skipper
val parser = bannedWordsRule.compile()
val text = "I want to drink your blood dirty slave, I will teach you how to commit a murder and buy some drugs!"
parser.repalceAll(text, "") // This will return 'I want to drink your  dirty , I will teach you how to commit a  and buy some !'

// Let's try to modify our words a little bit, so the example doesn't work
val text = "I want to drink your b-l-o-o-d dirty sla**ve, I will teach you how to commit a m-u-r-d-e-r and buy some d-r*u-gs!"
// To make it work now we have to add a skipper. This will make sure all the characters are divided by this skipper
val skipper = (space or char('*',',','-','.','\'','"')).repeat()
val bannedWordsRule = caseInsensitiveOneOf(strings = listOf('slave', 'blood', 'murder', 'drugs'), skipper = skipper)
val parser = bannedWordsRule.compile()
parser.repalceAll(text, "") // This will return 'I want to drink your  dirty , I will teach you how to commit a  and buy some !'
```

#### Performance note:
The search is optimized by using `TernarySearchTree` for matching strings. To maximize the performance try to order the strings in oneOf to be NOT sorted, this will keep `TernarySearchTree` more balanced.

Also it's more effective to use or operator for small sequence of strings with minimum repeted substrings. So the example above could be written as:
```Kotlin
str("Jhon") or "Ivan" or "Bin"
```
We will discuss `or` operator later.

# Operators

Operators help us to create complex rules from basic rules.

## Sequence rule
The resultType of the rule is `CharSequence`. We will discuss how to retrieve parsing results later.

`val sequence = a + b` matches `a` followed by `b`.
As an example let's consider we want to create a math expression with 2 integers and an operator.
```Kotlin
val exp = int + char('+', '-', '/', '*') + int
```
## Or rule
The resultType of the rule depends on the resultTypes of `a` and `b`. If the resultTypes are the same, the resultType of `or` will be the resultType of `a` and `b`. Otherwise it will be Any or something less generic in some cases.

`val exp = a or b` matches `a` or `b`

As an example let's consider we want to create a parser, which parses an input, where a user can specify his username or an identification number.
```Kotlin
val id = long or (char('a'..'z') + +char('a'..'z', '0'..'9'))
```
## Difference rule
The resultType of the rule is the same as the resultType of `a`.

`val exp = a - b` matches `a` only when it doesn't match `b` at the same time.

As an example let's create Kotlin comment parser
```Kotlin
val comment = str("/*") + (char - "*/").repeat() + "*/"
```
It starts with `/*`, then it eats every character, until it finds `*/` and then closes it with `*/` at the end. Repeat matches 0 or more times of the give expression. We will discuss it later.

## Not rule
The resultType of the rule is Char

`val exp = !a` Matches one character, if it doesn't match rule a. If we are at the end of input, and `a` doesn't match EOF, it outputs '\0' as a result

This rule is similar to some cases of the difference rule. For example `char - 'a'` is similar to `!char('a')`. The difference is only at the end of input.
`!char('a')` matches EOF, but `char - 'a'` doesn't match EOF.

## Repeat rules
The resultType of repeat rules might be different, depending on the repeated rule kind. If the repeated rule is `Char` rule the result is `CharSequence` in all other cases it is `List<T>`, where `T` is the resultType of the repeared rule.

Repeat rule is specified by:

`repeated.repeat()` Repeat 0 or more times

`repeated.repeat(n: Int)` Repeat exactly n times

`repeated.repeat(m..n)` Repeat from m to n times

`+repeated` Repeat 1 or more times

Char repeat example:
```Kotlin
val name = char('A'..'Z') + char('a'..'z').repeat(1..19)
```
Matches any name, started with an uppercase letter, ends with lowercase letters, and has 2 - 20 length.

Lets repeat the names
```Kotlin
val names = +names
```
matches a list of names, at least one name should be specified. 

For example: `names.compile().parseGetResultOrThrow("HelloWorldYo")` will return list `["Hello", "World", "Yo"]`

## Split rule
The resultType of split rule is `List<T>`

`val exp = a % divider` Matches a list of `a` items splitted by a `divider` rule.

`a.split(divider: Rule<*>, n: Int)` Matches `a` splitted by `divider` exactly `n` times
  
`a.split(divider: Rule<*>, range: IntRange)` Matches `a` splitted by `divider` i times, where i in range.

`a % divider` Matches `a` splitted by `divider` i times, where i in [1, MAX_INT].

Let's consider we want to implement a parser of numbers divided by ',':
```Kotlin
val numbers = int % ','
val parser = numbers.compile()
val result = parser.parseGetResultOrThrow("12,16,76,1233,-5")
```
The result will be [12,16,76,1233,-5]
  
## Optional rule
The resultType of optional rule is T?, where T is the result of `a`
`val optional = -a` This rule is always successful. It matches `a` if possible, if not it just outputs null.

Warning: If your root parser rule is OptionalRule `parseGetResultOrThrow` will always throw an exception. This issue is going to be fixed in the next versions of KotlinSpirit.

## FailIf rule
The resultType of the rule is the same as the resultType of `a` rule

`a.failIf(predicate: (T) -> Boolean)` Checks the result of `a` and fails if the passed predicate returns true.

Let's consider we want to create a name parser, where the name is not Jhon.
```Kotlin
val name = char('A'..'Z') + +char('a'..'z')
val nameButNotJhon = name.failIf { it == "Jhon" }
```

## Quoted rule
Quoted rule represents rule `a` quoted by `left` and `right` rules. If you specify only a single rule as an argument of `quoted` `left` and `right` will be the same. You may ask what is the difference between sequence rule `left + a + right` and `a.quoted(left, right)`. The difference is the result. The resultType of sequence rule is always CharSequence, started from the beginning to the end of the match, so quotes are included in the result as well. However the quoted rule result is the same as `a` result.

Let's consider we want to implement quoted string parser:
```Kotlin
val quotedStr = (char - '"').repeat().quoted('"').compile()
val result = quotedStr.parseGetResultOrThrow("\"Hello, world!\"")
```
In the example `result` will be `Hello, world!`. But not `"Hello world!"`

## String wrapper rule
Converts any rule to a rule with CharSequence result, reperesenting the match's substring.
```Kotlin
rule.asString()
```
Example. Let's parse a list of names into a string.
```Kotlin
val name = char('A'..'Z') + +char('a'..'z')
val names = (+name).asString()
val namesString: CharSequence = names.compile.parseGetResultOrThrow("JhonIvanAbdula")
```
In this example we get the list of names as a string. However if we don't use `asString()`, names will have List<CharSequence> result.

## End rule
End indicates the end of input. If the end of input is reached the rule is successful but fails otherwise.
#### Example:
```Kotlin
val intPrefixRule = int + !end
val parser = intPrefixRule.compile()
parser.matches("123abc") // true
parser.matches("123") // false
parser.matches("123.34") // true
```

## Start rule
Start indicates the beginning of the input. If the seek is at the beginning of the inputthe rule is successful but fails otherwise.
#### Example:
```Kotlin
val intPrefixRule = start + int
val parser = intPrefixRule.compile()
parser.matches("123abc") // true
parser.matches("a123") // false
parser.matches("123.34") // true
```

# Parser functions
Each rule contains its result after parsing, when you parse without a result, just for matching, the runtime performance will be a little bit better, but the difference is usually not noticeable.

## Matching functions

Returns true if the string matches the rule from the beginning to the end.
```Kotlin
fun matches(string: CharSequence): Boolean
```
Checks if the string matches the rule from the beginning to the end. If no, throws ParseException.
```Kotlin
fun matchOrThrow(string: CharSequence)
```
Returns true if the string matches the rule from the beginning only.
```Kotlin
fun matchesAtBeginning(string: CharSequence): Boolean
```

## Parsing functions

Parses and gets the result, if the rule doesn't match throws ParseException
```Kotlin
fun parseGetResultOrThrow(string: CharSequence): T
```
Parses without any result returning the ending seek, if the rule doesn't match throws ParseException.
```Kotlin
fun parseOrThrow(string: CharSequence): Int
```
Parses without any result, returns ending seek if rule matches and null otherwise.
```Kotlin
fun tryParse(string: CharSequence): Int?
```
Parses with returning ParseResult. it contains the result or errorCode if the rule doesn't match
```Kotlin
fun parseWithResult(string: CharSequence): ParseResult<T>
```
Parses without a result returning ParseSeekResult. ParseSeekResult contains ending seek and errorCode.
```Kotlin
fun parse(string: CharSequence): ParseSeekResult
```

## Searching functions
Unlike parsing functions, searching functions try to find the result from the beginning to the end, moving a seek. But parsing functions fail immidiatly if the result doesn't match at the beginning.

Retrurns an index of the first match or null if the match is not found.
```Kotlin
fun indexOf(string: CharSequence): Int?
```
Retrurns an index of the shortest last match or null if the match is not found.
```Kotlin
fun lastIndexOfShortestMatch(string: CharSequence): Int?
```
Retrurns an index of the longest last match or null if the match is not found.
```Kotlin
fun lastIndexOfLongestMatch(string: CharSequence): Int?
```
Retrurns a result and a range of the first match or null if the match is not found.
```Kotlin
fun findFirstResult(string: CharSequence): ParseRangeResult<T>?
```
Retrurns a result of the first match or null if the match is not found.
```Kotlin
fun findFirst(string: CharSequence): T?
```
Retrurns a range of the first match or null if the match is not found.
```Kotlin
fun findFirstRange(string: CharSequence): ParseRange?
```
Retrurns results of all matches.
```Kotlin
fun findAll(string: CharSequence): List<T>
```
Retrurns results and ranges of all matches.
```Kotlin
fun findAllResults(string: CharSequence): List<ParseRangeResult<T>>
```
Returns a number of all matches
```Kotlin
fun count(string: CharSequence): Int
```

## Replace functions

Replaces the first match
```Kotlin
fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence
```
Replaces all matches
```Kotlin
fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence
```
Replaces the first match using a custom replacementProvider, it takes the rule's result as its argument.
```Kotlin
fun replaceFirst(source: CharSequence, replacementProvider: (T) -> Any): CharSequence
```
Replaces matches using a custom replacementProvider, it takes the rule's result as its argument.
```Kotlin
fun replaceAll(source: CharSequence, replacementProvider: (T) -> Any): CharSequence
```
Replaces the first match. Returns null if the match was not found
```Kotlin
fun replaceFirstOrNull(source: CharSequence, replacement: CharSequence): CharSequence?
```
Replaces the first match using a custom replacementProvider. Returns null if the match was not found
```Kotlin
fun replaceFirstOrNull(source: CharSequence, replacementProvider: (T) -> CharSequence): CharSequence?
```

## String extensions
Most of the Parser functions are added as string extensions for convinience. The drawback is that a rule is recreated all the time when you call the function again. So if you have a huge rule, it's recommended to compile it into Parser. And you should aware, that a rule is not synchronized. So you can't save a rule and execute it from different threads, unless `rule.isThreadSafe()` returns true

The correct way of using an extension:
```Kotlin
fun replaceIdesWithNamesSplittedByDots(string: String, namesMap: Map<Int, String>): CharSequence {
    return string.replaceAll(int % ',') { ides -> ides.joinToString(".") { id -> namesMap[id] ?: "error" } }
}
```
Here `int % ','` is recreated all the time we call `replaceIdesWithNamesSplittedByDots`. So it's totally safe to use it from different threads.

A potentially wrong way:
```Kotlin
val ints = int % ','
fun replaceIdesWithNamesSplittedByDots(string: String, namesMap: Map<Int, String>): CharSequence {
    return string.replaceAll(ints) { ides -> ides.joinToString(".") { id -> namesMap[id] ?: "error" } }
}
```
Here `int % ','` is created once. And we use the same rule from different threads. It's unsafe unless `(int % ',').isThreadSafe()` returns true.

### Using safe()
A new alternative of creating a thread-safe rule without compiling it into a parser is using `safe()` method that returns a thread-safe rule. It invokes `rule.isThreadSafe()` internally and makes the rule thread-safe if required.
```Kotlin
val ints = (int % ',').safe()
fun replaceIdesWithNamesSplittedByDots(string: String, namesMap: Map<Int, String>): CharSequence {
    return string.replaceAll(ints) { ides -> ides.joinToString(".") { id -> namesMap[id] ?: "error" } }
}
```


# Recursive expressions
Let's consider that there is a case: rule `a` could point to rule `b` and rule `b` could point to rule `a`. Or even rule `a` points to rule `a`. So we get a recursion here.

Let's discuss a real-time example. We want to parse a mathematic expression like 5 + (34 + 48).
```Kotlin
val operator = char('+', '-', '*', '/')
val value = expressionInBrackets or double
val expression = value + operator + value
val expressionInBrackets = expression.quoted('(', ')')
```
Looks clear. However, if you try to run it you will get StackOverflow error. Let's see how we can solve it.

## Lazy rules
Let's rewrite our above example using lazy rules. Lazy rules are rules, computed at runtime.
```Kotlin
val operator = char('+', '-', '*', '/')
val value = lazy { expressionInBrackets or double }
val expression = value + operator + value
val expressionInBrackets = expression.quoted('(', ')')
```
Ok we fixed StackOverflow error here. But let's move further and figure out how we can create rules with custom results and resolve recursive issues as well.

# Capturing custom results
You may be wondering how do we get results from nested rules during parsing. `parseWithResult` function of Parser is quite limited.

## Rule callbacks
Each rule can have a custom callback specified, this callback is called when the rule is successful. Let's come back to our first example where we parsed a key-value pair of name and age. And specify callbacks to retrieve the results.
```Kotlin
var name = ""
var age = -1
val nameRule = char('A'..'Z') + +char('a'..'z')
val rule = nameRule { name = it } + '=' + int { age = it }
```
Callbacks are usually used in Grammar or Repalcer. We will discuss them later.

## getRange, getRangeResult hooks
Sometimes you need to get a range of your rule's match. You can use getRange and getRangeResult hooks for that. Warning: getRange and getRangeResult are not properly synchronized for multithreading usage. So you need to use them inside Grammar or Replacer. See Thread safety section for a reference. 

getRange accepts ParseRange as a parameter. ParseRange is filled with startSeek and endSeek during parsing, when the rule is succesful. You can create a range using `range()` function. The example shows how to find int in a string and get its range
```Kotlin
val range = range()
val r = (!int).repeat() + int.getRange(range) + (!int).repeat()
r.compile.match("yoyoyo322323yoyoyo")
Assert.assertEquals(range.startSeek, "yoyoyo".length)
Assert.assertEquals(range.endSeek, "yoyoyo322323".length)
```
getRangeResult is the same as getRange, but it also parses a result, so it takes ParseRangeResult. To create ParseRangeResult use `rangeResult()` function.

```Kotlin
val rangeResult = rangeResult()
val r = (!int).repeat() + int.getRangeResult(rangeResult) + (!int).repeat()
r.compile.match("yoyoyo322323yoyoyo")
Assert.assertEquals(rangeResult.data, 322323)
Assert.assertEquals(rangeResult.startSeek, "yoyoyo".length)
Assert.assertEquals(rangeResult.endSeek, "yoyoyo322323".length)
```

## Grammars
Grammars are used to create rules with custom results. They are computed at runtime, so can be used instead of lazy rules.

To create a grammar we need to override
```Kotlin
abstract val result: T
abstract fun defineRule(): Rule<*>
open fun resetResult() {}
```
resetResult is optional and it's needed to reinitialize the result, cause we might receive the result from the previous parsing process.

Let's rewrite our above example with callbacks using Grammar
```Kotlin
data class Person(
    val name: String,
    val age: Int
)

val personRule = object : Grammar<Person>() {
    private var personName: CharSequence = ""
    private var age: Int = -1

    override val result: Person
        get() = Person(personName, age)

    override fun defineRule(): Rule<*> {
        val nameRule = char('A'..'Z') + +char('a'..'z')
        return nameRule { personName = it } + '=' + int { age = it }
    }
}.toRule()
```
Note: `toRule` is used to convert the grammar to a rule.

## Groups

Groups are simplified and less flexible replacements for grammars for simple use cases. With `group` operator you can spescify which rule is responsible for setting the result of the expression. The result of the group rule is always the result of a rule marked with `asResult()` call. Let's consider we want to parse unsigned int literal, 124454u. It contains u suffix. To create a parser for that spesific case you can use grammar and override `defineRule` with uint + 'u' expression. And there is nothing wrong with this solution. However let's try to implement this unsigned int literal parser using `group`.

```Kotlin
val unsignedIntLiteralRule = group(uint.asResult() + 'u')
val parser = unsignedIntLiteralRule.compile()
parser.parseGetResultOrThrow("434334u") // 434334
```
Note: The expression inside group should contain sequence built using + operator and it should cotnain only a single `asResult()` call.

### withSuffix withPrefix
These are factory methods that create simple groups with prefix and suffix.
```
rule.withSuffix(suffix) -> group(rule.asResult() + suffix)
rule.withPrefix(prefix) -> group(prefix + rule.asResult())
```
Let's rewrite our example above with `withSuffix` method.
```Kotlin
val unsignedIntLiteralRule = uint.withSuffix('u')
```

## Dynamic string rule
This rule is usually used to remember some token during the parsing process, the result of the rule is `CharSequence`

The rule has the following syntax `dynamicString { someToken }`

As an example let's create a simple parser for html tag with body and without any nested tags and attributes
```Kotlin
private data class Tag(
    val body: String,
    val name: String
)

private val parser = object : Grammar<Tag>() {
    private var name = ""
    private var body = ""

    override val result: Tag
        get() = Tag(body, name)

    override fun defineRule(): Rule<*> {
        return char('<') + (nonEmptyLatinStr {
            name = it.toString()
        }) + char('>') + ((char - '<').repeat()) {
            body = it.toString()
        } + "</" + dynamicString {
            name
        } + '>'
    }
}.toRule().compile()
```
Here we need to remeber the name of the tag during parsing, so we make sure that the closing tag matches the opening tag.

Now let's test it:
```Kotlin
parser.matchOrThrow("<a></a>")
parser.matchOrThrow("<a>Hello!</a>")
Assert.assertFalse(parser.matches("<a>Hello!</b>"))
Assert.assertFalse(parser.matches("<b>Hello!</a>"))
Assert.assertEquals(parser.parseGetResultOrThrow("<a>Hello!</a>"), Tag(body = "Hello!", name = "a"))
```
## Dynamic rule
This rule works the same way as `dynamicString` above, but it's more advanced, cause you can return any dynamically generated rule you want.
For example: `dynamicRule { int or double }`

## Suffix rule
The rule has the same result as the main rule. The result seek of the rule is the same as the main's rule result seek. It points that some suffix is required after the main rule.
```Kotlin
fun expectsSuffix(other: Rule<*>): SuffixExpectationRule<T>
```
```Kotlin
val parser = int.expectsSuffix("yo!").compile()
parser.parseGetResultOrThrow("345yo!") // 345
parser.tryParse("345yo!") // "345".length
```

## Prefix rule
The rule has the same result as the main rule. The result seek of the rule is the same as the main's rule result seek. It points that some prefix is required before the main rule.
```Kotlin
fun requiresPrefix(other: Rule<*>): RequiresPrefixRule<T>
```
```Kotlin
val parser = (+char('a'..'z')).requiresPrefix(int).compile()
parser.findFirst("345yo") // yo
```
Prefix rule is complicated. It requires reverse search to check for the prefix. It means it requires some special attention for grammars and callbacks. So if your grammar is inside the prefix rule all the callbacks are executed in a reverse order. Let's write a simple xml parser with tags only and without attributes to see how it works.
```Kotlin
private data class Xml(
    val body: List<Any>,
    val name: String
)

private val xmlRule = object : Grammar<Xml>() {
    private var name = ""
    private var body = emptyList<Any>()

    override val result: Xml
        get() = Xml(body, name)

    override fun defineRule(): Rule<*> {
        val firstTagNameOccurrenceRule = nonEmptyLatinStr {
            name = it.toString()
        }

        val tagName = dynamicRule {
            if (name.isEmpty()) {
                firstTagNameOccurrenceRule
            } else {
                // Second tagName occurrence
                str(name)
            }
        }

        val openingTag = char('<') + tagName + char('>')

        val closedTag = str("</") + tagName + '>'

        return openingTag + xmlTagBody {
            body = it
        } + closedTag
    }

    override fun resetResult() {
        name = ""
    }
}.toRule()

private val xmlTagBody: Rule<List<Any>> = (xmlRule or (char - char('<', '>')).repeat()).repeat()
```
Here we use `dynamicRule` to identify if we return a tagName we already remembered during parsing, or use nonEmptyLatinStr to check a tag. If we simply keep the same order as we did in our `Dynamic string rule` example above, the parser will not work for reverse search, used in the prefix rule.

## Regexp rule
This rule is used to mix regular expressions with KotlinSpirit rules. The rule has `kotlin.text.MatchResult` result.
```Kotlin
val ipAddressRule = regexp("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)")
val name = char('A'..'Z') + +char('a'..'z')
val ipAddressNamePair = ipAddressRule + "->" + name
val parser = ipAddressNamePair.compile()
parser.matches("127.0.0.1->Jhon") // true
```
#### Warning
Be careful with using $ and ^ operators, usually. Regexp rule always check the regex to match from the current seek during parsing process and it uses `matchesAt` method of `kotlin.text.Regex`

## JSON rules
The result of these rules is ParseRange object containing startSeek and endSeek of the match. There are `jsonObject` and `jsonArray` rules.
Here are some examples:
```Kotlin
val jsonStringObject = """text { "key": "value", "array": [1, 2, 3], "nested": { "a": true } } some other text """
Assert.assertEquals(jsonStringObject.findFirst(jsonObject), ParseRange(jsonStringObject.indexOf('{'), jsonStringObject.lastIndexOf('}') + 1))
```
```Kotlin
val jsonStringArray = """text [ { "key": "value" }, { "key2": "value2" } ] some other text """
Assert.assertEquals(jsonStringArray.findFirst(jsonArray), ParseRange(jsonStringArray.indexOf('['), jsonStringArray.lastIndexOf(']') + 1))
```
### Factory functions
Sometimes ParseRange is not so convenient to get parse results from JSON objects and arrays. Here are some factory methods that help.
```Kotlin
fun <To : Any> jsonObject(mapper: (CharSequence) -> To): TransformRule<CharSequence, To>
fun <To : Any> jsonArray(mapper: (CharSequence) -> To): TransformRule<CharSequence, To>
```
Here is an example of creating orgJson rules
```Kotlin
val orgJsonObjectRule = jsonObject {
    JSONObject(it.toString())
}

val orgJsonArrayRule = jsonArray {
    JSONArray(it.toString())
}
```

# Building advanced replacers
Sometimes you need to create some advanced replace logic, so Parser repalce functions don't handle it. KotlinSpirit provides Replacer. It has similar functionality to regular expressions replacements with groups. To describe the functionality of Replacer let's discuss an example: We want to replace a string containing a list of Name LastName, followed by a list of integers, separated by ','. We want to repalce Name and LastName with initials and multiply all the integers twice. Let's create Repalcer for it.
```Kotlin
val replacer = Replacer {
    val nameRange = range()
    val lastNameRange = range()
    val intsResult = rangeResultList<Int>()

    val name = char('A'..'Z') + +char('a'..'z')
    val nameAndLastName = name.getRange(nameRange) + ' ' + name.getRange(lastNameRange)

    val ints = int.getRangeResult {
        intsResult.add(it)
    } % ','

    fun replaceName(name: CharSequence): CharSequence {
        return name[0].toString() + '.'
    }

    Replace(
        rule = nameAndLastName + ' ' + ints
    ) {
        replace(nameRange, ::replaceName)
        replace(lastNameRange, ::replaceName)
        replace(intsResult) {
            it * 2
        }
    }
}
```
Let's test it
```Kotlin
Assert.assertEquals(
    "I. A. 2,4,-10,12 Urvan Arven 12,12,323,3",
    replacer.replaceFirst("Ivan Abdulan 1,2,-5,6 Urvan Arven 12,12,323,3").toString()
)

Assert.assertEquals(
    "I. A. 2,4,-10,12 U. A. 24,24,646,6",
    replacer.replaceAll("Ivan Abdulan 1,2,-5,6 Urvan Arven 12,12,323,3").toString()
)

Assert.assertEquals(
    "45Ivan Abdulan 1,2,-5,6 Urvan Arven 12,12,323,3",
    replacer.replaceIfMatch(0, "45Ivan Abdulan 1,2,-5,6 Urvan Arven 12,12,323,3").toString(),
)

Assert.assertEquals(
    "I. A. 2,4,-10,12 Urvan Arven 12,12,323,3",
    replacer.replaceIfMatch(0, "Ivan Abdulan 1,2,-5,6 Urvan Arven 12,12,323,3").toString(),
)
```
In the example Repalcer takes Replace builder as an argument. The builder returns Replace object, containing the rule and another builder as the second argument, where we discribe how the replacement process goes.

# Thread safety
A compiled parser is completely thread-safe, you can access it from different threads at the same time. And it's lock-free as well. However if you use callbacks or getRange or getRangeResult in your rule, there are some edge cases you should consider. Callbacks and getRange hooks are not synchronized, so they might be called from different threads, when you call your parser functions from different threads. But it's totally safe to use callbacks or getRange or getRangeResult in Grammar or Replacer, because a copy of your Grammar/Replace is created for each thread.

# Debugging
KotlinSpirit has a debugging engine included, which makes it easier to debug errors in your parser. To enable debugging you need to create a parser using  `rule.compile(debug = true)`.

For example: `val debugIntParser = int.compile(debug = true)`

After parsing is finished you can get a tree of your parsing process using `parser.getDebugTree()`. The tree is convertible to json string using `toString` method.

### Debug names
KotlinSpirit assigns readable names for all the rules by default. However, if you want to specify your own custom name you can do it by applying `name(name: String)` method to intermediate rules. If you use repalce or search functions you may get multiple trees during the process. Call `getDebugHistory()` to get trees of all the parsing attempts.

Let's come back to our first example and make it debuggable.
```Kotlin
val name = char('A'..'Z') + +char('a'..'z')
val age = int
val parser = (name.name("name") + '=' + age.name("age")).compile(debug = true)
```

### Debug performance
Making your parser debuggable affects performance and after you finish testing and debugging your rules you need to remove `debug = true`.
