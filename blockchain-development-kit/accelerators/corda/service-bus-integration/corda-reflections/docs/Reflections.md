# Reflections
[Index](Index.md)

The Kotlin and Java reflections libraries are used to dynamically create the Corda Flows from
data supplied via the REST API. The mapping rules are detailed below. For now only the Kotlin 
rules are implemented and documented. Flows written in Java CANNOT be called at present. 

The most simple JSON parser (`org.json`) is used internally. This will emit only basic Java types (String, Boolean, 
Integer, Long, Double, List and Map). Any necessary type mappings and coercions are implemented 
within the reflections layer.

## Basic Form 

All forms match by parameter name not ordinal position (_the reason why Java reflections 
is not supported at present is that there is no native support for passing by name in the Java 
reflections API_). 

### Simple Params 

```kotlin
class OneParam(val name : String)
```

Simply needs a map with a single entry 

```json
{ "name" : "Alice"} 
```

### Default Values 

```kotlin
class TwoParams(val name : String, val age : Int = 21)
```

Will apply the default if not supplied  

```json
{ "name" : "Alice"} 
```

or 

```json
{ "name" : "Bob", "age" : 30} 
```

### Numeric Type Conversion 

```kotlin
class NumericParams (val intParam : Int, val longParam : Long, val floatParam : Float ,val doubleParam : Double)
```

Will automatically coerce Long <-> Int and Float <-> Double and anything -> BigDecimal if possible, so

```json
{ "intParam" : 1, "longParam": 123, "floatParam" : 1.0, "doubleParam" : 9.9} 
```

is allowed, but 

```json
{ "intParam" :  2147483648, "longParam": 123, "floatParam" : 1.0, "doubleParam" : 9.9}
```

Will fail as the value is out of range for an Int.

_note that these rely upon the inbuilt type conversions within org.json and Kotlin.  
Rounding problems may occur, please test for your scenarios. Consider 
writing a custom resolver or encoding as a String and defer 
conversion rules to the application code_ 

### Nested Objects 

```kotlin
class NestedParams(val one: OneParam, val two : TwoParams, val extra : String)
```

Needs a nested map  

```json
{ "one" : {"name" : "Alice"}, 
  "two" : { "name" : "Bob", "age" : 30}, 
  "extra" : "hello world"}  
```

### Enums 

```kotlin
enum class Colours { Red, Green, Blue }
class EnumParam(val colour: Colours)
```

Needs a string that matches an enum value exactly

```json
{ "colour" : "Red"} 
```

### Scalar Types 

It is common to use scalar types that have an agreed canonical String format. UUID is a good example. In 
these case the mapping will look for either:

* a static factory method taking a single String.
* a constructor taking a single String

```kotlin
class ScalarClassParam (val id : UUID)
```

```json
{ "id" : "3ea99e8d-afab-4053-b4dc-c4437836ef03" }
```

### Using a Resolver 

For types that don't fit into any of the these forms a custom [Resolver](Resolvers.md) can be used. 
