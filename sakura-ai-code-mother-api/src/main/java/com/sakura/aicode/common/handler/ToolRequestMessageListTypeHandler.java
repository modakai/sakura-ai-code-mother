package com.sakura.aicode.common.handler;

import com.sakura.aicode.module.ai.core.model.message.ToolRequestMessage;
import com.sakura.aicode.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 自定义 TypeHandler
 */
@MappedTypes(List.class)  // 映射的 Java 类型
@MappedJdbcTypes(JdbcType.LONGVARCHAR)
public class ToolRequestMessageListTypeHandler extends BaseTypeHandler<List<ToolRequestMessage>> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<ToolRequestMessage> parameter, JdbcType jdbcType) throws SQLException {
        // 将 List<String> 序列化为 JSON 字符串，设置到 PreparedStatement
        String json = null;
        try {
            json = JsonUtils.toJson(parameter);
        } catch (Exception e) {
            throw new SQLException("Failed to serialize List<String> to JSON: " + e.getMessage());
        }
        ps.setString(i, json);

        ps.setObject(i, json, JdbcType.LONGVARCHAR.TYPE_CODE);
    }

    @Override
    public List<ToolRequestMessage> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public List<ToolRequestMessage> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public List<ToolRequestMessage> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    // 解析 JSON 字符串为 List<String>
    private List<ToolRequestMessage> parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JsonUtils.fromJsonToList(json, ToolRequestMessage.class);
        } catch (Exception e) {
            throw new SQLException("Failed to deserialize JSON to List<String>: " + e.getMessage());
        }
    }
}