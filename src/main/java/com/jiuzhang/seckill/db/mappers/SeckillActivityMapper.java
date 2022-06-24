package com.jiuzhang.seckill.db.mappers;

import com.jiuzhang.seckill.db.po.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeckillActivityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SeckillActivity record);

    int insertSelective(SeckillActivity record);

    SeckillActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SeckillActivity record);

    int updateByPrimaryKey(SeckillActivity record);

    List<SeckillActivity> querySeckillActivitysByStatus(int activityStatus);

    int lockStock(Long seckillActivityId);

    int deductStock(Long seckillActivityId);

    void revertStock(Long seckillActivityId);
}