# KotlinSpirit

Lightweight library for creating parsers, inspired by C++ boost spirit library.

# Introduction
There are no good libraries or frameworks to parse text easily in Kotlin. Yeah, we have regular expressions. But they are hard to debug, hard to read, don't support 
recursive expressions and they perform poorly. One time I got StackOverflow error easily while parsing a large text. So the idea was to create a simple library with compile-time 
expressions checking.

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
implementation "com.github.tiksem:KotlinSpirit:1.0.1"
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
int, long, float, double, uint, ulong
```
## Char rules
`char` represents any single chcracter

`char(vararg ch: Char)` Represents any character from the characters list. For example: `val operators = char('+', '-', '*', '/')`

`char(vararg range: CharRange)` Represents characters from the given ranges. For example: `val letter = char('a'..'z', 'A'..'Z')`

`char(chars: CharArray, ranges: Array<CharRange>)` Mix of chars list and ranges

`charIf(predicate: (Char) -> Boolean)` Char with a custom matching predicate
## String rules
`str(string: String)` Matches an exact string. For example `str("Sun")`

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
Matches one of the strings from a given list. The search is optimized by using `TernarySearchTree` for matching strings. 
```Kotlin
oneOf(vararg strings: CharSequence)
```
Example: `oneOf("Jhon", "Ivan", "Bin")` matches one of the names.

You can also use operator `or` for creating `oneOf` rule. So the example above could be written as:
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
The resultType of repeat rules might be different, depending on the repeated rule kind. If the repeated rule is `Char` rule the result is `CharSequence` in all other cases it is `List<T>`, where `T` is the resultType of repeared rule.

Repeat rule is specified by:

`repeated.repeat()` Repeat 0 or more times

`repeated.repeat(m..n)` Repeat from m to n times

`+repeated` Repeat 1 or more times

Char repeat example:
```Kotlin
val name = char('A'..'Z') + char('a'..'z').repeat(1..19)
```
Matches any name with 2 - 20 length.

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
`nameButNotJhon` above is the same as `name - "Jhon"` if we use the difference rule instead

## Quoted rule
Quoted rule represents rule `a` quoted by `left` and `right` rules. If you specify only a single rule as an argument of `quoted` `left` and `right` will be the same. You may ask what is the difference between sequence rule `left + a + right` and `a.quoted(left, right)`. The difference is the result. The resultType of sequence rule is always CharSequence from the beginning to the end of the rule, so quotes are included in the result as well. However the quoted rule result is the same as `a` result.

Let's consider we want to implement quoted string parser:
```Kotlin
val quotedStr = (char - '"').quoted('"').compile()
val result = quotedStr.parseGetResultOrThrow("\"Hello, world!\"")
```
In the example `result` will be `Hello, world!`. But not `"Hello world!"`

## Expectation rule
The resultType of the rule is the same is the resultType of rule `a`

`val exp = a.expect(b)` Matches rule `a`, only if rule `b` matches after rule `a`

The rule is similar to `a + b`. However it has the resultType of rule `a` and it sets the seek to the end of `a` after parsing is finished.

Let's consider we want to parse `int` followed by a string with 3 characters and this int cannot be decimal
```Kotlin
val exp = int.expect(!char('.')) + char.repeat(3..3)
val parser = exp.compile()
parser.matches("123abc") // true
parser.matches("123.bc") // false
parser.matches("123.34") // false
```

# Parser functions
Each rule contains its result after parsing, when you parse without a result, just for matching, the runtime performance will be a little bit better, but the difference is usually not noticeable.

## Matching functions

Returns true if the string matches the rule from the beginning to the end.
```
fun matches(string: CharSequence): Boolean
```
Checks if the string matches the rule from the beginning to the end. If no, throws ParseException.
```
fun matchOrThrow(string: CharSequence)
```
Returns true if the string matches the rule from the beginning only.
```
fun matchesAtBeginning(string: CharSequence): Boolean
```

## Parsing functions

Parses and gets the result, if the rule doesn't match throws ParseException
```
fun parseGetResultOrThrow(string: CharSequence): T
```
Parses without any result returning the ending seek, if the rule doesn't match throws ParseException.
```
fun parseOrThrow(string: CharSequence): Int
```
Parses without any result, returns ending seek if rule matches and null otherwise.
```
fun tryParse(string: CharSequence): Int?
```
Parses with returning ParseResult. it contains the result or errorCode if the rule doesn't match
```
fun parseWithResult(string: CharSequence): ParseResult<T>
```
Parses without a result returning ParseSeekResult. ParseSeekResult contains ending seek and errorCode.
```
fun parse(string: CharSequence): ParseSeekResult
```

## Searching functions
Unlike parsing functions, searching functions try to find the result from the beginning to the end, moving a seek. But parsing functions fail immidiatly if the result doesn't match at the beginning.

Retrurns an index of the first match or null if the match is not found.
```
fun indexOf(string: CharSequence): Int?
```
Retrurns a result and a range of the first match or null if the match is not found.
```
fun findFirstResult(string: CharSequence): ParseRangeResult<T>?
```
Retrurns a result of the first match or null if the match is not found.
```
fun findFirst(string: CharSequence): T?
```
Retrurns a range of the first match or null if the match is not found.
```
fun findFirstRange(string: CharSequence): ParseRange?
```
Retrurns results of all matches.
```
fun findAll(string: CharSequence): List<T>
```
Retrurns results and ranges of all matches.
```
fun findAllResults(string: CharSequence): List<ParseRangeResult<T>>
```

## Replace functions

Replaces the first match
```
fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence
```
Replaces all matches
```
fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence
```
Replaces the first match using a custom replacementProvider, it takes the rule's result as its argument.
```
fun replaceFirst(source: CharSequence, replacementProvider: (T) -> Any): CharSequence
```
Replaces matches using a custom replacementProvider, it takes the rule's result as its argument.
```
fun replaceAll(source: CharSequence, replacementProvider: (T) -> Any): CharSequence
```
Replaces the first match. Returns null if the match was not found
```
fun replaceFirstOrNull(source: CharSequence, replacement: CharSequence): CharSequence?
```
Replaces the first match using a custom replacementProvider. Returns null if the match was not found
```
fun replaceFirstOrNull(source: CharSequence, replacementProvider: (T) -> CharSequence): CharSequence?
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
Sometimes you need to get a range of your rule's match. You can use getRange and getRangeResult hooks for that. Warning: getRange and getRangeResult are not properly synchronized for multithreading usage. So you need to use them inside Grammar or Replacer. See Thread safety section for reference. 

getRange accepts ParseRange as a parameter. ParseRange is filled with startSeek and endSeek during parsing, when the rule is succesful. You can create a range using `range()` function. The example shows how to find int in a string and get its range
```
val range = range()
val r = (!int).repeat() + int.getRange(range) + (!int).repeat()
r.compile.match("yoyoyo322323yoyoyo")
```
getRangeResult is the same as getRange, but it also parses a result, so it takes ParseRangeResult. To create ParseRangeResult use `rangeResult()` function.

```Kotlin
var name = ""
var age = -1
val nameRule = char('A'..'Z') + +char('a'..'z')
val rule = nameRule { name = it } + '=' + int { age = it }
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

# Thread safety
A compiled parser is completely thread-safe, you can access it from different threads at the same time. And it's lock-free as well. However if you use callbacks or getRange or getRangeResult in your rule, there are some edge cases you should consider. Callbacks and getRange hooks are not synchronized, so they might be called from different threads, when you call your parser functions from different threads. But it's totally safe to use callbacks or getRange or getRangeResult in Grammar or Replacer, because a copy of your Grammar/Replace is created for each thread.

# Debugging
KotlinSpirit has a debugging engine included, which makes it easier to debug errors in your parser. To enable debugging you need to call `debug()` on your root rule.

For example: `val debugIntRule = int.debug()`

After parsing is finished you can get a tree of your parsing process using `parser.getDebugTree()`. The tree is passed to `ParseException` as well. The tree is also convertible to json string using `toString` method.

### Debug names
KotlinSpirit assigns readable names for all the rules by default. However, if you want to specify your own custom name you can do it by applying `debug(name: String)` method to intermediate rules. If you use repalce or search functions you may get multiple trees during the process. Call `getDebugHistory()` to get trees of all the parsing attempts.

Let's come back to our first example and make it debuggable.
```Kotlin
val name = char('A'..'Z') + +char('a'..'z')
val age = int
val r = (name.debug("name") + '=' + age.debug("age")).debug()
```
Don't forget to make your root rule debuggable by calling `.debug()`

### Debug performance
Adding `debug()` to your rules affects performance and after you finish testing and debugging your rules you need to remove `debug()` calls.
