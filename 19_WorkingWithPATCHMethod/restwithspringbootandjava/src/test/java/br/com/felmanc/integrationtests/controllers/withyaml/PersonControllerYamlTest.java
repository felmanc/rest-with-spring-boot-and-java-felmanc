package br.com.felmanc.integrationtests.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import br.com.felmanc.configs.TestConfigs;
import br.com.felmanc.integrationtests.controllers.withyaml.mapper.YMLMapper;
import br.com.felmanc.integrationtests.controllers.withyaml.mapper.YamlTreeReader;
import br.com.felmanc.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.felmanc.integrationtests.vo.AccountCredentialsVO;
import br.com.felmanc.integrationtests.vo.PersonVO;
import br.com.felmanc.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YMLMapper objectMapper;

	private static PersonVO person;

	/* em setup() ainda não existe contexto Somente após o primeiro teste que o
	 * Spring é iniciado e passa a existir um contexo
	 */

	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
		// objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // Tratado em YMLMapper

		person = new PersonVO();
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var accessToken = given()
				.filter(new RequestLoggingFilter(LogDetail.ALL))
				.filter(new ResponseLoggingFilter(LogDetail.ALL))
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.body(user, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(TokenVO.class, objectMapper)
						.getAccessToken();

		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();

		PersonVO persistedPerson = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.body(person, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertTrue(persistedPerson.getEnabled());

		assertEquals(person.getId() ,persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(2)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		mockPerson();

		var persistedPerson = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertFalse(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var persistedPerson = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertFalse(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}
	
	@Test
	@Order(4)
	public void testUpdateNotFound() throws JsonMappingException, JsonProcessingException {
		mockPerson();

		PersonVO update = new PersonVO();

		update.setId(22L);
		update.setFirstName(person.getFirstName());
		update.setLastName(person.getLastName());
		update.setAddress(person.getAddress());
		update.setGender(person.getGender());

		var content = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.body(update, objectMapper)
				.when()
				.put()
				.then()
				.statusCode(404)
				.extract()
				.body()
				.asString();

		assertNotNull(content);
		
		YAMLFactory factory = new YAMLFactory(); // Cria uma fábrica para YAML
		try {
			YAMLParser parser = factory.createParser(content); // Cria um parser para processar a string YAML
			YamlTreeReader yamlTreeReader = new YamlTreeReader();
			JsonNode rootNode = yamlTreeReader.readTree(parser);
			String message = rootNode.get("message").asText();
			assertEquals("No records found for this ID: 22", message);			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Test
	@Order(5)
	public void testUpdateWithNoContent() throws JsonMappingException, JsonProcessingException {

		var content = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.body("", objectMapper)
				.when()
				.put()
				.then()
				.statusCode(400)
				.extract()
				.body()
				.asString();

		assertNotNull(content);
		assertEquals(
				"{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"Failed to read request\",\"instance\":\"/api/person/v1\"}",
				content);
	}

	@Test
	@Order(6)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		mockPerson();

		PersonVO update = new PersonVO();

		update.setId(person.getId());
		update.setFirstName(person.getFirstName() + "2");
		update.setLastName(person.getLastName() + "2");
		update.setAddress(person.getAddress());
		update.setGender(person.getGender());

		var persistedPerson = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.body(update, objectMapper)
				.when()
				.put()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertFalse(persistedPerson.getEnabled());
		assertEquals(update.getId() ,persistedPerson.getId());

		assertEquals("Nelson2", persistedPerson.getFirstName());
		assertEquals("Piquet2", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(7)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
										ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(8)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {

		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO[].class, objectMapper);

			
		List<PersonVO> people = Arrays.asList(content);		
		
		PersonVO foundPersonOne = people.get(0);

		assertNotNull(foundPersonOne);
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		
		assertTrue(foundPersonOne.getEnabled());

		assertEquals(1, foundPersonOne.getId());

		assertEquals("Ayrton", foundPersonOne.getFirstName());
		assertEquals("Senna", foundPersonOne.getLastName());
		assertEquals("São Paulo", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
		
		PersonVO foundPersonSix = people.get(5);

		assertNotNull(foundPersonSix);
		
		assertNotNull(foundPersonSix.getId());
		assertNotNull(foundPersonSix.getFirstName());
		assertNotNull(foundPersonSix.getLastName());
		assertNotNull(foundPersonSix.getAddress());
		assertNotNull(foundPersonSix.getGender());
		
		assertTrue(foundPersonSix.getEnabled());

		assertEquals(9, foundPersonSix.getId());

		assertEquals("Nelson", foundPersonSix.getFirstName());
		assertEquals("Mandela", foundPersonSix.getLastName());
		assertEquals("Mvezo -South Africa", foundPersonSix.getAddress());
		assertEquals("Male", foundPersonSix.getGender());
	}
	
	@Test
	@Order(9)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

		RequestSpecification specificationWithouToken;
		
		specificationWithouToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given().spec(specificationWithouToken)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.when()
					.get()
				.then()
					.statusCode(403);
	}

	@Test
	@Order(10)
	public void testUpdateWrongContentType() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		given()
				.spec(specification)
				.contentType("text/html")
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.when()
					.put()
				.then()
					.statusCode(415);
	}

	@Test
	@Order(11)
	public void testUpdateWrongBodyContentType() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		given()
					.spec(specification)
					.contentType(TestConfigs.CONTENT_TYPE_YAML)
					//.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(person, objectMapper)
					.when()
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.put()
				.then()
					.statusCode(400);
	}	
	
	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
		person.setEnabled(true);
	}
}
