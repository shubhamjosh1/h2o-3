package water.automl.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import water.api.Handler;
import water.automl.api.schemas3.AutoMLJSONSchemaV99;
import water.automl.api.schemas3.AutoMLBuildSpecV99;


public class AutoMLJSONSchemaHandler extends Handler {
  @SuppressWarnings("unused") // called through reflection by RequestServer
  public AutoMLJSONSchemaV99 getJSONSchema(int version, AutoMLJSONSchemaV99 args) {

    Class clazz = AutoMLBuildSpecV99.class;
    ObjectMapper m = new ObjectMapper();

    SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
    try {
      m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
    } catch (JsonMappingException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    com.fasterxml.jackson.module.jsonSchema.JsonSchema jsonSchema = visitor.finalSchema();
    ((ObjectSchema) jsonSchema).getProperties().remove("job");  // heh, get at and mutate the private field :)
    ((ObjectSchema) jsonSchema).getProperties().remove("implClass");
    ((ObjectSchema) jsonSchema).getProperties().remove("_exclude_fields");
    ((ObjectSchema) jsonSchema).getProperties().remove("schemaVersion");

    try {
      args.json_schema = m.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return args;
  }
}