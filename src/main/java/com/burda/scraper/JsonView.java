package com.burda.scraper;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

public class JsonView {

  public static void render(Object model, HttpServletResponse response)
  {
      MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();

      MediaType jsonMimeType = MediaType.APPLICATION_JSON;


      try {
          jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));
      } catch (HttpMessageNotWritableException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}
