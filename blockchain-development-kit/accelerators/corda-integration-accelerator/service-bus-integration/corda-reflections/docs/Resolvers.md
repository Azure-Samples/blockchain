# Resolvers 
[Index](Index.md)

These allow any custom type to be resolved via reflections. Reason for using a resolver include:
* there isn't enough information for the reflections layer to work
* additional processing is required, for example looking up full Party details from an org name. 

## Writing a resolver 
Resolvers simply implement the [Resolver](../src/main/kotlin/net/corda/reflections/resolvers/Resolver.kt)
interface. 

For now they must be manually registered (see source code).

## Built in resolvers 

### PartyResolver 

This supports the lookup of a Party by its org name. It assumes that:
* org names are case insensitive 
* org names are unique across the network

```kotlin
class PartyParam (val party : Party)
```

```json
{ "party" : "Alice" }
```

Future enhancements might allow better qualification by passing other components of the certificate name,
e.g. 


```json
{ "party" : "O=Alice,L=London,C=GB" }
```

or possibly even 

```json
{ "party" : {"organisation" : "Alice", 
             "location" : "London" ,
             "country" : "GB" }}
```              

### UniqueIdentifier resolver 

This allows both forms of UniqueIdentifier (with and without an external id) to be used


```kotlin
class UniqueIdentifierParam (val id : UniqueIdentifier)
```


```json
{ "id" : "42E13F55-75C5-4B17-90A9-2B2A66B853ED" }
```

or 

```json
{ "id" : "extid123_42E13F55-75C5-4B17-90A9-2B2A66B853ED" }
```