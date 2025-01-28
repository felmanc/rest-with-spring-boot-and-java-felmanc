package br.com.felmanc.configs;

import org.springframework.http.MediaType;

public class TestConfigs {
	public static final int SERVER_PORT = 8888;

	public static final String HEADER_PARAM_AUTHORIZATION = "Authorization";
	public static final String HEADER_PARAM_ORIGIN = "Origin";

	public static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;
	public static final String CONTENT_TYPE_XML = MediaType.APPLICATION_XML_VALUE;
	public static final String CONTENT_TYPE_YAML = MediaType.APPLICATION_YAML_VALUE;
	
	public static final String ORIGIN_ERUDIO = "https://erudio.com.br";
	public static final String ORIGIN_SEMERU = "https://semeru.com.br";
	public static final String ORIGIN_FELMANC = "https://felmanc.com.br";
}
