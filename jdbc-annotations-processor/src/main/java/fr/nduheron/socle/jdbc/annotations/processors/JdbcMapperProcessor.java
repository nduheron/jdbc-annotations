package fr.nduheron.socle.jdbc.annotations.processors;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.StringUtils;

import fr.nduheron.socle.jdbc.annotations.core.Column;
import fr.nduheron.socle.jdbc.annotations.core.Columns;
import fr.nduheron.socle.jdbc.annotations.core.JdbcMapper;
import fr.nduheron.socle.jdbc.annotations.processors.model.Converter;
import fr.nduheron.socle.jdbc.annotations.processors.model.Entity;
import fr.nduheron.socle.jdbc.annotations.processors.model.Field;
import fr.nduheron.socle.jdbc.annotations.processors.model.RowMapper;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@SupportedAnnotationTypes("fr.nduheron.socle.jdbc.annotations.core.JdbcMapper")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JdbcMapperProcessor extends AbstractProcessor {

	private static final Set<String> javaNulSafeTypes = new HashSet<>();
	private static final Set<String> javaTypes = new HashSet<>();

	private freemarker.template.Template rowMapperTemplate;
	static {
		javaNulSafeTypes.add(String.class.getCanonicalName());
		javaNulSafeTypes.add(BigDecimal.class.getCanonicalName());
		javaNulSafeTypes.add(Date.class.getCanonicalName());
		javaNulSafeTypes.add(Time.class.getCanonicalName());
		javaNulSafeTypes.add(Timestamp.class.getCanonicalName());
		javaNulSafeTypes.add(java.util.Date.class.getCanonicalName());
	}

	static {
		javaTypes.add(Boolean.class.getCanonicalName());
		javaTypes.add(Character.class.getCanonicalName());
		javaTypes.add(Byte.class.getCanonicalName());
		javaTypes.add(Short.class.getCanonicalName());
		javaTypes.add(Integer.class.getCanonicalName());
		javaTypes.add(Long.class.getCanonicalName());
		javaTypes.add(Float.class.getCanonicalName());
		javaTypes.add(Double.class.getCanonicalName());
	}

	public JdbcMapperProcessor() {
		try {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
			cfg.setClassForTemplateLoading(this.getClass(), "/");
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			cfg.setLogTemplateExceptions(false);
			cfg.setWrapUncheckedExceptions(true);
			cfg.setSharedVariable("statics", ((BeansWrapper) cfg.getObjectWrapper()).getStaticModels());
			rowMapperTemplate = cfg.getTemplate("rowmapper.ftlh");
		} catch (Exception e) {
			throw new IllegalStateException("Erreur lors de l'initilisation de freemarker.", e);
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element elem : roundEnv.getElementsAnnotatedWith(JdbcMapper.class)) {
			if (elem.getKind().isInterface()) {
				TypeElement classElement = (TypeElement) elem;
				System.out.println("Création du mapper: " + classElement.getQualifiedName());

				try {
					RowMapper model = buildRowMapperModel(classElement);
					writeRowMapperImpl(model);

				} catch (Exception e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), elem);
				}

			} else {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING,
						"Annotation invalide sur la classe " + elem.getSimpleName(), elem);
			}
		}
		return false;
	}

	/**
	 * Construit un objet {@link RowMapper} à partir du code source de l'interface
	 * annotée {@link JdbcMapper}
	 * 
	 * @param classElement
	 * @return
	 */
	private RowMapper buildRowMapperModel(TypeElement classElement) {
		RowMapper model = new RowMapper();
		model.setPackageName(((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString());
		model.setClassName(classElement.getSimpleName().toString());

		// Vérifie que l'annotation JdbcMapper est présente sur un RowMapper
		DeclaredType rowMapper = classElement.getInterfaces().stream().map(i -> (DeclaredType) i)
				.filter(i -> "org.springframework.jdbc.core.RowMapper"
						.equals(((TypeElement) i.asElement()).getQualifiedName().toString()))
				.findAny().orElseThrow(() -> new IllegalArgumentException(
						"Annotation invalide sur la classe " + classElement.getSimpleName()));
		model.setEntity(
				buildEntityModel((TypeElement) ((DeclaredType) rowMapper.getTypeArguments().get(0)).asElement()));
		return model;
	}

	/**
	 * Construit un objet {@link Entity} à partir des sources du domaine métier
	 * 
	 * @return
	 */
	private Entity buildEntityModel(TypeElement entity) {
		Entity entityModel = new Entity();
		entityModel.setClassName(entity.getSimpleName().toString());
		entityModel.setFullClassName(entity.getQualifiedName().toString());

		List<VariableElement> entityFields = getFields(entity);

		entityFields.stream().filter(e -> e.getAnnotation(Column.class) != null).map(e -> e.getAnnotation(Column.class))
				.collect(Collectors.groupingBy(Column::index, Collectors.counting())).entrySet().stream()
				.filter(e -> e.getValue() > 1).map(Map.Entry::getKey)
				.forEach(index -> processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING,
						"l'index " + index + " est utilisé plusieur fois !!!", entity));

		for (VariableElement variableElement : entityFields) {

			List<Column> columns = new ArrayList<>();
			if (variableElement.getAnnotation(Column.class) != null) {
				columns.add(variableElement.getAnnotation(Column.class));
			} else if (variableElement.getAnnotation(Columns.class) != null) {
				columns.addAll(Arrays.asList(variableElement.getAnnotation(Columns.class).value()));
			}

			if (columns.isEmpty()) {
				processingEnv.getMessager().printMessage(
						Diagnostic.Kind.MANDATORY_WARNING, "Annotation manquante sur le champ "
								+ variableElement.getSimpleName() + " de la classes " + entity.getSimpleName(),
						variableElement);
			} else {
				if (columns.stream().anyMatch(c -> c.index() < 1)) {
					processingEnv.getMessager()
							.printMessage(Diagnostic.Kind.ERROR, "L'index est invalide pour le champ "
									+ variableElement.getSimpleName() + " de la classe " + entity.getSimpleName(),
									variableElement);
				} else {
					Field field = buildField(variableElement, columns);
					if (field.getConverter() != null) {
						entityModel.getConverters().add(field.getConverter());
					}
					entityModel.getConverters()
							.addAll(field.getParameters().stream().filter(f -> f.getConverter() != null)
									.map(Field::getConverter).collect(Collectors.toSet()));
					entityModel.addField(field);
				}
			}
		}

		return entityModel;
	}

	private List<VariableElement> getFields(TypeElement entity) {
		String className = entity.getQualifiedName().toString();
		if ("java.lang.Object".equals(className)) {
			return new ArrayList<>();
		} else {
			List<VariableElement> collect = entity.getEnclosedElements().stream()
					.filter(e -> ElementKind.FIELD == e.getKind()).map(e -> (VariableElement) e)
					.filter(e -> !e.getModifiers().contains(Modifier.STATIC)).collect(Collectors.toList());
			collect.addAll(getFields((TypeElement) ((DeclaredType) entity.getSuperclass()).asElement()));
			return collect;
		}
	}

	private Field buildField(VariableElement variableElement, List<Column> columns) {
		Field field = new Field();
		field.setName(StringUtils.capitalize(variableElement.getSimpleName().toString()));

		Column column = columns.get(0);
		field.setIndex(column.index());

		TypeElement converter = getConverter(column);
		TypeMirror variableType = variableElement.asType();
		if (converter != null) {
			boolean isConverter = converter.getInterfaces().stream().map(i -> (DeclaredType) i)
					.anyMatch(i -> "org.springframework.core.convert.converter.Converter"
							.equals(((TypeElement) i.asElement()).getQualifiedName().toString()));
			if (!isConverter) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						"Le converter " + converter.getSimpleName() + " n'est pas valide.", variableElement);
			}

			ExecutableElement convertMethod = converter.getEnclosedElements().stream()
					.filter(i -> ElementKind.METHOD == i.getKind()).map(m -> (ExecutableElement) m)
					.filter(m -> m.getSimpleName().contentEquals("convert")).findFirst().get();

			variableType = convertMethod.getParameters().get(0).asType();
			TypeElement type = (TypeElement) ((DeclaredType) variableType).asElement();
			if (!variableType.getKind().isPrimitive() && !javaTypes.contains(type.getQualifiedName().toString())
					&& !javaNulSafeTypes.contains(type.getQualifiedName().toString())) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						"La source du converter n'est pas valide", variableElement);
			}
			Converter converterModel = new Converter();
			converterModel.setClassName(converter.getSimpleName().toString());
			converterModel.setFullClassName(converter.getQualifiedName().toString());
			converterModel.setFieldName(StringUtils.uncapitalize(converter.getSimpleName().toString()));
			field.setConverter(converterModel);
		}

		if (variableType.getKind().isPrimitive()) {
			field.setType(StringUtils.capitalize(variableType.getKind().toString().toLowerCase()));
		} else {
			TypeElement type = (TypeElement) ((DeclaredType) variableType).asElement();
			if (javaNulSafeTypes.contains(type.getQualifiedName().toString())) {
				field.setType(type.getSimpleName().toString());
			} else if (ElementKind.ENUM == type.getKind()) {
				field.setEnum(true);
				field.setType(type.getQualifiedName().toString());
			} else if (javaTypes.contains(type.getQualifiedName().toString())) {
				field.setType(type.getQualifiedName().toString());
				field.setNulSafe(true);
			} else {
				Optional<ExecutableElement> matchingConstructor = type.getEnclosedElements().stream()
						.filter(e -> e.getKind() == ElementKind.CONSTRUCTOR).map(e -> (ExecutableElement) e)
						.filter(e -> e.getParameters().size() == columns.size()).findFirst();
				if (!matchingConstructor.isPresent()) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							String.format("Le type %s ne  contient pas de constructeur valide.",
									type.getQualifiedName().toString()),
							variableElement);
				} else {
					field.setType(type.getQualifiedName().toString());
					field.setNulSafe(true);
					List<? extends VariableElement> parameters = matchingConstructor.get().getParameters();
					for (int j = 0; j < parameters.size(); j++) {
						VariableElement paramElement = parameters.get(j);
						field.getParameters().add(buildField(paramElement, Arrays.asList(columns.get(j))));
						if (paramElement.asType().getKind().isPrimitive()) {
							field.setNulSafe(false);
						}
					}

				}
			}
		}
		return field;
	}

	/**
	 * Création de la classe java
	 * 
	 * @param mapper
	 * @throws IOException
	 * @throws TemplateException
	 */
	private void writeRowMapperImpl(RowMapper mapper) throws IOException, TemplateException {
		JavaFileObject jfo = processingEnv.getFiler()
				.createSourceFile(mapper.getPackageName() + "." + mapper.getClassName() + "Impl");
		Map<String, Object> root = new HashMap<>();
		root.put("mapper", mapper);
		try (Writer writer = jfo.openWriter()) {
			rowMapperTemplate.process(root, writer);
		}
	}

	private TypeElement getConverter(Column annotation) {
		try {
			annotation.converter();
		} catch (MirroredTypeException mte) {
			TypeElement element = (TypeElement) ((DeclaredType) mte.getTypeMirror()).asElement();
			return "java.lang.Void".equals(element.getQualifiedName().toString()) ? null : element;
		}
		return null;
	}

}
