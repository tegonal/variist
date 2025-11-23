//import kotlin.io.path.ExperimentalPathApi
//import kotlin.io.path.copyToRecursively

plugins {
	id("build-logic.published-kotlin-jvm")
	id("code-generation.generate")
	id("me.champeau.jmh") version "0.7.3"
}

version = "2.0.0-RC-4-SNAPSHOT"
group = "com.tegonal.variist"
description = "Library which helps to setup and prioritise parameterized tests"


dependencies {
	api(libs.junit.jupiter.params)
	api(libs.kbox)
	implementation(kotlin("reflect"))

	testImplementation(kotlin("test"))
	testImplementation(libs.atrium.fluent)
}

val generationFolder: ConfigurableFileCollection = project.files("src/main/generated/kotlin")
val generationFolderJava: ConfigurableFileCollection = project.files("src/main/generated/java")
val generationTestFolder: ConfigurableFileCollection = project.files("src/test/generated/kotlin")
val generationTestFolderJava: ConfigurableFileCollection = project.files("src/test/generated/java")

kotlin {
	sourceSets {
		main {
			kotlin.srcDir(generationFolder)
		}
		test {
			kotlin.srcDir(generationTestFolder)
		}
	}
}
java {
	sourceSets {
		main {
			java.srcDir(generationFolderJava)
			java.srcDir(project.files("src/main/lib/java"))
		}
		test {
			java {
				srcDir(generationTestFolderJava)
			}
		}
	}
}

// current workaround for java so that we can execute `main` methods
// intellij uses the module path and apparently it is not possible to patch modules if it contains only
// the module-info.class -- moreover, intellij adds only the java sources to the module path, so as for now it is
// easier to just copy the kotlin .class files to the output folder of java
//project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
//	doLast {
//		this.outputs.files.filter { it.name == "main" }.forEach {
//			@OptIn(ExperimentalPathApi::class)
//			it.toPath().copyToRecursively(
//				project.layout.buildDirectory.file("classes/java/main/").get().asFile.toPath(),
//				followLinks = false,
//				overwrite = true
//			)
//		}
//	}
//}

jmh {
	profilers = listOf("gc")
	// run ./gradlew jmh -Pjmh.include=... to run a specific bench
	// transform the txt to csv via ./scripts/jmh-txt-to-csv.sh
	// find the report in build/results/jmh/results.txt.csv
	includes.set(project.findProperty("jmh.include")?.let { listOf(it.toString()) } ?: emptyList())
}

/*
Release & deploy a commit
--------------------------------

1. update main:


export VARIIST_PREVIOUS_VERSION=2.0.0-RC-3
export VARIIST_VERSION=2.0.0-RC-4
find ./ -name "*.md" | xargs perl -0777 -i \
   -pe "s@$VARIIST_PREVIOUS_VERSION@$VARIIST_VERSION@g;" \
   -pe "s@tree/main@tree/v$VARIIST_VERSION@g;" \
   -pe "s@latest#/kdoc@$VARIIST_VERSION/kdoc@g;"
perl -0777 -i \
  -pe "s@$VARIIST_PREVIOUS_VERSION@$VARIIST_VERSION@g;" \
  -pe "s/version = \"${VARIIST_VERSION}-SNAPSHOT\"/version = \"$VARIIST_VERSION\"/;" \
  ./build.gradle.kts
perl -0777 -i \
  -pe 's/(<!-- for main -->\n)\n([\S\s]*?)(\n<!-- for main end -->\n<!-- for release -->\n)<!--\n([\S\s]*?)-->\n(<!-- for release end -->)/$1<!--\n$2-->$3\n$4\n$5/;' \
  -pe 's/(---\n❗ You are taking[^-]*?---)/<!$1>/;' \
  ./README.md
git commit -a -m "v$VARIIST_VERSION"

check changes (CONTRIBUTING.md, build.gradle.kts, README.md)
git push


2. prepare release on github
    a) git tag "v$VARIIST_VERSION"
    b) git push origin "v$VARIIST_VERSION"
    c) Log in to github and create draft for the release

The tag is required for dokka in order that the externalLinkDocumentation works

3. update github pages:
Assumes you have a variist-gh-pages folder on the same level as variist where the gh-pages branch is checked out

Either use the following commands or the manual steps below (assuming VARIIST_PREVIOUS_VERSION and VARIIST_VERSION
is already set from commands above)

Increment VARIIST_GH_PAGES_VERSIONS_JS_VERSION and VARIIST_GH_PAGES_VERSIONS_JS_VERSION__NEXT

export VARIIST_GH_PAGES_LOGO_CSS_VERSION="1.3"
export VARIIST_GH_PAGES_ALERT_CSS_VERSION="1.1"
export VARIIST_GH_PAGES_VERSIONS_JS_VERSION="1.3.0"
export VARIIST_GH_PAGES_VERSIONS_JS_VERSION_NEXT="1.4.0"

gr dokkaHtml

cd ../variist-gh-pages
git add . && git commit -m "dokka generation for v$VARIIST_VERSION"

perl -0777 -i \
  -pe "s@$VARIIST_PREVIOUS_VERSION@$VARIIST_VERSION@g;" \
  ./index.html
perl -0777 -i \
  -pe "s@$VARIIST_PREVIOUS_VERSION@$VARIIST_VERSION@g;" \
  ./latest/index.html
perl -0777 -i \
  -pe "s/(\s+)\"$VARIIST_PREVIOUS_VERSION\",/\$1\"$VARIIST_VERSION\",\$1\"$VARIIST_PREVIOUS_VERSION\",/;" \
  ./scripts/versions.js


find "./$VARIIST_VERSION" -name "*.html" | xargs perl -0777 -i \
    -pe "s@<script.*src=\"https://unpkg\.com.*</script>@@;" \
    -pe "s@(<div class=\"library-name\">[\S\s]+?)Variist@\$1<span>Variist</span>@;" \
    -pe "s@\"((?:\.\./+)*)styles/logo-styles.css\" rel=\"Stylesheet\">@\"../../\${1}styles/logo-styles.css?v=$VARIIST_GH_PAGES_LOGO_CSS_VERSION\" rel=\"Stylesheet\">\n<link href=\"../../\${1}styles/alert.css?v=$VARIIST_GH_PAGES_ALERT_CSS_VERSION\" rel=\"Stylesheet\">\n<script id=\"versions-script\" type=\"text/javascript\" src=\"\../../\${1}scripts/versions.js?v=$VARIIST_GH_PAGES_VERSIONS_JS_VERSION\" data-version=\"$VARIIST_VERSION\" async=\"async\"></script>@g;" \
    -pe "s@((?:\.\./+)*)images/logo-icon.svg\"([^>]+)>@../../\${1}images/logo-icon.svg\"\$2>\n<meta name=\"og:image\" content=\"../../\${1}images/logo_social.png\"/>@g;" \
    -pe "s@(<a class=\"library-name--link\" href=\"(?:\.\./+)*)index.html\">@\$1../../index.html\" title=\"Back to Overview Code Documentation of Variist\">@g;" \
    -pe "s@<html@<html lang=\"en\"@g;" \
    -pe "s@<head>@<head>\n<meta name=\"keywords\" content=\"Kotlin, junit, junit-jupiter, test, Testing, parameterized tests, minimal test set\">\n<meta name=\"author\" content=\"Tegonal Genossenschaft\">\n<meta name=\"copyright\" content=\"Tegonal Genossenschaft\">@g;" \
    -pe "s@<title>([^<]+)</title>@<title>\$1 - Variist $VARIIST_VERSION</title>\n<meta name=\"description\" content=\"Code documentation of Variist $VARIIST_VERSION: \$1\">@g;" \
    -pe "s@(<code class=\"runnablesample[^>]+>)[\S\s]+?//sampleStart[\n\s]*([\S\s]+?)\s+//sampleEnd[\n\s]*\}@\${1}\${2}@g;"

find "./" -name "*.html" | xargs perl -0777 -i \
    -pe "s@(scripts/versions\.js\?v\=)$VARIIST_GH_PAGES_VERSIONS_JS_VERSION@\${1}$VARIIST_GH_PAGES_VERSIONS_JS_VERSION_NEXT@g;"

cp "./$VARIIST_PREVIOUS_VERSION/index.html" "./$VARIIST_VERSION/index.html"
perl -0777 -i \
  -pe "s/$VARIIST_PREVIOUS_VERSION/$VARIIST_VERSION/g;" \
  -pe "s@Released .*</p>@Released $(LC_ALL=en_GB date '+%b %d, %Y')</p>@;" \
  "./$VARIIST_VERSION/index.html"
git add . && git commit -m "v$VARIIST_VERSION"

check changes
git push

cd ../variist

3. deploy to sonatype central portal:
(assumes you have an alias named gr pointing to ./gradlew)
    a) java -version 2>&1 | grep "version \"11" && PUB=true CI=true gr clean pubToMaLo &&
       tmpDir=$(mktemp -d -t "variist-release-$VARIIST_VERSION-XXXXXXXXXX") &&
       find "$HOME/.m2/repository/com/tegonal/variist" -type d -name "*$VARIIST_VERSION" -print0 |
         while read -r -d $'\0' versionDir; do
           find "$versionDir" -type f -print0 | while read -r -d $'\0' file; do
              relPath="${file#"$HOME/.m2/repository/"}"
              mkdir -p "$tmpDir/$(dirname "$relPath")"
              cp "$file" "$tmpDir/$relPath"
           done
         done &&
       find "$tmpDir" -type f -not -name "*.md5" -not -name "*.sha1" -not -name "*.asc" -print0 |
         while read -r -d $'\0' file; do
           base=$(basename "$file");
           md5sum "$file" | awk '{ print $1 }' > "${file}.md5"
           sha1sum "$file" | awk '{ print $1 }' > "$file.sha1"
       done &&
       (cd "$tmpDir" && zip -r "variist-$VARIIST_VERSION.zip" .) &&
       find "$tmpDir" -name "*.jar" | head -n 1 | xargs -I {} gpg --verify "{}.asc" "{}" &&
       echo "verify the correct gpg key was used (see above) and you might want to check the release in file://$tmpDir"
    b) Log into https://central.sonatype.com/publishing/deployments
    c) click on publish component and upload zip
    d) click on publish once verification is done

4. publish release on github
    1) Log in to github and publish draft

Prepare next dev cycle
-----------------------
    1. update main:


export VARIIST_VERSION=2.0.0-RC-3
export VARIIST_NEXT_VERSION=2.0.0-RC-4
find ./ -name "*.md" | xargs perl -0777 -i \
   -pe "s@tree/v$VARIIST_VERSION@tree/main@g;" \
   -pe "s@$VARIIST_VERSION/kdoc@latest#/kdoc@g;" \
   -pe "s/add \\\`\@since $VARIIST_VERSION\\\` \(adapt to current/add \\\`\@since $VARIIST_NEXT_VERSION\\\` \(adapt to current/g;"
perl -0777 -i \
  -pe "s/rootProject.version = \"$VARIIST_VERSION\"/rootProject.version = \"${VARIIST_NEXT_VERSION}-SNAPSHOT\"/;" \
  -pe "s/VARIIST_VERSION=$VARIIST_VERSION/VARIIST_VERSION=$VARIIST_NEXT_VERSION/;" \
  ./build.gradle.kts
perl -0777 -i \
  -pe 's/(<!-- for main -->\n)<!--\n([\S\s]*?)-->(\n<!-- for release -->)\n([\S\s]*?)\n(\n# <img)/$1\n$2$3\n<!--$4-->\n$5/;' \
  -pe 's/<!(---\n❗ You are taking[^-]*?---)>/$1/;' \
  -pe "s@(latest version: \[README of v$VARIIST_VERSION\].*tree/)main/@\$1v$VARIIST_VERSION/@;" \
  ./README.md
git commit -a -m "prepare dev cycle of $VARIIST_NEXT_VERSION"

check changes
git push

*/
