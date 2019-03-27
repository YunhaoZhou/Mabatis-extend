package org.github.mybatisextend.mybatis.crud.annotation;

/**
 * @author zhouyh3 on 2019/3/24.
 */
public enum BaseMethod {
    /**
     * The method should be created like
     * @BaseDao(BaseMethod.Query)
     * @Select
     * List<Entity> XXX(@Param("entity")Entity XXX)
     */
    Query,

    /**
     * The method should be created like
     * @BaseDao(BaseMethod.PagedQuery)
     * @Select
     * List<Entity> XXX(@Param("entity")Entity XXX,@Param("pageSize")int pageSize,@Param("pageNum")int pageNum)
     */
    PagedQuery,

    /**
     * The method should be created like
     * @BaseDao(BaseMethod.Insert)
     * @Update
     * int XXX(@Param("entity")Entity XXX)
     */
    Insert,
    /**
     * The method should be created like
     * @BaseDao(BaseMethod.BatchInsert)
     * @Update
     * int XXX(@Param("entity")List<Entity> XXX)
     */
    BatchInsert,

    /**
     * The method should be created like
     * @BaseDao(BaseMethod.Delete)
     * @Update
     * int XXX(@Param("entity")Entity XXX)
     */
    Delete,

    /**
     * The method should be created like
     * @BaseDao(BaseMethod.BatchDelete)
     * @Update
     * int XXX(@Param("entity")Entity XXX)
     */
    BatchDelete,


    /**
     * The method should be created like
     * @BaseDao(BaseMethod.Update)
     * @Update
     * int XXX(@Param("entity")Entity XXX)
     */
    Update
}
