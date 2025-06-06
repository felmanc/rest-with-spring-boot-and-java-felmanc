package br.com.felmanc.integrationtests.controllers.cors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.felmanc.configs.TestConfigs;
import br.com.felmanc.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.felmanc.integrationtests.vo.AccountCredentialsVO;
import br.com.felmanc.integrationtests.vo.PersonVO;
import br.com.felmanc.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerCorsJsonTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	
	private static PersonVO person;
	
	/* em setup() ainda não existe contexto
	 * Somente após o primeiro teste que o Spring é iniciado e passa a existir um contexo
	 */
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // O JSON será gerado somente com os dados. FAIL_ON_UNKNOWN_PROPERTIES é necessário para desconsiderar a inexistencia do HATEOAS
		
		person = new PersonVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var accessToken = given()
				.filter(new RequestLoggingFilter(LogDetail.ALL))
				.filter(new ResponseLoggingFilter(LogDetail.ALL))
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(user)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(TokenVO.class)
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
	
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FELMANC)
					.body(person)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);

		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertTrue(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
				.body(person)
				.when()
				.post()
				.then()
				.statusCode(403)
				.extract()
				.body()
				.asString();
		
		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}
	
	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FELMANC)
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);

		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertTrue(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
					.when()
					.get("{id}")
				.then()
					.statusCode(403)
				.extract()
					.body()
						.asString();
		
		assertNotNull(content);
		assertEquals("Invalid CORS request", content);

	}	

	@Test
	@Order(5)
	public void testUpdateNotFound() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		PersonVO update = new PersonVO();
		
		update.setId(22L);
		update.setFirstName(person.getFirstName());
		update.setLastName(person.getLastName());
		update.setAddress(person.getAddress());
		update.setGender(person.getGender());	
		
		var content = given()
				.spec(specification)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FELMANC)
					.body(update)
					.when()
					.put()
				.then()
					.statusCode(404)
					.extract()
					.body()
					.asString();
		
		assertNotNull(content);
		
		JsonNode rootNode = objectMapper.readTree(content);
		String message = rootNode.get("message").asText();
		
		assertEquals("No records found for this ID: 22", message);
	}
	
	
	@Test
	@Order(6)
	public void testUpdateWithNoContent() throws JsonMappingException, JsonProcessingException {
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FELMANC)
					.body("")
					.when()
					.put()
				.then()
					.statusCode(400)
				.extract()
					.body()
						.asString();
		
		assertNotNull(content);
		assertEquals("{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"Failed to read request\",\"instance\":\"/api/person/v1\"}", content);
	}

	@Test
	@Order(7)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		PersonVO update = new PersonVO();
		
		update.setId(person.getId());
		update.setFirstName(person.getFirstName() +  "2");
		update.setLastName(person.getLastName() +  "2");
		update.setAddress(person.getAddress());
		update.setGender(person.getGender());
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FELMANC)
					.body(update)
					.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);

		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertTrue(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Richard2", persistedPerson.getFirstName());
		assertEquals("Stallman2", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}
	

	@Test
	@Order(8)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_FELMANC)
					.when()
					.delete("{id}")
				.then()
					.statusCode(204)
				.extract()
					.body()
						.asString();
		
		assertNotNull(content);
	}

	
	private void mockPerson() {
		person.setFirstName("Richard");
		person.setLastName("Stallman");
		person.setAddress("New York City, New York, US");
		person.setGender("Male");
		person.setEnabled(true);
	}
}
