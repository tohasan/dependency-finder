# dependency-finder

## Description

Console utility finds depenent modules for maven projects. 
You can find modules that depends on another module. 
For example, you can find war and ear that depends on some jar.
Assume we have project with structure presented below and you want to know which modules use example.jar

```
    /my-project
        /common
            /example
                pom.xml <example.jar>
            pom.xml
        /moduleA
            /moduleA1
                /moduleA11
                    pom.xml <has dependency: example.jar>
                pom.xml
            pom.xml <moduleA.war; has dependency: moduleA11.jar>
        /moduleB
            /moduleB1
                pom.xml <moduleB1.jar; has dependency: example.jar>
            /moduleB2
                pom.xml <moduleB2.jar; has dependency: example.jar>
            pom.xml <moduleB.war; has dependencies: moduleB1.jar, moduleB2.jar>
        /moduleC
            pom.xml <moduleC.war>
```

So using dependency finder you get following result:

```
   moduleA.war
   moduleB.war
```

Dependency finder has found modules dependent on example.jar.

## Requirements

1. java 8

## Usage

```
        java -jar df.jar --directory <path/to/directory> --search <dependency name>
```

## Options

```    
        -d,--directory <arg>   search dependent modules in pom.xml in this
                               directory including subdirectories. For example,
                               -d /opt/my-project
        -s,--search <arg>      search modules that have this module as dependency
                               (directly or indirectly). For example, -s modBar
```                            

## Build

```
        mvn clean package
```

## License 

MIT

Copyright (c) 2016 Kalashnikov Anton aka tohasan

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
