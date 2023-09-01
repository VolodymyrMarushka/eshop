package eshop.core.starter.adapter.cache.serializer;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonParser;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class JsonBeanRedisSerializer implements RedisSerializer<JsonBean> {

  private final Charset charset;

  public static final JsonBeanRedisSerializer UTF_8 = new JsonBeanRedisSerializer(StandardCharsets.UTF_8);

  public JsonBeanRedisSerializer() {
    this(StandardCharsets.UTF_8);
  }

  public JsonBeanRedisSerializer(Charset charset) {
    Assert.notNull(charset, "Charset must not be null!");
    this.charset = charset;
  }

  @Override
  public byte[] serialize(JsonBean jsonBean) throws SerializationException {
    return jsonBean == null ? null : jsonBean.asString().getBytes();
  }

  @Override
  public JsonBean deserialize(@Nullable byte[] bytes) {
    return bytes == null ? null : JsonParser.stringToEntity(String.valueOf(bytes), JsonBean::new);
  }

}
