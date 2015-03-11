# Bulk Data Processor

I recently had to write a program that can bulk process a lot of data. This is basically the skeleton of that program which is able to fetch and update entries from a database table. There is one thread that scrolls through the database entries in order to keep the memory consumption reasonable and multiple consumer threads that process each entry.
Feel free to use it as you like.

## How To Use

1. Open up your favorite IDE and import this project
2. Configure the database connection via [`persistence.xml`](blob/master/src/META-INF/persistence.xml) and [`hibernate.cfg.xml`](blob/master/src/META-INF/hibernate.cfg.xml)
3. Configure the program via [`config.xml`](blob/master/resources/config.xml)
4. Adjust [`Entry.java`](blob/master/src/com/fniephaus/bulkdataprocessor/Entry.java) to your database schema
5. Define your enumeration ([`StatusType`](blob/36bebd6edfe42b8dad4754a61f8a02daf1ad7b3a/src/com/fniephaus/bulkdataprocessor/helpers/Configuration.java#L9-L11))
6. Implement what you want to do with each entry (See [`EntryProcessor.java`](blob/36bebd6edfe42b8dad4754a61f8a02daf1ad7b3a/src/com/fniephaus/bulkdataprocessor/threads/EntryProcessor.java#L39-L49))
7. Run the [`Main.java`](blob/master/src/com/fniephaus/bulkdataprocessor/Main.java)


## Credits

This projects uses [Java Persistence API](http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html), [Hibernate](http://hibernate.org/), [PostgreSQL](http://www.postgresql.org/), [c3p0](http://www.mchange.com/projects/c3p0/) and [Apache Commons Configuration](http://commons.apache.org/proper/commons-configuration/index.html).
