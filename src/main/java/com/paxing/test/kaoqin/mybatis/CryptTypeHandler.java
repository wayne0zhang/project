package com.paxing.test.kaoqin.mybatis;

import com.paxing.test.kaoqin.utils.AESUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 需要对入库 & 查询字段做加密的类型
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/27
 */
public class CryptTypeHandler extends BaseTypeHandler<String> {

    /**
     * 加密后的密文最短长度，用于判断是否加密
     */
    private static final int LENGTH = 32;

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String parameter, JdbcType jdbcType) throws SQLException {
        String encryptedString = AESUtil.encrypt(parameter);
        preparedStatement.setString(i, encryptedString);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String value = resultSet.getString(columnName);
        if (StringUtils.isBlank(value) || value.trim().length() < LENGTH) {
            return value;
        }
        return AESUtil.decrypt(value);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String value = resultSet.getString(columnIndex);
        if (StringUtils.isBlank(value) || value.trim().length() < LENGTH) {
            return value;
        }
        return AESUtil.decrypt(value);    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String value = callableStatement.getString(columnIndex);
        if (StringUtils.isBlank(value) || value.trim().length() < LENGTH) {
            return value;
        }
        return AESUtil.decrypt(value);    }
}
