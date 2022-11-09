# CashShop

## Gateways:

  <ul>
    <li><a href="https://github.com/DaveTheCamper/PayPalGateway" target="_blank">PayPal</a></li>
    <li><a href="https://github.com/DaveTheCamper/PagSeguroGateway" target="_blank">PagSeguro</a></li>
  </ul>

## Como usar:

### pom.xml
```xml 
<dependency>
    <groupId>me.davethecamper.cashshop</groupId>
    <artifactId>cash-shop</artifactId>
    <version>**{VERSÃO}**</version>
</dependency>
```
### settings.xml
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
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
</settings>
```

Mais informações sobre ``pom.xml`` https://maven.apache.org/pom.html

Mais informações sobre ``settings.xml`` https://maven.apache.org/settings.html
