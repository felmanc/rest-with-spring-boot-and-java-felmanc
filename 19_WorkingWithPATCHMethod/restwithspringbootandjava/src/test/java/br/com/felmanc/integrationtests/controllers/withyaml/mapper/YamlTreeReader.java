package br.com.felmanc.integrationtests.controllers.withyaml.mapper;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YamlTreeReader {

    private final YAMLMapper yamlMapper;

    public YamlTreeReader() {
        this.yamlMapper = new YAMLMapper(new YAMLFactory());
    }

    public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
        _assertNotNull("p", p);
        // Verificar EOF antes de chamar readValue()
        DeserializationConfig cfg = yamlMapper.getDeserializationConfig();
        JsonToken t = p.currentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                return null;
            }
        }
        // _readValue() verifica tokens finais
        JsonNode n = (JsonNode) _readValue(cfg, p, yamlMapper.constructType(JsonNode.class));
        if (n == null) {
            n = yamlMapper.getNodeFactory().nullNode();
        }
        @SuppressWarnings("unchecked")
        T result = (T) n;
        return result;
    }

    private void _assertNotNull(String parameterName, Object parameterValue) {
        if (parameterValue == null) {
            throw new IllegalArgumentException(parameterName + " should not be null");
        }
    }

    private <T> T _readValue(DeserializationConfig cfg, JsonParser p, com.fasterxml.jackson.databind.JavaType valueType) throws IOException {
        return yamlMapper.readValue(p, valueType);
    }
}
