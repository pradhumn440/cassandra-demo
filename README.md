# cassandra-demo-R&D

This is a demo project to test the read/write performance of the Cassandra Database which claims to have a really high throughput.

**Apache Cassandra version** - 3.11

**Spring Boot version** - 3.0.5

**JDK used in Spring** - 17

**Java local version** - 1.8 (Java 8)

- Cassandra is not supported for Java 17 until now, it's progress can be tracked here: https://issues.apache.org/jira/browse/CASSANDRA-16895. So, to use cassandra with Java 17, I've set the path variable of java in my .zshrc (local) to Java 8, which works well with Cassandra **3.11**. 

- I've selected JDK 17 for dev in Spring properties.

- Cassandra's read and write performance can be varied by configurations like replication factors, no of nodes etc.
