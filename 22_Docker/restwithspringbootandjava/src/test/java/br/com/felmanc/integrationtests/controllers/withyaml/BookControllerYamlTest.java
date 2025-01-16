package br.com.felmanc.integrationtests.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Date;

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
import br.com.felmanc.integrationtests.vo.BookVO;
import br.com.felmanc.integrationtests.vo.TokenVO;
import br.com.felmanc.integrationtests.vo.pagedmodels.PagedModelBook;
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
public class BookControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static BookVO book;
	
	/* em setup() ainda não existe contexto
	 * Somente após o primeiro teste que o Spring é iniciado e passa a existir um contexo
	 */
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
		//objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // O YAML será gerado somente com os dados. FAIL_ON_UNKNOWN_PROPERTIES é necessário para desconsiderar a inexistencia do HATEOAS
		
		book = new BookVO();
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
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.body(user , objectMapper)
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
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();
	
		BookVO persistedBook = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.body(book, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(BookVO.class, objectMapper);

		book = persistedBook;
		
		assertNotNull(persistedBook);
		
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getTitle());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		
		assertTrue(persistedBook.getId() > 0);

		assertEquals("Effective Java", persistedBook.getTitle());
		assertEquals("Joshua Bloch", persistedBook.getAuthor());
		assertEquals(65.9d, persistedBook.getPrice());
	}

	@Test
	@Order(2)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		var foundBook = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.pathParam("id", book.getId())
					.body(book, objectMapper)
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(BookVO.class, objectMapper);
		
		assertNotNull(foundBook);
		
		assertNotNull(foundBook.getId());
		assertNotNull(foundBook.getTitle());
		assertNotNull(foundBook.getAuthor());
		assertNotNull(foundBook.getLaunchDate());
		assertNotNull(foundBook.getPrice());
		
		assertTrue(foundBook.getId() > 0);

		assertEquals("Effective Java", foundBook.getTitle());
		assertEquals("Joshua Bloch", foundBook.getAuthor());
		assertEquals(65.9d, foundBook.getPrice());
	}

	@Test
	@Order(3)
	public void testUpdateNotFound() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		BookVO update = new BookVO();
		
		update.setId(30L);
		update.setTitle(book.getTitle());
		update.setAuthor(book.getAuthor());
		update.setLaunchDate(book.getLaunchDate());
		update.setPrice(book.getPrice());	
		
		var content = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
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
			assertEquals("No records found for this ID: 30", message);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	@Test
	@Order(4)
	public void testUpdateWithNoContent() throws JsonMappingException, JsonProcessingException {
		
		var content = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					//.body("")
					.when()
					.put()
				.then()
					.statusCode(400)
				.extract()
					.body()
						.asString();
		
		assertNotNull(content);
		assertEquals(
				"{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"Failed to read request\",\"instance\":\"/api/book/v1\"}",
				content);
	}

	@Test
	@Order(5)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		BookVO update = new BookVO();
		
		update.setId(book.getId());
		update.setTitle(book.getTitle() +  "ABC");
		update.setAuthor(book.getAuthor() +  "DEF");
		update.setLaunchDate(book.getLaunchDate());
		update.setPrice(book.getPrice());
		
		var updatedBook = given()
				.config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
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
						.as(BookVO.class, objectMapper);

		assertNotNull(updatedBook);
		
		assertNotNull(updatedBook.getId());
		assertNotNull(updatedBook.getTitle());
		assertNotNull(updatedBook.getAuthor());
		assertNotNull(updatedBook.getLaunchDate());
		assertNotNull(updatedBook.getPrice());
		
		assertEquals(update.getId() ,updatedBook.getId());

		assertEquals("Effective JavaABC", updatedBook.getTitle());
		assertEquals("Joshua BlochDEF", updatedBook.getAuthor());
		assertEquals(65.9d, updatedBook.getPrice());
	}

	@Test
	@Order(6)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		given()
		.config(RestAssuredConfig
				.config()
				.encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
		.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.pathParam("id", book.getId())
				.body(book, objectMapper)
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}

	@Test
	@Order(7)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {

		var wrapper = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.queryParams("page", 0, "size", 7, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PagedModelBook.class, objectMapper);
		
		var books = wrapper.getContent();		

		//('Brian Goetz e Tim Peierls', '2017-11-07 15:09:01.674000', 80.00, 'Java Concurrency in Practice')
		BookVO foundBookTwo = books.get(1);

		assertNotNull(foundBookTwo);
		
		assertNotNull(foundBookTwo.getId());
		assertNotNull(foundBookTwo.getTitle());
		assertNotNull(foundBookTwo.getAuthor());
		assertNotNull(foundBookTwo.getLaunchDate());
		assertNotNull(foundBookTwo.getPrice());
		
		assertEquals(9, foundBookTwo.getId());

		assertEquals("Java Concurrency in Practice", foundBookTwo.getTitle());
		assertEquals("Brian Goetz e Tim Peierls", foundBookTwo.getAuthor());
		assertEquals(80.0d, foundBookTwo.getPrice());
		
		//('Martin Fowler e Kent Beck', '2017-11-07 15:09:01.674000', 88.00, 'Refactoring')
		BookVO foundBookSeven = books.get(6);

		assertNotNull(foundBookSeven);
		
		assertNotNull(foundBookSeven.getId());
		assertNotNull(foundBookSeven.getTitle());
		assertNotNull(foundBookSeven.getAuthor());
		assertNotNull(foundBookSeven.getLaunchDate());
		assertNotNull(foundBookSeven.getPrice());
		
		assertEquals(6, foundBookSeven.getId());

		assertEquals("Refactoring", foundBookSeven.getTitle());
		assertEquals("Martin Fowler e Kent Beck", foundBookSeven.getAuthor());
		assertEquals(88.0d, foundBookSeven.getPrice());
	}

	@Test
	@Order(8)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

		RequestSpecification specificationWithouToken;
		
		specificationWithouToken = new RequestSpecBuilder()
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given()
			.spec(specificationWithouToken)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.when()
				.get()
			.then()
				.statusCode(403);
	}

	@Test
	@Order(9)
	public void testUpdateWrongContentType() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
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
	@Order(10)
	public void testUpdateWrongBodyContentType() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		given()
			.spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_YAML)
			.body(book, objectMapper)
			.when()
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
			.put()
				.then()
					.statusCode(400);
	}	

	@Test
	@Order(11)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {

		var untreatedContent = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.queryParams("page", 0, "size", 7, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		var content = untreatedContent.replace("\r", "").replace("\n", "");
		
		System.out.println(content);

		assertTrue(content.contains("links:  - rel: \"self\"    href: \"http://localhost:8888/api/book/v1/9\""));
		assertTrue(content.contains("links:  - rel: \"self\"    href: \"http://localhost:8888/api/book/v1/8\""));
		assertTrue(content.contains("links:  - rel: \"self\"    href: \"http://localhost:8888/api/book/v1/14\""));

		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=0&size=7&sort=author,asc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/book/v1?page=0&size=7&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=7&sort=author,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=2&size=7&sort=author,asc\""));
		
		assertTrue(content.contains("page:  size: 7  totalElements: 15  totalPages: 3  number: 0"));
	}

	private void mockBook() {
		book.setTitle("Effective Java");
		book.setAuthor("Joshua Bloch");
		book.setLaunchDate(new Date());
		book.setPrice(65.9d);
	}
}
