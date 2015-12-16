CWAC RichTextUtils: Making Rich Text Cheaper
============================================

**THE CLASSES FROM THIS PROJECT HAVE BEEN MIGRATED TO THE [CWAC-RichEdit](https://github.com/commonsguy/cwac-richedit)
LIBRARY AND WILL BE SUPPORTED THERE. THIS REPOSITORY IS HERE PURELY FOR HISTORICAL PURPOSES.**

Android's `TextView` and subclasses support `Spanned`
objects that blend in formatting rules along with text.
This library provides some utilities for working with
`Spanned` objects.

In particular, it has code to convert a `Spanned` to
and from XHTML, as an alternative to the `toHtml()`
and `fromHtml()` methods on Android's `Html` class.

In addition to the documentation on this page,
[partial JavaDocs are also available](http://javadocs.commonsware.com/cwac/richtextutils/index.html).

This Android library project is available as an artifact for use
with Gradle. To use that, add the following
blocks to your `build.gradle` file:

```groovy
repositories {
    maven {
        url "https://repo.commonsware.com.s3.amazonaws.com"
    }
}

dependencies {
    compile 'com.commonsware.cwac:richtextutils:0.1.+'
}
```

Or, if you cannot use SSL, use `http://repo.commonsware.com` for the repository
URL.

If you are not using Gradle, JARs are available in the "Releases" area
of the GitHub repo.

XHTML Conversion
----------------
The principle set of utilities for this library is to convert
`Spanned` objects to/from XHTML.

### Scope of Support

The primary objective of this conversion logic is to support the
formatting offered by [the `RichEditText` widget](https://github.com/commonsguy/cwac-richedit).
Apps that wish to allow users to enter in rich text can use
`RichEditText`, then persist the `Spanned` using this library. Later
on, if the user wants to edit rich text entered previously, the app
can convert the XHTML back into a `Spanned` to supply to `RichEditText`.

A secondary objective is to allow the resulting persisted value to be
usable by anything that needs an XHTML representation of rich text.
For example, you might supply the XHTML to a Web service, or upload
it to a Web site. That's why XHTML is chosen as the representation format,
as opposed to some sort of `Serializable` or other binary packaging.

Whereas `Html.fromHtml()` is designed to parse semi-arbitrary HTML,
this library is not. You are welcome to feed it XHTML from wherever and
see if it works. As the saying goes, YMMV.

### Basic Parsing and Generating

Given a `Spanned` (e.g., `getText()` on a `RichEditText`), to get an
XHTML representation of the `Spanned`, create an instance of
`SpannedXhtmlGenerator` and call `toXhtml()` on it. This will return
a `String` of XHTML.

Later on, to get the `Spanned` back from that XHTML, create an instance
of a `SpannableStringGenerator` and call `fromXhtml()` on it, passing
it the `String` of XHTML, and getting back a a `Spannable` that you can
use with `RichEditText` or whatever.

And that's pretty much it.

### Conversion Rules

A stock set of rules, embodied in a collection of `SpanTagHandler`
instances, are applied to convert the `Spanned` to XHTML and back again:

| `CharacterStyle`      | XHTML Tag Structure                     |
| --------------------- | --------------------------------------- |
| `AbsoluteSizeSpan`    | `<span style="font-size: ...px;">`      |
| `BackgroundColorSpan` | `<span style="background-color: #...">` |
| `ForegroundColorSpan` | `<font color="...">`                    |
| `RelativeSizeSpan`    | `<span style="font-size: ...%;">`       |
| `StrikethroughSpan`   | `<strike>`                              |
| `StyleSpan`           | `<b>` or `<i>`                          |
| `SubscriptSpan`       | `<sub>`                                 |
| `SuperscriptSpan`     | `<sup>`                                 |
| `TypefaceSpan`        | `<span style="font-family:...;">`       |
| `UnderlineSpan`       | `<u>`                                   |
| `URLSpan`             | `<a href="...">`                        |

### Customizing the Conversion

If there are new `CharacterStyle` subclasses that you want to support,
and you want to do so on a global (process-level) basis, create
a subclass of `SpanTagHandler` and register it via
`registerGlobalSpanTagHandler()` on the `SpanTagHandler` class.

If you want to override the default rules, create a subclass
(or subclasses) of `SpanTagHandler` for those rules. Then, create
an instance of `SpanTagRoster` and register your handlers via
`registerSpanTagHandler()` on the roster. You can pass in your
roster to the constructor of `SpannedXhtmlGenerator` or
`SpannableStringGenerator`.

There are a bunch of implemenations of `SpanTagHandler`, for the
stock rules, in the `com.commonsware.cwac.richtextutils.handler`
package, so you can see what creating these looks like.
There is also a `ClassSpanTagHandler` that you can use to
use a `<span class="...">` tag for a particular `CharacterStyle`, if
you want to use CSS classes for the actual formatting rules.

Note, though, that if you customize the rules by any of these
mechanisms, it is incumbent upon you to *keep* those customizations.
If you generate XHTML using one set of rules, you need to use
the same (or a compatible) set of rules to restore the `Spanned`.

Known Limitations
-----------------
- Two start tags in sequence may be flipped in order during conversion.
So, for example, suppose you had `<b><i>Foo</i></b>`, and you converted
that into a `Spanned`, then back into XHTML. The resulting XHTML could
be the same or could be `<i><b>Foo</b></i>`.

- It is possible that multiple `<span>` elements will be applied for the
same text (e.g., it is adjusted using a `RelativeSizeSpan` and a
`BackgroundColorSpan`). No attempt is made to coalesce those `<span>`
elements into one, even though from an XHTML standpoint, this is certainly
possible (and perhaps even desired).

- The XHTML generated by this library is unofficial until the library
reaches 1.0. At that point, the XHTML specification will remain fixed
through point-level releases (e.g., 1.1) until the next major release
(e.g., 2.0). Hence, until the library reaches 1.0, and for major
releases after that, you may need to go through some cleanup logic, as
your XHTML may not be parsed the same way as it had been in earlier
versions of the library.

Dependencies
------------
There are no third-party dependencies at this time for the library.

Version
-------
This is version v0.1.0 of this module, meaning it is brand new.

Demo
----
In the `demo/` sub-project you will find
a sample activity that demonstrates the use of the `Spanned`/XHTML
conversion logic.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
**THIS PROJECT IS DISCONTINUED**

Release Notes
-------------
- v0.1.0: initial release

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

