package cn.hotpot.websocketdispersion.tool;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author qinzhu
 * @since 2020/4/21
 */
@Slf4j
public class JsonUtil {
    private JsonUtil() {
    }

    public static ObjectMapper getInstance() {
        return JsonUtil.JacksonHolder.INSTANCE;
    }

    public static <T> T parse(String content, Class<T> valueType) {
        try {
            return getInstance().readValue(content, valueType);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
            return null;
        }
    }

    /**
     * 将json字符串转换成Map
     *
     * @param content json字符串
     * @return 转换后的结果
     */
    public static Map<String, Object> toMap(String content) {
        try {
            return (Map) getInstance().readValue(content, Map.class);
        } catch (IOException var2) {
            log.error(var2.getMessage(), var2);
            return null;
        }
    }

    /**
     * 把对象转换成json字符串
     *
     * @param value 待转换实体
     */
    public static <T> String toJson(T value) {
        try {
            return getInstance().writeValueAsString(value);
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
            throw new IllegalArgumentException(var2);
        }
    }

    public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
        try {
            if (StringUtils.isEmpty(content) || !content.startsWith("[")) {
                content = "[" + content + "]";
            }

            List<Map<String, Object>> list = (List) getInstance().readValue(content, new TypeReference<List<T>>() {
            });
            List<T> result = new ArrayList<>();
            Iterator var4 = list.iterator();

            while (var4.hasNext()) {
                Map<String, Object> map = (Map) var4.next();
                result.add(toPojo(map, valueTypeRef));
            }

            return result;
        } catch (IOException var6) {
            log.error(var6.getMessage(), var6);
            return null;
        }
    }

    public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
        return getInstance().convertValue(fromValue, toValueType);
    }

    private static class JacksonHolder {
        private static ObjectMapper INSTANCE = new JacksonObjectMapper();

        private JacksonHolder() {
        }
    }

    private static class JacksonObjectMapper extends ObjectMapper {
        private static final long serialVersionUID = 4288193147502386170L;
        private static final Locale CHINA;

        static {
            CHINA = Locale.CHINA;
        }

        public JacksonObjectMapper() {
            super.setLocale(CHINA);
            super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            super.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA));
            super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            super.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            super.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            super.findAndRegisterModules();
            super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            super.getDeserializationConfig().withoutFeatures(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES});
            super.registerModule(new BladeJavaTimeModule());
            super.findAndRegisterModules();
        }

        @Override
        public ObjectMapper copy() {
            return super.copy();
        }
    }

    private static class BladeJavaTimeModule extends SimpleModule {
        public BladeJavaTimeModule() {
            super(PackageVersion.VERSION);
            DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter ymdhms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ymdhms));
            this.addDeserializer(LocalDate.class, new LocalDateDeserializer(ymd));
//            this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeUtil.TIME_FORMAT));
//            this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeUtil.DATETIME_FORMAT));
//            this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeUtil.DATE_FORMAT));
//            this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeUtil.TIME_FORMAT));
        }
    }
}
