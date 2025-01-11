package br.com.felmanc.integrationtests.controllers.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.felmanc.configs.TestConfigs;
import br.com.felmanc.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.felmanc.integrationtests.vo.AccountCredentialsVO;
import br.com.felmanc.integrationtests.vo.BookVO;
import br.com.felmanc.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXMLTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	
	private static BookVO book;
	
	/* em setup() ainda não existe contexto
	 * Somente após o primeiro teste que o Spring é iniciado e passa a existir um contexo
	 */
	
	@BeforeAll
	public static void setup() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // O XML será gerado somente com os dados. FAIL_ON_UNKNOWN_PROPERTIES é necessário para desconsiderar a inexistencia do HATEOAS
		
		book = new BookVO();
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
	
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);

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
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.pathParam("id", book.getId())
					.body(book)
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		BookVO foundBook = objectMapper.readValue(content, BookVO.class);

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
					.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
		
		assertEquals("No records found for this ID: 30", message);
	}
	
	
	@Test
	@Order(4)
	public void testUpdateWithNoContent() throws JsonMappingException, JsonProcessingException {
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(update)
					.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);

		assertNotNull(persistedBook);
		
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getTitle());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		
		assertEquals(update.getId() ,persistedBook.getId());

		assertEquals("Effective JavaABC", persistedBook.getTitle());
		assertEquals("Joshua BlochDEF", persistedBook.getAuthor());
		assertEquals(65.9d, persistedBook.getPrice());
	}

	@Test
	@Order(6)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		given()
			.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", book.getId())
				.body(book)
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}

	@Test
	@Order(7)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {

		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		List<BookVO> books = objectMapper.readValue(content, new TypeReference<List<BookVO>>() {});		
		
		//('Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm', '2017-11-29 15:15:13.636000', 45.00, 'Design Patterns'),
		BookVO foundBookTwo = books.get(1);

		assertNotNull(foundBookTwo);
		
		assertNotNull(foundBookTwo.getId());
		assertNotNull(foundBookTwo.getTitle());
		assertNotNull(foundBookTwo.getAuthor());
		assertNotNull(foundBookTwo.getLaunchDate());
		assertNotNull(foundBookTwo.getPrice());
		
		assertEquals(2, foundBookTwo.getId());

		assertEquals("Design Patterns", foundBookTwo.getTitle());
		assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", foundBookTwo.getAuthor());
		assertEquals(45.0d, foundBookTwo.getPrice());
		
		//('Eric Freeman, Elisabeth Freeman, Kathy Sierra, Bert Bates', '2017-11-07 15:09:01.674000', 110.00, 'Head First Design Patterns'),
		BookVO foundBookSeven = books.get(6);

		assertNotNull(foundBookSeven);
		
		assertNotNull(foundBookSeven.getId());
		assertNotNull(foundBookSeven.getTitle());
		assertNotNull(foundBookSeven.getAuthor());
		assertNotNull(foundBookSeven.getLaunchDate());
		assertNotNull(foundBookSeven.getPrice());
		
		assertEquals(7, foundBookSeven.getId());

		assertEquals("Head First Design Patterns", foundBookSeven.getTitle());
		assertEquals("Eric Freeman, Elisabeth Freeman, Kathy Sierra, Bert Bates", foundBookSeven.getAuthor());
		assertEquals(110.0d, foundBookSeven.getPrice());
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
			.body(book)
			.when()
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
			.put()
				.then()
					.statusCode(400);
	}	
	
	private void mockBook() {
		book.setTitle("Effective Java");
		book.setAuthor("Joshua Bloch");
		book.setLaunchDate(new Date());
		book.setPrice(65.9d);
	}
}
