# KotlinSpirit

Lighweight library for creating parsers, inspired by C++ boost spirit library.

## Introduction
There are no good libraries or frameworks to parse text easily in Kotlin. Yeah, we have regular expressions. But they are hard to debug, hard to read, don't support 
recursive expressions and they perform poor. One time I got StackOverflow error easily while parsing a large text. So the idea was to create a simple library with compile-time 
expressions checking. KotlinSpirit does the job. It outperforms regular expressions in many cases.

## Creating a simple parser
KotlinSpirit consists of basic rules and operators. All the rules are defined in Rules object namespace. In all the examples below we consider, that the rules are already imported from Rules object.



