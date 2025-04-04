package com.dmdev.v5.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Вместо переопределения всех этих методов, подключим библиотеку hibernate-types
 * и воспользуемся классом JsonBinaryType, где эта работа уже проделана:
 * Type -> SingleColumnType --> AbstractSingleColumnStandardBasicType --> AbstractHibernateType --> JsonBinaryType
 */
public class JsonType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[0];
    }

    @Override
    public Class returnedClass() {
        return null;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return false;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return 0;
    }

    /**
     * Из resultSet получаем объект Json
     *
     * @param rs a JDBC result set
     * @param names the column names
     * @param session
     * @param owner the containing entity  @return Object
     * @return
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return null;
    }

    /**
     * Установить объект Json в PreparedStatement
     *
     * @param st a JDBC prepared statement
     * @param value the object to write
     * @param index statement parameter index
     * @param session
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {

    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return null;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return null;
    }
}
