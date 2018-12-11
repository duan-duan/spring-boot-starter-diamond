# spring-boot-starter-diamond
将diamond集成到springboot中使用，spring-boot-starte构件。


spring-boot-starter-diamond，让你可以使用spring-boot的方式开发diamond程序。使diamond开发变得如此简单。

让你可以使用`spring-boot`的方式开发`diamond`程序。使`diamond`开发变得如此简单。

## 如何使用

### 1. `clone`代码（还未发布到中央仓库）


```sh
git clone git@github.com:star45/spring-boot-starter-diamond.git
```

### 2. 编译安装（可选）

```sh
cd spring-boot-starter-diamond
mvn clean install
```

### 3. 修改`maven`配置文件

* 在`spring boot`项目的`pom.xml`增加`parent`:

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.10.RELEASE</version>
        <relativePath/>
    </parent>
 ```

* 在`spring boot`项目的`pom.xml`中添加以下依赖：

根据实际情况依赖最新版本

```xml
 <dependency>
    <groupId>com.github.star45</groupId>
    <artifactId>spring-boot-starter-diamond</artifactId>
    <version>1.0.0</version>
 </dependency>
 ```


```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>1.3.6.RELEASE</version>
</plugin>
```

在`application.properties`添加`diamond`的信息，如下：

```properties
# setting for diamond

spring.diamond.group=*_diamond_group
spring.diamond.dataId=*_dianmond_dataid