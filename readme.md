
### Enohar

Enohar is a library to serialize and deserialize Eno documents. It uses Java v8 syntax, but is published with and without a v9 module descriptor. (currently *Incomplete*)

&copy; Nicholas Prado. Begun 2018. Released under MIT license terms.

'Eno' is a document [file format](https://github.com/eno-lang/eno) / data structure specification. 'enolib' is the canon collection of libraries for parsing eno documents, in various languages. Enohar parses a valid eno document in compliance with the eno spec. It even imitates much of the api that enolib exposes. However, Enohar does not aspire become an enolib sublibrary and will ignore some functionality, for the forseeable future, and provide some functionality that that enolib lacks.

#### Inclusion in your project

When Enohar parses an eno document compliantly, I will upload it to sonatyp/maven central. At that point, it will be available with the following dependency descriptors, maven and gradle. The version elements are deliberately invalid, so a caller can choose the jar appropriate to whether that runtime understands a module descriptor. (j8 is java v8 and j11 is java v11). Otherwise, Enohar intends to follow [compatibility versioning](https://gitlab.com/staltz/comver).

##### Maven pom dependence declaration

```xml
	<groupId>ws.nzen.format.eno</groupId>
	<artifactId>enohar</artifactId>
	<version>
		1.0.j8
		1.0.j11
		</version>
```

##### Gradle build dependence declaration

```
	'ws.nzen.format.eno:enohar:
		1.0.j8
		1.0.j11'
```


Enohar relies on locale files prepared by the note_enojes library from the canon enolib locale json. For the time being, these are included in the Enohar project, until they've been published to sonatype/maven central.

#### Progress

* lex some eno
* parse some eno
* expose elements
* semantic analysis
* emit eno document
* >use canon localization
* >use templates
* >wrap missing-element api
* ~recognize empty-element~
* ~parse incrementally~
* ~html,terminal-color logging~
* ~recognize some types ('loaders')~
* ~schema validator?~
* ~enable use tracking ('touch')~

#### Name

"Enojar" is a portuguese-origin, infinitive verb meaning _to anger_ . That word is [pronounced](https://en.wiktionary.org/wiki/enojar#Pronunciation) the way this is spelt, in US english pronounciation.


































