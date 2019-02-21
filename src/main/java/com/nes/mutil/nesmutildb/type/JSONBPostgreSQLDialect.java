package com.nes.mutil.nesmutildb.type;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

public class JSONBPostgreSQLDialect extends PostgreSQL94Dialect {

    public JSONBPostgreSQLDialect() {
        super();
        registerColumnType(Types.JAVA_OBJECT, JSONBUserType.JSONB_TYPE);
        registerColumnType(Types.ARRAY, LongArrayUserType.LONG_ARRAY_TYPE);
        registerColumnType(Types.ARRAY, StringArrayUserType.STRING_ARRAY_TYPE);
    }
}
