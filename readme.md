
## Enohar

Enohar is a library to serialize and deserialize Eno documents. It uses Java v7 syntax, but is published with and without a v9 module descriptor. Its tests, however use java v8 lambda syntax. (currently *Incomplete*)

&copy; Nicholas Prado. Begun 2018. Released under _MIT_ license terms.

#### Contents

* [Sample eno](#Sample eno)
* [Inclusion](#Inclusion in your project)
* [Progress](#Progress)
* [Sample usage](#Sample usage)
* [Name](#Name)
* [Contribution](#Contribution)

_Eno_ is a document [file format](https://github.com/eno-lang/eno) / data structure specification. [Enolib](https://github.com/eno-lang/enolib) is the canon collection of libraries for parsing eno documents, in various languages (js,py,rb,php,rs).  Enohar is an implementation in Java, as enolib, contemporarily, has no jvm implementation. Eno intends to be a human read/write -able format with the ability to be more complex than a properties file, but without the data type inference of json. All values are text, so this moves the burden / freedom to application implementors using an eno parsing library as a dependency.

Enohar parses a valid eno document in compliance with the eno spec. It imitates much of the api that enolib exposes. However, Enohar will not provide some enolib behavior for the forseeable future (ex, tracking whether elements have been used). It provides some functionality that that enolib implementations lack (ex, emitting an eno document as text).

### Sample Eno
```
> comment
field with no content :

field with : immediately following content

field with : line
	\ wrapped content, all lines and element names are trimmed
	> this indentation is just for readability

-- multiline block delimiters must match
This is the means that eno provides for
a) multiline content
b) preserving the untrimmed formatting of that content
-- multiline block delimiters must match

# section containing some elements
	` # field name that would otherwise be a section` :
		|
		| the backticks suppress interpretation inside the key name

	` # field name that would otherwise be a section` :
		| Element keys can be repeated unless used in copying (see below)
		| Also, this value continuation becomes a space, \ becomes an empty string

## sub section of 'section containing some elements'
field with multiple values :
	- this is a list
	- basically

	field with pairs of values :
		name = value
		these can also have = repeating keys

field with content to avoid repeating :
	- banana
	- apple

field that has its own values and those from < field with content to avoid repeating
	- the < character is an operator that ensures this list also contains
		| the list items banana and apple.

> sections also have the option to deep copy with <<
> shallow copy < on a section will reference the other section's elements, but not when they share a name
> deep copy << will present a mix of this and that section's elements when they share a name
```

#### Inclusion in your project

At the moment, Enohar does not parse an eno document fully compliantly. (Not all elements support templating yet.) At that point, I will make it available on sonatype / maven-central. The below dependence declarations are provided as examples. They include the jre version they were compiled for. Enohar, without tests, is compatible with java 7 up to the latest (currently 12, but 13 in a couple of weeks). Enohar is published with  [compatibility versioning](https://gitlab.com/staltz/comver) in mind. Refer to the changelog in this repository for a survey of whether a given version is appropriate.

##### Maven pom dependence declaration

```xml
	<groupId>ws.nzen.format.eno</groupId>
	<artifactId>enohar</artifactId>
	<version>1.0+j8</version>
	<version>1.0+j11</version>
	<version>1.0+j12</version>
	<version>1.0+j7</version>
```

##### Gradle build dependence declaration

```
	'ws.nzen.format.eno:enohar:1.0.j8'
	'ws.nzen.format.eno:enohar:1.0.j11'
	'ws.nzen.format.eno:enohar:1.0.j12'
	'ws.nzen.format.eno:enohar:1.0.j7'
```

At this time, Enohar relies on one 'library', [note_enohaste](https://github.com/Nzen/note_enohaste). This is a jar with locale files preprocessed from the enolib project, using [note_enojes](https://github.com/Nzen/note_enojes). In this fashion, consumers may provide their own locale files, when desired.

In the near future, Enohar will also rely on Slf4J. In this fashion, its output will dump with the rest of your logs. For the moment, it complains to System.out .

Enohar includes some tests. These are a mix of junit 5 and cucumber-jvm tests. Some included tests use the lambda syntax, which requires compiling with java 8 or later compatibility.

#### Progress

Struck elements are not yet implemented.

* lex some eno (0.0-alpha.1.0)
* parse some eno (0.0-alpha.2.0)
* expose elements (0.0-alpha.3.0)
* semantic analysis (0.0-alpha.4.0)
* recognize empty-element (0.0-alpha.7.0)
* emit eno document (0.0-alpha.8.0)
* >use canon localization
* >use templates
* >wrap missing-element api
* ~parse incrementally~
* ~html,terminal-color logging~
* ~recognize some types ('loaders')~
* ~enable use-tracking ('touch')~

### Sample usage

Below is a self-contained example of reading one of the eno formatted issues of this project, changing it from status to do, and writing the file back to disc. Before the example is a list of libraries currently using Enohar.

* [JarLauncher](https://github.com/Nzen/JarLauncher/blob/master/src/main/java/ws/nzen/jarl/parser/EnoParser.java#L77) paired with the file it's intended [to consume](https://github.com/Nzen/JarLauncher/blob/master/etc/config.eno)

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import ws.nzen.format.eno.Eno;
import ws.nzen.format.eno.Section;
import ws.nzen.format.eno.Value;
public class ExampleOfEnoUsage
{
	public static void main( String[] args )
	{
		/* NOTE assuming invocation with path as first argument,
		ex `java -cp enohar-1.0+j8.jar ExampleOfEnoUsage usr/issues/190718_2253_slf4j.eno` */
		try
		{
			Path whereIsFile = Paths.get( args[ 0 ] );
			Section wholeDoc = new Eno().deserialize( whereIsFile );
			/* NOTE assuming document content is:
			# issue 190718_2253
			assignee : Nicholas (Nzen)
			title : provide client configurable logging with slf4j
			status : to do
			*/
			Section slf4jIssue = wholeDoc.section( "issue 190718_2253" );
			Value issueStatus = (Value)slf4jIssue.field( "status" );
			if ( issueStatus.requiredStringValue().equals( "to do" ) )
			{
				issueStatus.setStringValue( "in progress" );
				StringBuilder asText = wholeDoc.toString( new StringBuilder() );
				Path notReplacing = whereIsFile.getParent().resolve( "new.eno" );
				Files.write( whereIsFile, asText.toString().getBytes() );
			}
		}
		catch ( InvalidPathException | IOException ie )
		{
			System.err.println( ie );
			System.exit( 0 );
		}
	}
}
```

#### Name

"Enojar" is a portuguese-origin, infinitive verb meaning _to anger_ . That word is [pronounced](https://en.wiktionary.org/wiki/enojar#Pronunciation), in US english, the way this library is spelt. Using enojar felt a bit on the nose.

### Contribution

#### License Agreement : MIT

This library intends to maintain the MIT license for its lifetime. To ensure that remains the case, contributions over ten lines of code/content require a contributor license agreement, stating that you release your contributions under the MIT license. As an example, the contemporary project owner (Nzen/Nicholas) has published his agreement under docs/contributors/Nzen.adoc .

Probably, this will involve committing a plaintext and gpg signed file attesting the agreement with your name, and signing the commit with the same key. After that, it's typical git mechanics:

* fork the repository (or clone it, if you aren't using a host like github)
* create a feature branch for the changes
* publish a pull request for this branch
* we discuss the pull request until it's eventually merged

#### Guidelines

Code contributions should have a means of testing the implemented behavior.

Contemporarily, Enohar contains a list of issues for immediate development. Consider those. This readme has a progress section with crossed out enolib milestones. Consider those. Otherwise, we can discuss it.

To avoid relying on a particular webhost's ticket system, the Enohar repository contains issues within docs/issues. Feel free to use that webhost's system, but understand that those will end up migrated to the repository itself.

Enohar doesn't currently have a formally published code of conduct. Any of us might think briefly at the margins, at any given time. So, abide by what the fictional character Spider-Man would consider the Golden Rule. Not 'do what would Peter Parker do', but 'treat others as he would want to be treated'. Otherwise, if you're consistenly gray area or _obviously_ out of accordance, you'll probably earn a shunning.


































