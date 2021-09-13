# CashShop

## Como usar:

### pom.xml
```xml 
<dependency>
    <groupId>me.davethecamper.cashshop</groupId>
    <artifactId>cash-shop</artifactId>
    <version>0.0.1</version>
</dependency>
```
### settings.xml
```xml
    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>
  
    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                    <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                </repository>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/DaveTheCamper/CashShop</url>
                    <snapshots>
                    <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
  
    <servers>
        <server>
            <id>github</id>
            <username>**{SEU.USERNAME.GITHUB}**</username>
            <password>**{SEU.TOKEN.GITHUB}**</password>
        </server>
    </servers>
```

Mais informações sobre ``pom.xml`` https://maven.apache.org/pom.html

Mais informações sobre ``settings.xml`` https://maven.apache.org/settings.html
