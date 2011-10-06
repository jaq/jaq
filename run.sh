#!/bin/bash
#JAVA_HOME=/opt/java/sdk/java

echo "Using JAVA_HOME $JAVA_HOME"

JARS=./lib/*.jar
CLASSPATH=$CLASSPATH:./data/
for i in ${JARS}
do
    if [ "$i" != "${JARS}" ] ; then
        CLASSPATH=$CLASSPATH:"$i"
    fi
done

JAVA_OPTS="-Xmx256m -Xms232m -Xss256k"

$JAVA_HOME/bin/java $JAVA_OPTS  -cp $CLASSPATH org.jaq.QueryTool $@
