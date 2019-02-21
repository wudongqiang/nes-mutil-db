package com.nes.mutil.nesmutildb.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Objects;

public class LongArrayUserType implements UserType {

    public static final String LONG_ARRAY_TYPE = "Long[]";

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Long[]) this.deepCopy(value);
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {

        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);
        if (array != null) {
            Long[] javaArray = (Long[]) array.getArray();
            return javaArray;
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
//        Connection connection = preparedStatement.getConnection();
        Long[] castObject = Objects.nonNull(value) ? (Long[]) value : new Long[]{};
        Array array = st.getConnection().createArrayOf("bigint", castObject);
        st.setArray(index, array);
//        connection.close();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    @Override
    public Class<Long[]> returnedClass() {
        return Long[].class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.ARRAY};
    }
}