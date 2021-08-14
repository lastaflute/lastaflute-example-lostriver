LastaFlute Example Lostriver
=======================
example project for LastaFlute as single project (without RDB)

LastaFlute:  
https://github.com/lastaflute/lastaflute

# Quick Trial
Can boot it by example of LastaFlute:

1. git clone https://github.com/lastaflute/lastaflute-example-lostriver.git
2. compile it by Java8, on e.g. Eclipse or IntelliJ or ... as Maven project
3. execute the *main() method of (org.docksidestage.boot) LostriverBoot
4. access to http://localhost:8156/lostriver  

*main() method
```java
public class LostriverBoot {

    public static void main(String[] args) {
        new JettyBoot(8156, "/lostriver").asDevelopment(isNoneEnv()).bootAwait();
    }
}
```

# Information
## License
Apache License 2.0

## Official site
comming soon...
