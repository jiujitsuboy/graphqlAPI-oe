package com.openenglish.hr.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.junit.Before;

import java.lang.reflect.Type;
import java.util.Date;

public abstract class BaseControllerTest {

  protected Gson gson;

  @Before
  public void setup(){
    gson = gson();
  }

  private static class DateTimeTypeConverter
      implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public JsonElement serialize(Date src, Type srcType, JsonSerializationContext context) {
      if (src == null) {
        return null;
      }
      return new JsonPrimitive(src.getTime());
    }

    @Override
    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      try {
        if (json == null) {
          return null;
        }
        return new Date(json.getAsLong());
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  public Gson gson() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Date.class, new DateTimeTypeConverter());

    Gson gson = builder.create();
    return gson;
  }
}
