# KotlinSpirit

Lighweight library for creating parsers, inspired by C++ boost spirit library.

# Introduction
There are no good libraries or frameworks to parse text easily in Kotlin. Yeah, we have regular expressions. But they are hard to debug, hard to read, don't support 
recursive expressions and they perform poor. One time I got StackOverflow error easily while parsing a large text. So the idea was to create a simple library with compile-time 
expressions checking. KotlinSpirit does the job. It outperforms regular expressions in many cases.

# Creating a simple parser
KotlinSpirit consists of basic rules and operators. All the rules are defined in Rules object namespace. In all the examples below we consider, that the rules are already imported from Rules object.

Let's create a simple parser for key-value pair, where key is name and value is age, as our first example

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

`char(vararg range: CharRange)` Represents chcracters from the given ranges. For example: `val letter = char('a'..'z', 'A'..'Z')`

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

In our example above you may notice, that name contains `+char('a'..'z')`, that is basically the same as `nonEmptyStr('a'..'z')`.

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

`val exp = a + b` matches `a` or `b`

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

Repeat rule is specifed by:

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
val result = numbers.parseGetResultOrThrow("12,16,76,1233,-5")
```
The result will be [12,16,76,1233,-5]
  
## Optional rule
The resultType of optional rule is T?, where T is is the result of `a`
`val optional = -a` This rule is always succesful. It matches `a` if possible, if not it just outputs null.

Warning: If your root parser rule is OptionalRule `parseGetResultOrThrow` will always throw an exception. This issue is going to be fixed in next versions of KotlinSpirit.

## FailIf rule
The resultType of the rule is the same as the resultType of `a` rule

`a.failIf(predicate: (T) -> Boolean)` Checks the result of `a` and fails if the passed predicate returns true.

Let's consider we want to create a name parser, where name is not Jhon.
```Kotlin
val name = char('A'..'Z') + +char('a'..'z')
val nameButNotJhon = name.failIf { it == "Jhon" }
```
`nameButNotJhon` above is the same as `name - "Jhon"` if we use the difference rule instead

## Quoted rule
Quoted rule represents rule `a` quoted by `left` and `right` rules. If you specify only a single rule as an argument of `quoted` `left` and `right` will be the same. You may ask what is the difference between sequence rule `left + a + right` and `a.quoted(left, right)`. The difference is the result. The resultType of sequence rule is always CharSequence from the beginning to the end of the rule, so quotes are included into the result as well. However the quouted rule result is the same as `a` result.

Let's consider we want to implement quoted string parser:
```Kotlin
val quotedStr = (char - '"').quoted('"').compile()
val result = quotedStr.parseGetResultOrThrow("\"Hello, world!\"")
```
In the example `result` will be `Hello, world!`. But not `"Hello world!"`

# Parser functions, and getting a result
Each rule contains its result after parsing, when you parse without a result, just for matching, the runtime performance will be a little bit better, but the difference is usually not noticable.

`fun parseGetResultOrThrow(string: CharSequence): T` Parses and gets the result, if rule doesn't match throws ParseException

`fun parseOrThrow(string: CharSequence): Int` Parses without any result returning the ending seek, if rule doesn't match throws ParseException.

`fun tryParse(string: CharSequence): Int?` Parses without any result, returns ending seek if rule matches and null otherwise.

`fun parseWithResult(string: CharSequence): ParseResult<T>` Parses with returning ParseResult. it contains the result or errorCode if rule doesn't match

`fun parse(string: CharSequence): ParseSeekResult` Parses without a result returning ParseSeekResult. ParseSeekResult contains ending seek and errorCode.

`fun matches(string: CharSequence): Boolean` Returns true if the string metches the rule from the beginning to the end.

`fun matchOrThrow(string: CharSequence)` Checks if the string matches the rule from the beginning to the end. If no, throws ParseException.

`fun matchesAtBeginning(string: CharSequence): Boolean` Returns true if the string metches the rule from the beginning only.

# Recursive expressions
Let's consider that there is a case: rule `a` could point to rule `b` and rule `b` could point to rule `a`. Or even rule `a` points to rule `a`. So we get a recurssion here.

Let's discuss a real-time example. We want to parse a mathematic expression like 5 + (34 + 48).
```Kotlin
val operator = char('+', '-', '*', '/')
val value = expressionInBrackets or double
val expression = value + operator + value
val expressionInBrackets = expression.quoted('(', ')')
```
Looks clear. However if you try to run it you will get StackOverflow error. Let's see how we can solve it.

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
Each rule can have a custom callback specified, this callback is called when the rule is succesful. Let's come back to our first example where we parsed a key-value pair of name and age. And specify callbacks to retrieve the results.
```Kotlin
var name = ""
var age = -1
val nameRule = char('A'..'Z') + +char('a'..'z')
val rule = nameRule { name = it } + '=' + int { age = it }
```

## Grammars
Grammars are used to create rules with custom results. They are computed at rumtime, so can be used instead of lazy rules.

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
