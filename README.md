*MOVED TO [https://github.com/anystub/anystub](https://github.com/anystub/anystub)*

# anystub   

[![Build Status](https://travis-ci.org/anystub/anystub.svg?branch=master)](https://travis-ci.org/anystub/anystub) [![quality](https://sonarqube.com/api/badges/gate?key=org.anystub:anystub)](https://sonarqube.com/dashboard?id=org.anystub:anystub) [![gitter.im/anystub-java](https://badges.gitter.im/anystub-java.svg)](https://badges.gitter.im/anystub-java.svg)

record input/output for tests in java. inspired by vcr for rails

Install from Maven Central 
===

``` xml
    <dependency>
      <groupId>org.anystub</groupId>
      <artifactId>anystub</artifactId>
      <version>0.1.15</version>
    </dependency>
```


Example
===
you can find an example of using in tests folder of the repository. below the first scenario


# you have `class SourceSystem` which have access to external resources (http access).
``` java
public class SourceSystem {

    public String get() throws IOException {
        URL myURL = new URL("http://localhost:8080/");
        URLConnection myURLConnection = myURL.openConnection();
        myURLConnection.connect();


        InputStream inputStream = myURLConnection.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining());
    }


}
```

# You have business-class (Worker) which uses class-accessor and you want to control behaviour of this class.
 it could include building query, processing response and handling exceptions

``` java 
public class Worker {

    private SourceSystem sourceSystem;

    public Worker(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String get() throws IOException {
        return sourceSystem.get();
    }
}
```

to make sure your tests will run everywhere you need to create stub for the SourceSystem and record data that is necessarily for the tests

``` java
public class WorkerEasyTest {

    SourceSystem sourceSystem;
    Base base;

    @Before
    public void createStub()
    {
        base = new Base();
        sourceSystem = new SourceSystem("http://localhost:8080") {
            @Override
            public String get() throws IOException {
                return base.request(() -> super.get(), "root");
            }
        };
    }

    @Test
    public void easyTest() throws IOException {

        Worker worker = new Worker(sourceSystem);
        assertEquals("fixed", worker.get());
        assertEquals(1, base.times());
    }

}
```

By default your data is kept in src/test/resources/anystub/stub.yml. base.request(Supplier<>, String keys..) looks for stored data in the file. In case the search is successful the found result is returned to the client. In other way your supplier would be invoced, produced value will be keep in the file the it will be provided for clients.

**Recap**

your workflow scenario:
- extend your sourceSystem
- create stub on your local system and add it to the repository
- get benefits from automatic regression testing

look at [wiki|https://github.com/anystub/anystub/wiki/anystub-and-SpringBoot] to see how it works with spring-boot
