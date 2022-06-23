package com.meltingb.medicare.utils

import com.google.gson.*
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type

/**
 * Json - ZonedDateTime to DateTime Convert
 */

class DateTimeConverter : JsonDeserializer<DateTime>, JsonSerializer<DateTime> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DateTime {
        return DateTime.parse(json?.asString)
    }

    override fun serialize(src: DateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(ISODateTimeFormat
                .dateTimeNoMillis()
                .print(src))
    }
}