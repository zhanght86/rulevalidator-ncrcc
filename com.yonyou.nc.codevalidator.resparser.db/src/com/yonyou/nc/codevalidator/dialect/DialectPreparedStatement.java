package com.yonyou.nc.codevalidator.dialect;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class DialectPreparedStatement implements PreparedStatement {
	private PreparedStatement realPreparedStatement = null;

	private DialectAdapter adapter = null;

	public DialectPreparedStatement(PreparedStatement ps) {
		this.realPreparedStatement = ps;
	}

	public void setAdapter(DialectAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		ResultSet rs = realPreparedStatement.executeQuery();
		DialectResultSet drs = new DialectResultSet(rs);
		drs.setAdapter(adapter);
		return drs;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		ResultSet rs = realPreparedStatement.executeQuery(sql);
		DialectResultSet drs = new DialectResultSet(rs);
		drs.setAdapter(adapter);
		return drs;
	}

	@Override
	public void addBatch() throws SQLException {
		realPreparedStatement.addBatch();
	}

	@Override
	public void clearParameters() throws SQLException {
		realPreparedStatement.clearParameters();

	}

	@Override
	public boolean execute() throws SQLException {
		return realPreparedStatement.execute();
	}

	@Override
	public int executeUpdate() throws SQLException {
		return realPreparedStatement.executeUpdate();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return realPreparedStatement.getMetaData();
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return realPreparedStatement.getParameterMetaData();
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		realPreparedStatement.setArray(parameterIndex, x);

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		realPreparedStatement.setAsciiStream(parameterIndex, x);

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		realPreparedStatement.setAsciiStream(parameterIndex, x, length);

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		realPreparedStatement.setAsciiStream(parameterIndex, x, length);

	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		realPreparedStatement.setBigDecimal(parameterIndex, x);

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		realPreparedStatement.setBinaryStream(parameterIndex, x);

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		realPreparedStatement.setBinaryStream(parameterIndex, x, length);

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		realPreparedStatement.setBinaryStream(parameterIndex, x, length);

	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		realPreparedStatement.setBlob(parameterIndex, x);

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		realPreparedStatement.setBlob(parameterIndex, inputStream);

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		realPreparedStatement.setBlob(parameterIndex, inputStream, length);

	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		realPreparedStatement.setBoolean(parameterIndex, x);

	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		realPreparedStatement.setByte(parameterIndex, x);

	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		realPreparedStatement.setBytes(parameterIndex, x);

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		realPreparedStatement.setCharacterStream(parameterIndex, reader);

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		realPreparedStatement
				.setCharacterStream(parameterIndex, reader, length);

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		realPreparedStatement
				.setCharacterStream(parameterIndex, reader, length);

	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		realPreparedStatement.setClob(parameterIndex, x);

	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		realPreparedStatement.setClob(parameterIndex, reader);

	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		realPreparedStatement.setClob(parameterIndex, reader, length);

	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		realPreparedStatement.setDate(parameterIndex, x);

	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		realPreparedStatement.setDate(parameterIndex, x, cal);

	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		realPreparedStatement.setDouble(parameterIndex, x);

	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		realPreparedStatement.setFloat(parameterIndex, x);

	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		realPreparedStatement.setInt(parameterIndex, x);

	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		realPreparedStatement.setLong(parameterIndex, x);

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		realPreparedStatement.setNCharacterStream(parameterIndex, value);

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		realPreparedStatement
				.setNCharacterStream(parameterIndex, value, length);

	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		realPreparedStatement.setNClob(parameterIndex, value);

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		realPreparedStatement.setNClob(parameterIndex, reader);

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		realPreparedStatement.setNClob(parameterIndex, reader, length);

	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		realPreparedStatement.setNString(parameterIndex, value);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		realPreparedStatement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		realPreparedStatement.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		realPreparedStatement.setObject(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		realPreparedStatement.setObject(parameterIndex, x, targetSqlType);

	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		realPreparedStatement.setObject(parameterIndex, x, targetSqlType,
				scaleOrLength);

	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		realPreparedStatement.setRef(parameterIndex, x);

	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		realPreparedStatement.setRowId(parameterIndex, x);

	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		realPreparedStatement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		realPreparedStatement.setShort(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		realPreparedStatement.setString(parameterIndex, x);

	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		realPreparedStatement.setTime(parameterIndex, x);

	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		realPreparedStatement.setTime(parameterIndex, x, cal);

	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		realPreparedStatement.setTimestamp(parameterIndex, x);

	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		realPreparedStatement.setTimestamp(parameterIndex, x, cal);

	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		realPreparedStatement.setURL(parameterIndex, x);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		realPreparedStatement.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		realPreparedStatement.addBatch(sql);
	}

	@Override
	public void cancel() throws SQLException {
		realPreparedStatement.cancel();
	}

	@Override
	public void clearBatch() throws SQLException {
		realPreparedStatement.clearBatch();
	}

	@Override
	public void clearWarnings() throws SQLException {
		realPreparedStatement.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		realPreparedStatement.close();

	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return realPreparedStatement.execute(sql);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		return realPreparedStatement.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return realPreparedStatement.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		return realPreparedStatement.execute(sql, columnNames);
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return realPreparedStatement.executeBatch();
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return realPreparedStatement.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		return realPreparedStatement.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		return realPreparedStatement.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		return realPreparedStatement.executeUpdate(sql, columnNames);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return realPreparedStatement.getConnection();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return realPreparedStatement.getFetchDirection();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return realPreparedStatement.getFetchSize();
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		ResultSet rs = realPreparedStatement.getGeneratedKeys();
		return new DialectResultSet(rs);
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return realPreparedStatement.getMaxFieldSize();
	}

	@Override
	public int getMaxRows() throws SQLException {
		return realPreparedStatement.getMaxRows();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return realPreparedStatement.getMoreResults();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return realPreparedStatement.getMoreResults(current);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return realPreparedStatement.getQueryTimeout();
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		ResultSet rs = realPreparedStatement.getResultSet();
		return new DialectResultSet(rs);
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return realPreparedStatement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return realPreparedStatement.getResultSetHoldability();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return realPreparedStatement.getResultSetType();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return realPreparedStatement.getUpdateCount();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return realPreparedStatement.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return realPreparedStatement.isClosed();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return realPreparedStatement.isPoolable();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		realPreparedStatement.setCursorName(name);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		realPreparedStatement.setEscapeProcessing(enable);

	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		realPreparedStatement.setFetchDirection(direction);

	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		realPreparedStatement.setFetchSize(rows);

	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		realPreparedStatement.setMaxFieldSize(max);

	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		realPreparedStatement.setMaxRows(max);

	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		realPreparedStatement.setPoolable(poolable);

	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		realPreparedStatement.setQueryTimeout(seconds);

	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return realPreparedStatement.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return realPreparedStatement.unwrap(iface);
	}

}