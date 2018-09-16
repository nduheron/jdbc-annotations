# Spring JDBC annotations

Librairie permettant de générer automatiquement les RowMapper lorsque l'on utilise JdbcTemplate.

[![Build Status](https://travis-ci.org/nduheron/jdbc-annotations.svg?branch=master)](https://travis-ci.org/nduheron/jdbc-annotations) 

## Usage

__Maven__ 

Import du projet (en optionnal car nécessaire seulement lors de la compilation):

```xml
	<dependency>
		<groupId>fr.nduheron.socle.spring</groupId>
		<artifactId>jdbc-annotations-processor</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<optional>true</optional>
	</dependency>
```
Déclaration du processor pour la génération du mapper:

```xml
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.8.0</version>
				<configuration>
					<source>${java.version}</source>
					<target>${java.version}</target>
					<generatedSourcesDirectory>
						${project.build.directory}/generated-sources/
					</generatedSourcesDirectory>
					<annotationProcessors>
						<annotationProcessor>fr.nduheron.socle.jdbc.annotations.processors.JdbcMapperProcessor</annotationProcessor>
					</annotationProcessors>
				</configuration>
			</plugin>
		</plugins>
	</build>
```

__Création du mapper__

```java
@JdbcMapper
public interface UserRowMapper extends RowMapper<User> {

}
```
__Déclaration des champs à mapper__

L'attribut index représente l'index du ResultSet:

```java
	@Column(index = 1)
	private long id;
```


__Mapper un sous objet__

Pour mapper un sous objet, on indique toutes les colonnes nécessaires à la construction de l'objet:

```java
	@Columns({
		@Column(index=2),
		@Column(index=3),
		@Column(index=4)
	})
	private Address address;
```

Le sous objet doit avoir un constructeur :

```java
	public Address(Integer number, String street, String city) {
		super();
		this.number = number;
		this.street = street;
		this.city = city;
	}
```

__Converter__

On peut utiliser un converter pour transformer notre resultset en un objet plus complexe:

```java
@Component
public class DateConverter implements Converter<Date, LocalDate> {

	@Override
	public LocalDate convert(Date source) {
		if (source != null) {
			return source.toLocalDate();
		}
		return null;
	}

}
```

Il suffit de déclarer le converter sur le champ à mapper:

```java
	@Column(index = 5, converter = DateConverter.class)
	private LocalDate lastConnexion;
```
