# Ingredient Extension API

This API provides the framework and necessary patches for creating custom Ingredient implementations.

## Modder Information

This API allows modders to create new Ingredient Serializers. An Ingredient serializer is responsible for reading and
writing a given Ingredient to and from JSON and network buffers. Ingredient Serializers can choose what types of
Ingredient they manage and can even produce non-vanilla Ingredient implementations.

### Creating A Serializer

An Ingredient serializer is any instance of a class that implements the IIngredientSerializer interface. These instances
must be registered in the registry which can be done using `IngredientHelper.register(id, serializer);`. 

// TODO add examples

### Maven Information

Every push to this repository is built and published to the [BlameJared](https://maven.blamejared.com) maven, to use these builds in your project, simply add the following code in your build.gradle

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    modCompileOnly("com.faux.ingredientextension:IngredientExtensionAPI-fabric-1.18.2:[VERSION]")
}
```

Just replace `[VERSION]` with the latest released version, which is currently:

[![Maven](https://img.shields.io/maven-metadata/v?color=C71A36&label=&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Ffaux%2Fingredientextension%2FIngredientExtensionAPI-fabric-1.18.2%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/faux/ingredientextension/)

Simply remove the `v` and use that version, so `v1.0.0.0` becomes `1.0.0.0`

## User Information

This API allows mods to create new types of Ingredients that you may use in Data Packs and similar features. Modded
ingredients can simplify complex logic, improve compatibility with other mods, and handle item matching completely
different to vanilla. A common example of this would be an Ingredient type that only accepts items with specific NBT
tags. Another example would be an Ingredient that only accepts furnace fuels that have a specific burn time range.

### JSON & Data Packs

Modded Ingredient types are easy to use with existing JSON based features like Data Packs. For example, modded
Ingredient types can be seamlessly used in new crafting recipes or advancement criterion. To use a modded Ingredient
type you must define a type property in the JSON object that has the value of the registry ID of the Ingredient type you
want to use. Mods adding new Ingredient types should include this ID in their documentation.

```json
{
  "type": "examplemod:some_new_ingredient"
}
```

Modded Ingredient types will often require additional properties to be defined in order. For example a hypothetical
Ingredient type that only accepts items with a furnace burn time within a given range will require that burn time to be
specified. These additional properties should be included with the documentation of the mod you are trying to use.

```json
{
  "type": "some_example_mod:burn_time",
  "burnTime": {
    "min": 200,
    "max": 450
  }
}
```

#### Handling Conflicting Mods

In some rare circumstances another mod may attempt to implement their own feature that uses the `type` property in a way
that is completely unrelated to this mod. These features should continue to work as expected however we will log a
warning that looks like `Tried to read an ingredient of unknown type XYZ`. This warning message is intended to help
users know when they have a typo in the Ingredient type ID or the mod that adds the Ingredient is not properly
installed. In a conflicting scenario these warnings are likely irrelevant and can be disabled by setting
the `ignoreIngredientExtensionAPI` property to true.

```json
{
  "type": "unrelated mod feature",
  "somethingElse": "x + 2 = 41",
  "ignoreIngredientExtensionAPI": true
}
```

### Multiplayer & Networking

Modded Ingredients have full support for Multiplayer and can be sent to clients by the server for things like new
recipes and advancements. All players connecting to a server should have this mod installed, and the mods that add the
new Ingredient types. This is required to ensure a stable connection and avoid unexpected errors. The players do not
need new server Data Pack files like new recipe JSONs.

### New Ingredient Types

// TODO We will add a list of new Ingredient types here soon.