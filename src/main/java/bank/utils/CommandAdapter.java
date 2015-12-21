package bank.utils;

import bank.datatypes.Command;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CommandAdapter implements JsonSerializer<Command>, JsonDeserializer<Command> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE  = "INSTANCE";

    @Override
    public Command deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject =  json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();

        Class<?> klass = null;

        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            throw new JsonParseException(e.getMessage());
        }

        return context.deserialize(jsonObject.get(INSTANCE), klass);
    }

    @Override
    public JsonElement serialize(Command src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject retValue = new JsonObject();
        JsonElement elem = context.serialize(src);
        String className = src.getClass().getName();

        retValue.addProperty(CLASSNAME, className);
        retValue.add(INSTANCE, elem);

        return retValue;
    }
}
