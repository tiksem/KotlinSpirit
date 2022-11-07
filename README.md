# KotlinSpirit

Lighweight library for creating parsers, inspired by C++ boost spirit library.

# Introduction
There are no good libraries or frameworks to parse text easily in Kotlin. Yeah, we have regular expressions. But they are hard to debug, hard to read, don't support 
recursive expressions and they perform poor. One time I got StackOverflow error easily while parsing a large text. So the idea was to create a simple library with compile-time 
expressions checking. KotlinSpirit does the job. It outperforms regular expressions in many cases.

# Creating a simple parser
KotlinSpirit consists of basic rules and operators. All the rules are defined in Rules object namespace. In all the examples below we consider, that the rules are already imported from Rules object.

Let's create a simple parser for key-value pair, where key is name and value is age, as our first example

```
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
int, long, float, double
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
```
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
```
oneOf(vararg strings: CharSequence)
```
Example: `oneOf("Jhon", "Ivan", "Bin")` matches one of the names.

You can also use operator `or` for creating `oneOf` rule. So the example above could be written as:
```
str("Jhon") or "Ivan" or "Bin"
```
We will discuss `or` operator later.

# Operators

Operators help us to create complex rules from basic rules.

## Sequence rule
The resultType of the rule is `CharSequence`. We will discuss how to retrieve parsing results later.

`val sequence = a + b` matches `a` followed by `b`.
As an example let's consider we want to create a math expression with 2 integers and an operator.
```
val exp = int + char('+', '-', '/', '*') + int
```
## Or rule
The resultType of the rule depends on the resultTypes of `a` and `b`. If the resultTypes are the same, the resultType of `or` will be the resultType of `a` and `b`. Otherwise it will be Any or something less generic in some cases.

`val exp = a + b` matches `a` or `b`

As an example let's consider we want to create a parser, which parses an input, where a user can specify his username or an identification number.
```
val id = long or (char('a'..'z') + +char('a'..'z', '0'..'9'))
```
## Difference rule
The resultType of the rule is the same as the resultType of `a`.

`val exp = a - b` matches `a` only when it doesn't match `b` at the same time.

As an example let's create Kotlin comment parser
```
val comment = str("/*") + (char - "*/").repeat() + "*/"
```
It starts with `/*`, then it eats every character, until it finds `*/` and then closes it with `*/` at the end. Repeat matches 0 or more times of the give expression. We will discuss it later.

## Not rule
The resultType of the rule is Char

`val exp = !a` Matches one character, if it doesn't match rule a. If there is no such a character, it may happen only on the end of input, it outputs '\0' as a result

This rule is similar to some cases of the difference rule. For example `char - 'a'` is similar to `!char('a')`. The difference is only at the end of input.
`!char('a')` matches the end of input, cause eof is catually not 'a', but `char - 'a'` doesn't match eof.

## Repeat rules
The resultType of repeat rules might be different, depending on the repeated rule kind. If the repeated rule is `Char` rule the result is `CharSequence` in all other cases it is `List<T>`, where `T` is the resultType of repeared rule.

Repeat rule is specifed by:

`repeated.repeat()` Repeat 0 or more times

`repeated.repeat(m..n)` Repeat from m to n times

`+repeated` Repeat 1 or more times

Char repeat example:
```
val name = char('A'..'Z') + char('a'..'z').repeat(1..19)
```
Matches any name with 2 - 20 length.

Lets repeat the names
```
val names = +names
```
matches a list of names, at least one name should be specified. 

For example: `names.compile().parseGetResultOrThrow("HelloWorldYo")` will return list `["Hello", "World", "Yo"]`

## Optional rule
The resultType of optional rule is T?, where T is is the result of `a`
`val optional = -a` This rule is always succesful. It matches `a` if possible, if not it just outputs null.

Warning: If your root parser rule is OptionalRule `parseGetResultOrThrow` will always throw an exception. This issue is going to be fixed in next versions of KotlinSpirit.

## Quoted rule
Quoted rule represents rule `a` quoted by `left` and `right` rules. If you specify only a single rule as an argument of `quoted` `left` and `right` will be the same. You may ask what is the difference between sequence rule `left + a + right` and `a.quoted(left, right)`. The difference is the result. The resultType of sequence rule is always CharSequence from the beginning to the end of the rule, so quoted are included into the result as well. However the quouted rule result is the same as `a` result.

Let's consider we want to implement a quoted string:
```
val quotedStr = (char - '"').quoted('"').compile()
val result = quotedStr.parseGetResultOrThrow("\"Hello, world!\"")
```
In the example `result` will be `Hello, world!`. But not `\"Hello world!\"`

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
