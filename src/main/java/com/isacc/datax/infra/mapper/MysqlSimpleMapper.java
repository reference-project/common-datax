package com.isacc.datax.infra.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.isacc.datax.domain.entity.reader.hdfsreader.HdfsColumn;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mysql Simple Mapper
 * </p>
 *
 * @author isacc 2019/04/29 20:24
 */
@SuppressWarnings("unused")
@DS("mysql")
public interface MysqlSimpleMapper {

    /**
     * 是否存在数据库
     *
     * @param databaseName Mysql数据库名称
     * @return java.lang.Integer
     * @author isacc 2019-04-29 20:09
     */
    @Select("SELECT COUNT(*) FROM information_schema.SCHEMATA " +
            "WHERE SCHEMA_NAME = #{databaseName}")
    Integer mysqlDbIsExist(String databaseName);

    /**
     * 根据mysql数据库和表名查询表字段相关信息
     *
     * @param databaseName Mysql数据库名称
     * @param tableName    Mysql表名称
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author isacc 2019/5/10 14:33
     */
    @Select("SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE, COLUMN_TYPE " +
            "FROM information_schema. COLUMNS " +
            "WHERE " +
            "TABLE_SCHEMA = #{databaseName} AND TABLE_NAME = #{tableName}")
    List<Map<String, Object>> mysqlTableColumnInfo(String databaseName, String tableName);

    /**
     * mysql表字段映射hive表字段
     *
     * @param databaseName Mysql数据库名称
     * @param tableName    Mysql表名称
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author isacc 2019/5/10 14:33
     */
    @Select("SELECT " +
            "column_name name, " +
            "CASE " +
            "WHEN NUMERIC_PRECISION IS NOT NULL " +
            "AND (" +
            "data_type = 'decimal' " +
            "OR data_type = 'numeric' " +
            ") THEN " +
            "concat( " +
            "'decimal(', " +
            "NUMERIC_PRECISION, " +
            "',', " +
            "NUMERIC_SCALE, " +
            "')' " +
            ") " +
            "WHEN (" +
            "CHARACTER_MAXIMUM_LENGTH IS NOT NULL " +
            "OR data_type = 'uniqueidentifier' " +
            ") " +
            "AND data_type NOT LIKE '%text%' THEN " +
            "'string' " +
            "WHEN data_type = 'datetime' THEN " +
            "'timestamp' " +
            "WHEN data_type = 'money' THEN " +
            "'decimal(9,2)' " +
            "WHEN data_type = 'tinyint' THEN " +
            "'int' " +
            "ELSE " +
            "data_type " +
            "END AS type " +
            "FROM " +
            "information_schema. COLUMNS " +
            "WHERE " +
            "table_schema = #{databaseName} " +
            "AND table_name = #{tableName}")
    List<HdfsColumn> mysqlColumn2HiveColumn(String databaseName, String tableName);

    /**
     * 判断指定数据库下指定表是否存在
     *
     * @param databaseName Mysql数据库名称
     * @param tableName    Mysql表名称
     * @return java.lang.Integer
     * @author isacc 2019-04-29 21:20
     */
    @Select("SELECT count(*) FROM information_schema.TABLES " +
            "WHERE table_schema=#{databaseName} and table_name=#{tableName}")
    Integer mysqlTblIsExist(String databaseName, String tableName);

    /**
     * 查询Hive所有数据库
     *
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author isacc 2019-04-30 10:48
     */
    @DS("mysql_hivemeta")
    @Select("select DB_ID,`DESC`,DB_LOCATION_URI,`NAME`,OWNER_NAME,OWNER_TYPE from dbs")
    @ResultType(Map.class)
    List<Map<String, Object>> allHiveDatabases();

    /**
     * 根据Hive数据库名称查看是否存在该数据库
     *
     * @param hiveDbName hive db name
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author isacc 2019-05-07 15:54
     */
    @DS("mysql_hivemeta")
    @Select("select DB_ID,`DESC`,DB_LOCATION_URI,`NAME`,OWNER_NAME,OWNER_TYPE from dbs " +
            "where `NAME` = #{hiveDbName}")
    @ResultType(Map.class)
    Map<String, Object> hiveDbIsExist(String hiveDbName);

    /**
     * 根据Hive数据库ID查看是否存在该表
     *
     * @param dbId        hive数据库id
     * @param hiveTblName hive数据表名称
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author isacc 2019-05-07 16:08
     */
    @DS("mysql_hivemeta")
    @Select("SELECT " +
            "TBL_ID,CREATE_TIME,DB_ID LAST_ACCESS_TIME,OWNER RETENTION,SD_ID,TBL_NAME,TBL_TYPE,VIEW_EXPANDED_TEXT,VIEW_ORIGINAL_TEXT,IS_REWRITE_ENABLED " +
            "FROM " +
            "tbls " +
            "WHERE " +
            "DB_ID = #{dbId} " +
            "AND TBL_NAME = #{hiveTblName}")
    @ResultType(Map.class)
    Map<String, Object> hiveTblIsExist(Long dbId, String hiveTblName);

    /**
     * 查询指定Hive数据库下所有表
     *
     * @param dbId Hive数据库ID
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author isacc 2019-04-30 10:54
     */
    @DS("mysql_hivemeta")
    @Select("SELECT " +
            "TBL_ID,CREATE_TIME,DB_ID LAST_ACCESS_TIME,OWNER RETENTION,SD_ID,TBL_NAME,TBL_TYPE,VIEW_EXPANDED_TEXT,VIEW_ORIGINAL_TEXT,IS_REWRITE_ENABLED " +
            "FROM " +
            "tbls " +
            "WHERE " +
            "DB_ID = #{dbId}")
    @ResultType(Map.class)
    List<Map<String, Object>> allHiveTableByDatabase(Long dbId);

}
